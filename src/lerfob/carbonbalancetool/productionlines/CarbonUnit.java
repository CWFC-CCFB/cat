/*
 * This file is part of the lerfob-forestools library.
 *
 * Copyright (C) 2010-2012 Mathieu Fortin for LERFOB INRA/AgroParisTech, 
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
package lerfob.carbonbalancetool.productionlines;

import java.util.ArrayList;
import java.util.List;

import lerfob.carbonbalancetool.CATCompartmentManager;
import lerfob.carbonbalancetool.CATDecayFunction;
import lerfob.carbonbalancetool.CATSettings.CATSpecies;
import lerfob.carbonbalancetool.CATTimeTable;
import lerfob.carbonbalancetool.productionlines.CarbonUnit.Element;
import repicea.simulation.MonteCarloSimulationCompliantObject;
import repicea.simulation.processsystem.AmountMap;
import repicea.simulation.processsystem.ProcessUnit;


/**
 * A CarbonUnit instance is a piece of carbon.
 * @author Mathieu Fortin - November 2010
 */
public class CarbonUnit extends ProcessUnit<Element> implements BiomassTypeProvider {

	public final static String AllSpecies = "AllSpecies";
	
	
	public static enum CarbonUnitStatus {
		EndUseWoodProduct, 
		LandFillDegradable,
		LandFillNonDegradable,
		Recycled, 
		/** 
		 * Can be either harvest residues or dead wood. 
		 */
		DeadWood, 
		IndustrialLosses,
		RecycledLosses;
	};

	public static enum BiomassType {
		Wood,
		Bark;
	}
	
	public static enum Element {
		Volume,
		Biomass,
		/** Carbon */
		C,	
		/** Nitrogen */
		N,
		/** Sulfur */
		S, 
		/** Phosphorus */
		P, 
		/** Potassium */
		K,
		/** Emissions CO2 eq. related to the production and transport and others **/
		EmissionsCO2Eq;
		
		private static List<Element> nutrientList;
		
		public static List<Element> getNutrients() {
			if (nutrientList == null) {
				nutrientList = new ArrayList<Element>();
				nutrientList.add(N);
				nutrientList.add(S);
				nutrientList.add(P);
				nutrientList.add(K);
				nutrientList.add(C);
			}
			return nutrientList;
		}
		
		
	}
	
	private CATTimeTable timeTable;
	
	private final int dateIndex;
	protected final String samplingUnitID;
	private final List<CarbonUnitStatus> status; 
	private final CarbonUnitFeature carbonUnitFeature;
	private CATSpecies species;
	private BiomassType biomassType;
	
	/**
	 * Initial carbon in this product (Mg)
	 */
	private double[] currentCarbonArray;
	
	private boolean actualized;

	/**
	 * General constructor.
	 * @param dateIndex the creation date index of the time scale
	 * @param samplingUnitID the id of the sample unit
	 * @param carbonUnitFeature a CarbonUnitFeature instance
	 * @param initialAmounts a map that contains the amount of each element to be processed
	 * @param speciesName the name of the species
	 */
	protected CarbonUnit(int dateIndex, 
			String samplingUnitID, 
			CarbonUnitFeature carbonUnitFeature, 
			AmountMap<Element> initialAmounts,
			CATSpecies species,
			BiomassType biomassType) {
		super(initialAmounts);
		this.dateIndex = dateIndex;
		this.carbonUnitFeature = carbonUnitFeature;
		this.samplingUnitID = samplingUnitID;
		this.species = species;
		status = new ArrayList<CarbonUnitStatus>();
		actualized = false;
		this.biomassType = biomassType; 
	}

	@Override
	public BiomassType getBiomassType() {
		if (biomassType == null) {
			return BiomassType.Wood;
		} else {
			return biomassType;
		}
	}

	@Override
	protected void addProcessUnit(ProcessUnit<Element> unit) {
		super.addProcessUnit(unit);
	}

	
	protected boolean isActualized() {return actualized;}
	

	public CATSpecies getSpecies() {return species;}
	
	/**
	 * This method returns the creation date of the product
	 * @return an integer
	 */
	protected int getCreationDate() {return timeTable.getDateYrAtThisIndex(dateIndex);}
	
	protected void setTimeTable(CATTimeTable timeTable) {this.timeTable = timeTable;}
	protected CATTimeTable getTimeTable() {return timeTable;}
	protected CarbonUnitFeature getCarbonUnitFeature() {return carbonUnitFeature;}
	/**
	 * This method returns the carbon (tC) at the creation date. NOTE: For the landfill carbon, only
	 * the degradable organic carbon is considered.
	 * @return a double 
	 */
	public double getInitialCarbon() {return getAmountMap().get(Element.C);}

	/**
	 * This method returns an array that contains the current carbon (tC) or null if the carbon unit has not been actualized.
	 * @return an array of double
	 */
	public double[] getCurrentCarbonArray() {
		if (isActualized()) {
			return currentCarbonArray;
		} else {
			return null;
		}
	}

