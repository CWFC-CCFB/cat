/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2010-2013 Mathieu Fortin AgroParisTech/INRA UMR LERFoB, 
 *				2019-2020 Mathieu Fortin Canadian Forest Service
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed with the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * Please see the license at http://www.gnu.org/copyleft/lesser.html.
 */
package lerfob.carbonbalancetool;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;

import lerfob.carbonbalancetool.CATCompartment.CompartmentInfo;
import lerfob.carbonbalancetool.CATSettings.CATSpecies;
import lerfob.carbonbalancetool.productionlines.CarbonUnit;
import lerfob.carbonbalancetool.productionlines.CarbonUnit.CarbonUnitStatus;
import lerfob.carbonbalancetool.productionlines.CarbonUnitList;
import lerfob.carbonbalancetool.productionlines.EndUseWoodProductCarbonUnit;
import lerfob.carbonbalancetool.productionlines.ProductionLineManager;
import lerfob.carbonbalancetool.productionlines.ProductionProcessorManager;
import lerfob.carbonbalancetool.sensitivityanalysis.CATSensitivityAnalysisSettings;
import repicea.simulation.ApplicationScaleProvider.ApplicationScale;
import repicea.simulation.HierarchicalLevel;
import repicea.simulation.MonteCarloSimulationCompliantObject;
import repicea.simulation.covariateproviders.plotlevel.ManagementTypeProvider.ManagementType;
import repicea.simulation.covariateproviders.plotlevel.StochasticInformationProvider;
import repicea.simulation.covariateproviders.treelevel.SamplingUnitIDProvider;
import repicea.simulation.covariateproviders.treelevel.TreeStatusProvider.StatusClass;
import repicea.util.REpiceaLogManager;
import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.TextableEnum;

@SuppressWarnings("deprecation")
public class CATCompartmentManager implements MonteCarloSimulationCompliantObject {

	private static enum MessageID implements TextableEnum {
		UnevenAgedWarning("Carbon balance calculation under infinite sequence will be allowed. Its validity will depend on how similar the initial and final stands of the simulation are.",
				"Le calcul du bilan de carbon en s\u00E9quence infinie sera possible. La validit\u00E9 de ce calcul d\u00E9pendra toutefois de la ressemblance entre les peuplements initial et final de la simulation."),
		;

		MessageID(String englishText, String frenchText) {
			setText(englishText, frenchText);
		}
		
		@Override
		public void setText(String englishText, String frenchText) {	
			REpiceaTranslator.setString(this, englishText, frenchText);
		}
		
		@Override
		public String toString() {return REpiceaTranslator.getString(this);}
		
	}
	
	
	private static int NumberOfExtraYrs = 80;	// number of years after the final cut

	private final Map<StatusClass, Map<CATCompatibleStand, Map<String, Map<String, Collection<CATCompatibleTree>>>>> treeCollections;
	private final Map<CATCompatibleTree, CATCompatibleStand> treeRegister;
	private final List<CATSpecies> speciesList;

	private List<CATCompatibleStand> stands;
	private CATSettings carbonAccountingToolSettings;		// reference to the extractor settings
	
	private Map<CompartmentInfo, CATCompartment> carbonCompartments;
	private int rotationLength;
	private boolean isInfiniteSequenceAllowed;
	private CATTimeTable timeTable;

	private boolean isSimulationValid;
	private int nbSimulations = 0;
	private final CarbonAccountingTool caller;
	private ManagementType managementType;
	
	protected CATSingleSimulationResult summary;
	
	
	/**
	 * Constructor for this class
	 * @param settings a CATSettings instance
	 */
	protected CATCompartmentManager(CarbonAccountingTool caller, CATSettings settings) {
		this.caller = caller;
		treeCollections = new HashMap<StatusClass, Map<CATCompatibleStand, Map<String, Map<String, Collection<CATCompatibleTree>>>>>();
		treeRegister = new HashMap<CATCompatibleTree, CATCompatibleStand>();
		speciesList = new ArrayList<CATSpecies>();
		
		this.carbonAccountingToolSettings = settings;
		this.carbonCompartments = new TreeMap<CompartmentInfo, CATCompartment>();	// TreeMap to make sure the merge compartments are not called before the regular compartment
		isSimulationValid = false;
		initializeCompartments();
	}
		