	/**
	 * Calculate the average carbon by integrating the carbon contained in 
	 * this product over its useful lifetime.
	 * @param subject a MonteCarloSimulationCompliantObject instance typically the CATCompartmentManager instance 
	 * which provides Monte Carlo realization id in case of stochastic simulation
	 * @return the integrated carbon in tC (double)
	 */
	public double getIntegratedCarbon(MonteCarloSimulationCompliantObject subject) {
		CATDecayFunction decayFunction = getCarbonUnitFeature().getDecayFunction();
		decayFunction.setAverageLifetimeYr(getCarbonUnitFeature().getAverageLifetime(subject));
		return getInitialCarbon() * decayFunction.getInfiniteIntegral(); //	0d : unnecessary parameter
	}



	/**
	 * This method actualizes the carbon content of this carbon unit.
	 * @param compartmentManager a CATCompartmentManager instance
	 * @throws Exception
	 */
	protected void actualizeCarbon(CATCompartmentManager compartmentManager) throws Exception {
		CATDecayFunction decayFunction = getCarbonUnitFeature().getDecayFunction();
		CATTimeTable timeScale = compartmentManager.getTimeTable();
		setTimeTable(timeScale);
		currentCarbonArray = new double[timeScale.size()];

		double averageLifetimeYr = getCarbonUnitFeature().getAverageLifetime(compartmentManager);
		decayFunction.setAverageLifetimeYr(averageLifetimeYr);
		double currentCarbon = getInitialCarbon();

		double formerCarbon;
		double factor;
		int date;
		
		for (int i = dateIndex; i < timeScale.size(); i++) {
			date = timeScale.getDateYrAtThisIndex(i);
			if (date > getCreationDate() && currentCarbon > ProductionProcessorManager.VERY_SMALL) {
				if (averageLifetimeYr > 0) {	// calculate the proportion only if lifetime is greater than 0
					double thisRemains = decayFunction.getValueAtTime(date - getCreationDate());
					double thatRemained = decayFunction.getValueAtTime(timeScale.getDateYrAtThisIndex(i - 1) - getCreationDate());
					factor = thisRemains / thatRemained;	
				} else { // otherwise all the carbon is gone
					factor = 0d;
				}
				formerCarbon = currentCarbonArray[i - 1];
				currentCarbon =  formerCarbon * factor;
				currentCarbonArray[i] = currentCarbon;
			} else if (date == getCreationDate()) {
				currentCarbonArray[i] = getInitialCarbon();
			}
		}
		actualized = true;
	}
	

	/**
	 * This method returns the released carbon along in time given the product has been actualized. Otherwise it returns null.
	 * @return an array of double that contains the released carbon (tC)
	 */
	public double[] getReleasedCarbonArray() {
		if (isActualized()) {
			double[] releasedCarbonArray = new double[currentCarbonArray.length];
			int date;
			for (int i = 1; i < currentCarbonArray.length; i++) {
				date = getTimeTable().getDateYrAtThisIndex(i);
				if (date > getCreationDate()) {
					releasedCarbonArray[i] = currentCarbonArray[i - 1] - currentCarbonArray[i];
				}
			}
			return releasedCarbonArray;
		} else {
			return null;
		}
	}


	/**
	 * A carbon unit object is considered to be equal if it has the same creation date and the same carbon unit features.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CarbonUnit) {
			CarbonUnit otherUnit = (CarbonUnit) obj;
			if ((otherUnit.carbonUnitFeature == null && carbonUnitFeature == null) || (carbonUnitFeature.equals(otherUnit.carbonUnitFeature)))  {
				if (dateIndex == otherUnit.dateIndex) {
					if (status.equals(otherUnit.status)) {
						if (samplingUnitID.equals(otherUnit.samplingUnitID)) {
							if (species.equals(otherUnit.species)) {
								if (getBiomassType() == otherUnit.getBiomassType()) {
									if (!actualized && !otherUnit.actualized) { // if both units have not been actualized yet
										return true;
									}
								}
							}
						}
					}
				}
			}
		} 
		return false;
	}

	
	
	@Override
	public String toString() {
		return "Code : " + this.hashCode() 
				+ "; Volume = " + this.getAmountMap().get(Element.Volume) 
				+ "; Carbon : " + getInitialCarbon();
	}
	
	protected CarbonUnitStatus getLastStatus() {
		return status.get(status.size() - 1);
	}
	
	protected void addStatus(CarbonUnitStatus currentStatus) {status.add(currentStatus);}
	
	/**
	 * This method returns the emissions in Mg of CO2 Eq.
	 * @return a double
	 */
	public double getTotalNonRenewableCarbonEmissionsMgCO2Eq() {
		Double emissions = getAmountMap().get(Element.EmissionsCO2Eq);
		if (emissions == null) {
			return 0d;
		} else {
			return - emissions;
		}
	}
	
	/** 
	 * This method returns the index on the time scale at which the product has been created.
	 * @return an Integer
	 */
	public int getIndexInTimeScale() {return dateIndex;}
	
	
}