	protected void registerTree(StatusClass statusClass, CATCompatibleStand stand, CATCompatibleTree tree) {
		if (!treeCollections.containsKey(statusClass)) {
			treeCollections.put(statusClass, new HashMap<CATCompatibleStand, Map<String, Map<String, Collection<CATCompatibleTree>>>>());
		}
		Map<CATCompatibleStand, Map<String, Map<String, Collection<CATCompatibleTree>>>> innerMap = treeCollections.get(statusClass);
		if (!innerMap.containsKey(stand)) {
			innerMap.put(stand, new HashMap<String, Map<String, Collection<CATCompatibleTree>>>());
		}
		
		Map<String, Map<String, Collection<CATCompatibleTree>>> innerInnerMap = innerMap.get(stand);
		
		String samplingUnitID;
		if (tree instanceof SamplingUnitIDProvider) {
			samplingUnitID = ((SamplingUnitIDProvider) tree).getSamplingUnitID(); 
		} else {
			samplingUnitID = "";
		}
		if (!innerInnerMap.containsKey(samplingUnitID)) {
			innerInnerMap.put(samplingUnitID, new HashMap<String, Collection<CATCompatibleTree>>());
		}
		
		Map<String, Collection<CATCompatibleTree>> mostInsideMap = innerInnerMap.get(samplingUnitID);
		if (!mostInsideMap.containsKey(tree.getSpeciesName())) {
			mostInsideMap.put(tree.getSpeciesName(), new ArrayList<CATCompatibleTree>());
		}
		
		Collection<CATCompatibleTree> trees = mostInsideMap.get(tree.getSpeciesName());
		trees.add(tree);
		treeRegister.put(tree, stand);
	}

	
	int getDateIndexForThisHarvestedTree(CATCompatibleTree tree) {
		if (treeRegister.containsKey(tree)) {
			CATCompatibleStand stand = treeRegister.get(tree);
			return getTimeTable().getIndexOfThisStandOnTheTimeTable(stand);
		} else {
			return -1;
		}
	}
	
	int getDateIndexOfPreviousStandForThisHarvestedTree(CATCompatibleTree tree) {
		if (treeRegister.containsKey(tree)) {
			CATCompatibleStand stand = treeRegister.get(tree);
			int currentIndexOfThisStandAmongStands = getTimeTable().getStandsForThisRealization().lastIndexOf(stand);
			if (currentIndexOfThisStandAmongStands > 0) {	// must be at least in the second slot
				stand = getTimeTable().getStandsForThisRealization().get(currentIndexOfThisStandAmongStands - 1); // get the previous stand
				return getTimeTable().getIndexOfThisStandOnTheTimeTable(stand);
			}
		} 
		return -1;
	}
	

	protected Map<CATCompatibleStand, Map<String, Map<String, Collection<CATCompatibleTree>>>> getTrees(StatusClass statusClass) {
		if (treeCollections.containsKey(statusClass)) {
			return treeCollections.get(statusClass);
		} else {
			return new HashMap<CATCompatibleStand, Map<String, Map<String, Collection<CATCompatibleTree>>>>();
		}
	}

	private void clearTreeCollections() {
		treeCollections.clear();
		treeRegister.clear();
		speciesList.clear();
	}

	protected List<CATSpecies> getSpeciesList() {
		return speciesList;
	}

	protected void registerTreeSpecies(CATCompatibleTree tree) {
		if (!speciesList.contains(tree.getCATSpecies())) {
			speciesList.add(tree.getCATSpecies());
		}
	}

	protected void setSimulationValid(boolean isSimulationValid) {
		this.isSimulationValid = isSimulationValid;
	}
	
	private boolean canBeRunInInfiniteSequence(CATCompatibleStand lastStand, int nRealizations) {
		if (lastStand.getApplicationScale() == ApplicationScale.Stand && nRealizations == 1) {	// we can hardly deal with multiple realizations in an infinite sequence because the stand may be ready for final harvesting in one realization but not in the others
			if (lastStand.getManagementType() == ManagementType.EvenAged) {
				return true; 
			} else { // then uneven-aged management type
				if (lastStand.isInterventionResult()) { // but last stand has been harvested; 
					REpiceaLogManager.logMessage(CarbonAccountingTool.LOGGER_NAME, Level.WARNING, null, MessageID.UnevenAgedWarning.toString());
					if (caller.isGuiEnabled()) {
						caller.getUI().displayWarningMessage(MessageID.UnevenAgedWarning.toString());
					}
					return true;
				}
			}
		}
		return false; // any other case
	}
	
	/**
	 * Initialize the carbon balance simulation. <p>
	 * This method determines <p>
	 * <ul>
	 * <li> the management type (even aged or uneven aged)
	 * <li> the rotation length (or cutting cycle in case of uneven-aged management)
	 * <li> whether the simulation can be run in infinite sequence
	 * <li> the number of Monte Carlo realizations (set to 1 if the model is deterministic)
	 * </ul> 
	 * @param stands a List of CATCompatibleStand instances
	 */
	public void init(List<CATCompatibleStand> stands) {
		this.stands = stands;
		if (stands != null) {
			CATCompatibleStand lastStand = stands.get(stands.size() - 1);
			managementType = lastStand.getManagementType();
			int nRealizations = getNumberOfRealizations(lastStand);
			isInfiniteSequenceAllowed = canBeRunInInfiniteSequence(lastStand, nRealizations);
			boolean isStochastic = isStochastic(lastStand);
			CATSensitivityAnalysisSettings.getInstance().setModelStochastic(isStochastic);
			CATSensitivityAnalysisSettings.getInstance().setNumberOfMonteCarloRealizations(nRealizations);
			int nbExtraYears = 0;
			int initialAgeYr = -999;
			if (isInfiniteSequenceAllowed && lastStand.getManagementType() == ManagementType.EvenAged) {
				if (!lastStand.getTrees(StatusClass.alive).isEmpty()) { // even aged but the last stand has not been fully harvested
					CATCompatibleStand stand = lastStand.getHarvestedStand();
					stands.add(stand);
					lastStand = stand;
					caller.setFinalCutHadToBeCarriedOut(true);
				} 					
				rotationLength = lastStand.getAgeYr();
				initialAgeYr = stands.get(0).getAgeYr();
				nbExtraYears = NumberOfExtraYrs;
			} else { // infinite sequence might be allowed but management is uneven-aged
				rotationLength = lastStand.getDateYr() - stands.get(0).getDateYr();
			}
				
			timeTable = new CATTimeTable(stands, initialAgeYr, nbExtraYears);
		}
	}
	
	
	/**
	 * Check if the stand implements Monte Carlo features and retrieve the number of Monte Carlo 
	 * realizations.  
	 * @return the number of Monte Carlo realizations or 1 if either the stand does not implement
	 * Monte Carlo feature or these are not compatible
	 */
	private int getNumberOfRealizations(CATCompatibleStand stand) {
		if (stand instanceof StochasticInformationProvider) {
			StochasticInformationProvider<?> stochProv = (StochasticInformationProvider<?>) stand;
			List<Integer> monteCarloIds = stochProv.getRealizationIds();
			if (stochProv.isStochastic() && stochProv.getRealization(monteCarloIds.get(0)) instanceof CATCompatibleStand) {
				return monteCarloIds.size();
			}
		} 
		return 1;
	}

	private boolean isStochastic(CATCompatibleStand stand) {
		if (stand instanceof StochasticInformationProvider) {
			StochasticInformationProvider<?> stochProv = (StochasticInformationProvider<?>) stand;
			List<Integer> monteCarloIds = stochProv.getRealizationIds();
			if (stochProv.isStochastic() && stochProv.getRealization(monteCarloIds.get(0)) instanceof CATCompatibleStand) {
				return true;
			}
		} 
		return false;
	}

	
	
	protected void resetManager() {
		clearTreeCollections();
		resetCompartments();
		if (getCarbonToolSettings().formerImplementation) {
			ProductionLineManager productionLines = carbonAccountingToolSettings.getProductionLines();
			productionLines.resetCarbonUnitMap();
		} else {
			getCarbonToolSettings().getCurrentProductionProcessorManager().resetCarbonUnitMap();
		}
	}

//	/**
//	 * This method provides the duration of the time step
//	 * @param steps a Vector of Step instances
//	 * @return an integer 
//	 */
//	private int retrieveAverageTimeStep(List<CATCompatibleStand> stands) {
//		double averageTimeStep = 0;		// default time step
//		int nbHits = 0;
//		int date;
//		int formerDate;
//		for (int i = 1; i < stands.size(); i++) {
//			date = stands.get(i).getDateYr();
//			formerDate = stands.get(i-1).getDateYr();
//			if (date - formerDate > 0) {
//				averageTimeStep += date - formerDate;
//				nbHits++;
//			}
//		}
//		return (int) Math.round(averageTimeStep / nbHits);
//	}
	
	/**
	 * This method returns the TimeScale instance the simulation has been run with.
	 * @return a CATTimeTable instance
	 */
	public CATTimeTable getTimeTable() {return timeTable;}
	
	public CATSettings getCarbonToolSettings() {return carbonAccountingToolSettings;}

	@SuppressWarnings({ "unchecked"})
	protected void resetCompartmentsAndSetCarbonUnitCollections() {
		if (getCarbonToolSettings().isVerboseEnabled()) {
			System.out.println("Resetting compartment...");
		}
		CarbonUnitList joinEndUseProductRecyclageList = new CarbonUnitList();
		CarbonUnitList leftInForestList;
		CarbonUnitList degradableLandfillList;
		CarbonUnitList nonDegradableLandfillList;
		if 	(!getCarbonToolSettings().formerImplementation) {
			ProductionProcessorManager productionLineManager = getCarbonToolSettings().getCurrentProductionProcessorManager();
			joinEndUseProductRecyclageList.addAll(productionLineManager.getCarbonUnits(CarbonUnitStatus.EndUseWoodProduct));
			joinEndUseProductRecyclageList.addAll(productionLineManager.getCarbonUnits(CarbonUnitStatus.Recycled));
			leftInForestList = productionLineManager.getCarbonUnits(CarbonUnitStatus.DeadWood);
			degradableLandfillList = productionLineManager.getCarbonUnits(CarbonUnitStatus.LandFillDegradable);
			nonDegradableLandfillList = productionLineManager.getCarbonUnits(CarbonUnitStatus.LandFillNonDegradable);
		} else {
			ProductionLineManager productionLineManager = getCarbonToolSettings().getProductionLines();
			joinEndUseProductRecyclageList.addAll(productionLineManager.getCarbonUnits(CarbonUnitStatus.EndUseWoodProduct));
			joinEndUseProductRecyclageList.addAll(productionLineManager.getCarbonUnits(CarbonUnitStatus.Recycled));
			leftInForestList = productionLineManager.getCarbonUnits(CarbonUnitStatus.DeadWood);
			degradableLandfillList = productionLineManager.getCarbonUnits(CarbonUnitStatus.LandFillDegradable);
			nonDegradableLandfillList = productionLineManager.getCarbonUnits(CarbonUnitStatus.LandFillNonDegradable);
			
		}
		Collection<EndUseWoodProductCarbonUnit>[] endUseWoodProductCarbonUnits = (Collection<EndUseWoodProductCarbonUnit>[]) formatCarbonUnits(getTimeTable(), joinEndUseProductRecyclageList);
		Collection<CarbonUnit>[] leftInForestCarbonUnits = formatCarbonUnits(getTimeTable(), leftInForestList);
		Collection<CarbonUnit>[] degradableLandfillCarbonUnits = formatCarbonUnits(getTimeTable(), degradableLandfillList);
		Collection<CarbonUnit>[] nonDegradableLandfillCarbonUnits = formatCarbonUnits(getTimeTable(), nonDegradableLandfillList);


		resetCompartments();

		carbonCompartments.get(CompartmentInfo.CarbEmis).setCarbonUnitsArray(endUseWoodProductCarbonUnits);
		carbonCompartments.get(CompartmentInfo.EnerSubs).setCarbonUnitsArray(endUseWoodProductCarbonUnits);
		carbonCompartments.get(CompartmentInfo.Products).setCarbonUnitsArray(endUseWoodProductCarbonUnits);
		carbonCompartments.get(CompartmentInfo.WComb).setCarbonUnitsArray(endUseWoodProductCarbonUnits);

		// dead biomass
		carbonCompartments.get(CompartmentInfo.DeadBiom).setCarbonUnitsArray(leftInForestCarbonUnits);
	
		// landfill wood products
		carbonCompartments.get(CompartmentInfo.LfillDeg).setCarbonUnitsArray(degradableLandfillCarbonUnits);
		carbonCompartments.get(CompartmentInfo.LfillEm).setCarbonUnitsArray(degradableLandfillCarbonUnits);
		carbonCompartments.get(CompartmentInfo.LfillND).setCarbonUnitsArray(nonDegradableLandfillCarbonUnits);
	}
	
	
	/**
	 * This method is called just before calculating the carbon in the compartments. It deletes all the carbon values in the different compartments.
	 */
	private void resetCompartments() {
		for (CATCompartment compartment : this.carbonCompartments.values()) {
			compartment.resetCarbon();
		}
	}
	

	/**
	 * This method initializes the different carbon compartments.
	 * @throws Exception
	 */
	private void initializeCompartments() {
		for (CompartmentInfo compartmentInfo : CompartmentInfo.values()) {
			switch (compartmentInfo) {
			case Roots:
			case AbGround:
			case DeadBiom:
			case LfillDeg:
			case LfillND:
			case LfillEm:
				carbonCompartments.put(compartmentInfo,	new CATCompartment(this, compartmentInfo));
				break;
			case CarbEmis:
			case Products:
			case EnerSubs:
			case WComb:
				carbonCompartments.put(compartmentInfo, new CATProductCompartment(this, compartmentInfo));
				break;
			case LivingBiomass:
				CATCompartment standing = new CATCompartment(this, compartmentInfo);
				standing.addFatherCompartment(carbonCompartments.get(CompartmentInfo.AbGround));
				standing.addFatherCompartment(carbonCompartments.get(CompartmentInfo.Roots));
				this.carbonCompartments.put(compartmentInfo, standing);
				break;
			case TotalProducts:
				CATCompartment overallNet = new CATCompartment(this, compartmentInfo);
				overallNet.addFatherCompartment(carbonCompartments.get(CompartmentInfo.Products));
				overallNet.addFatherCompartment(carbonCompartments.get(CompartmentInfo.LfillDeg));
				this.carbonCompartments.put(compartmentInfo, overallNet);
				break;
			case NetSubs:
				CATCompartment substitutionNet = new CATCompartment(this, compartmentInfo);
				substitutionNet.addFatherCompartment(carbonCompartments.get(CompartmentInfo.EnerSubs));
				substitutionNet.addFatherCompartment(carbonCompartments.get(CompartmentInfo.CarbEmis));
				substitutionNet.addFatherCompartment(carbonCompartments.get(CompartmentInfo.LfillEm));
				substitutionNet.addFatherCompartment(carbonCompartments.get(CompartmentInfo.LfillND));
				this.carbonCompartments.put(compartmentInfo, substitutionNet);
				break;
			}
		}

	}
	

	public Map<CompartmentInfo, CATCompartment> getCompartments() {return this.carbonCompartments;}

	/**
	 * This method returns the last stand from the list of stands. 
	 * @return a CarbonToolCompatibleStand or null if the list is null or empty
	 */
	public CATCompatibleStand getLastStand() {
		if (getStandList() != null && !getStandList().isEmpty()) {
			return getStandList().get(getStandList().size() - 1);
		} else {
			return null;
		}
	}

	
	/**
	 * Return the list of stands as set by the init method. This method should not be called within
	 * the realization because the stands refer to wrappers that contain all the realizations. 
	 * Within the realization, the method CATTimeTable.getStandsForThisRealization should be called 
	 * instead.
	 * @return a List of CATCompatibleStand instances
	 */
	protected List<CATCompatibleStand> getStandList() {return stands;}
	
	
	/**
	 * This method returns the rotation length in year.
	 * @return an integer
	 */
	protected int getRotationLength() {return rotationLength;}


	/**
	 * This method format the end products into an array of collection of end products. The array has the time scale as index.
	 * @param timeScale a TimeScale instance
	 * @param endProductsCollections a Collection of Collections of EndProduct instances
	 * @return an array of Collections of EndProduct instances
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private Collection[] formatCarbonUnits(CATTimeTable timeScale, Collection<? extends CarbonUnit> carbonProducts) {
		Collection<CarbonUnit>[] outputArray = new Collection[timeScale.size()];
		for (int i = 0; i < outputArray.length; i++) {
			outputArray[i] = new ArrayList<CarbonUnit>();
		}
		
		if (carbonProducts!= null && !carbonProducts.isEmpty()) {
			for (CarbonUnit carbonUnit : carbonProducts) {
				outputArray[carbonUnit.getIndexInTimeScale()].add(carbonUnit);
			}
		}
	
		return outputArray;
	}

	/**
	 * This method returns a summary of simulation.
	 * @return a CarbonAccountingToolExportSummary instance if the simulation has been carried out or null otherwise
	 */
	protected CATSingleSimulationResult getSimulationSummary() {
		if (isSimulationValid) {
			if (summary == null) {
				summary = new CATSingleSimulationResult("Sim " + ++nbSimulations, this);
			}
			return summary; 
		} else {
			return null;
		}
	}

	protected void storeResults() {
		getSimulationSummary().updateResult(this);
	}
		
	protected boolean isInfiniteSequenceAllowed() {return isInfiniteSequenceAllowed;}
	protected ManagementType getManagementType() {return managementType;}

	protected void setRealization(int realizationId) {
		getTimeTable().setMonteCarloRealization(realizationId);
	}

	@Override
	public int getMonteCarloRealizationId() {
		return getTimeTable().getCurrentMonteCarloRealizationId();
	}
	
	/*
	 * Useless for sensitivity analysis (non-Javadoc)
	 * @see repicea.simulation.MonteCarloSimulationCompliantObject#getSubjectId()
	 */
	@Override
	public String getSubjectId() {return null;}

	/*
	 * Useless for sensitivity analysis (non-Javadoc)
	 * @see repicea.simulation.MonteCarloSimulationCompliantObject#getHierarchicalLevel()
	 */
	@Override
	public HierarchicalLevel getHierarchicalLevel() {return null;}


	
}


