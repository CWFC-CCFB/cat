/*
 * This file is part of the CAT library.
 *
 * Copyright (C) 2024 His Majesty the King in Right of Canada
 * Authors: Jean-Francois Lavoie and Mathieu Fortin, Canadian Wood Fibre Centre
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed with the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * Please see the license at http://www.gnu.org/copyleft/lesser.html.
 */
package lerfob.carbonbalancetool.memsconnectors;

import java.util.concurrent.ConcurrentHashMap;

import lerfob.carbonbalancetool.CATCompartmentManager;
import lerfob.carbonbalancetool.CATTimeTable;
import lerfob.carbonbalancetool.CarbonArray;
import lerfob.carbonbalancetool.memsconnectors.MEMSSite.SiteName;
import lerfob.mems.SoilCarbonPredictor;
import lerfob.mems.SoilCarbonPredictorCompartments;
import lerfob.mems.SoilCarbonPredictorInput;
import repicea.serial.UnmarshallingException;
import repicea.serial.xml.XmlDeserializer;
import repicea.util.ObjectUtility;

/**
 * A wrapper of the original MEMS model for easier implementation in CAT.
 * @author J-F Lavoie - April 2024
 */
public class MEMSWrapper {
	
    public class CarbonStock {
        public final static double factorGCm2ToMgHa = 0.01d;
        public final static double factorMgHaToGCm2 = 1.0d / factorGCm2ToMgHa;
        public CarbonStock(double humus, double soil) {
            this.humus = humus;
            this.soil = soil;
        }

        public void setCarbon(SoilCarbonPredictorCompartments compartments) {
            humus = compartments.getLitterBinsgCm2() * factorGCm2ToMgHa;
            soil = compartments.getSoilBinsgCm2() * factorGCm2ToMgHa;
        }

        public double humus;
        public double soil;
    }
        
    CarbonStock[] inputAnnualStocksGCm2;
    CarbonStock[] outputAnnualStocksMgHa;
    private CarbonArray inputFromLivingTreesAboveGroundMgHa;
    private CarbonArray inputFromLivingTreesBelowGroundMgHa;
    
    SoilCarbonPredictor predictor;
    SoilCarbonPredictorCompartments compartments;
    
    static ConcurrentHashMap<MEMSSite.SiteName, MEMSSite> sites = new ConcurrentHashMap<MEMSSite.SiteName, MEMSSite>();

    MEMSSite.SiteName currentSiteName;
    
    private final CATCompartmentManager manager;

    /**
     * Constructor.
     */
    public MEMSWrapper(CATCompartmentManager manager) {
    	this.manager = manager;
    }
    
//    public static List<MEMSSite.SiteName> getSitesList() {
//        return Arrays.asList(MEMSSite.SiteName.values());
//    }
    
    public CarbonArray getInputFromLivingTreesAboveGroundMgHaArray() {
    	return inputFromLivingTreesAboveGroundMgHa;
    }

    public CarbonArray getInputFromLivingTreesBelowGroundMgHaArray() {
    	return inputFromLivingTreesBelowGroundMgHa;
    }

    /**
     * Initialize MEMS with appropriate parameters.<p>
     * This method is called as the carbon compartment manager is reset.
     * @param siteName a MEMSSite.SiteName enum
     */
    public void prepareSimulation(SiteName siteName) {

    	CATTimeTable timeTable = manager.getTimeTable();
        // run a simulation to reach stability into compartment bins
        // todo: use what input params ?  and what input npps ?
        int nbYears = timeTable.size();

        // prepare the carbon stock array
        inputAnnualStocksGCm2 = new CarbonStock[nbYears];
        outputAnnualStocksMgHa = new CarbonStock[nbYears];
        inputFromLivingTreesAboveGroundMgHa = new CarbonArray(nbYears);
        inputFromLivingTreesBelowGroundMgHa = new CarbonArray(nbYears);

        for (int i = 0; i < nbYears; i++) {
            inputAnnualStocksGCm2[i] = new CarbonStock(0.0, 0.0);
            outputAnnualStocksMgHa[i] = new CarbonStock(0.0, 0.0);
        }
        
        setSiteAndEstimateInitialCarbon(siteName);
    }
    
    private void setSiteAndEstimateInitialCarbon(SiteName siteName) {
        currentSiteName = siteName;

        if (!sites.containsKey(currentSiteName)) {
            String sitesPath = ObjectUtility.getRelativePackagePath(SoilCarbonPredictor.class) + "data" + ObjectUtility.PathSeparator + "sites" + ObjectUtility.PathSeparator;

            // load the site params
            String filename = sitesPath + currentSiteName.name() + ".site.zml";
            XmlDeserializer dser = new XmlDeserializer(filename);

            try {
                sites.put(currentSiteName, (MEMSSite) dser.readObject());
            } catch (UnmarshallingException e) {
                throw new RuntimeException(e);
            }
        }

        MEMSSite currentSite = sites.get(currentSiteName);
        currentSite.inputs.reset();
        
        compartments = new SoilCarbonPredictorCompartments(1.0, currentSite.getMAT(), currentSite.getTRange());

        predictor = new SoilCarbonPredictor(false);
        // read the fit params from mha and set them to the Predictor
        predictor.setParms(currentSite.getMetropolisHastingsAlgorithm().getFinalParameterEstimates());

        for (int i = 0; i < 1000; i++) {		// TODO the initial carbon stocks could be integrated in the MEMSSite instance MF20240516
            predictor.predictAnnualCStocks(compartments, currentSite.getInputs());
        }
        
        outputAnnualStocksMgHa[0].setCarbon(compartments);
    }

    /**
     * Add carbon to MEMS input.<p>
     * This carbon is retrieved from the actualization of LeftInForestCarbonUnit instances.
     * @param index the time index as defined in the CATTimeTable instance
     * @param carbonStockMgHa the carbon stock (Mg/ha)
     * @param addToHumus a boolean (true: add to humus; false add to soil)
     */
    public void addCarbonToMEMSInput(int index, double carbonStockMgHa, boolean addToHumus) {
        if (addToHumus) {
            inputAnnualStocksGCm2[index].humus += carbonStockMgHa * CarbonStock.factorMgHaToGCm2;
        } else {
            inputAnnualStocksGCm2[index].soil += carbonStockMgHa * CarbonStock.factorMgHaToGCm2;
        }
    }
    
    /**
     * Simulate the carbon stock for each year of the simulation.<p>
     * This method is called immediately after actualizing the carbon unit in all the compartments.
     */
    public void simulate() {
    	// first we add the input from living biomass to the annual input which contains only dead organic matter at this point
        for (int i = 0; i < inputAnnualStocksGCm2.length; i++) {
        	addCarbonToMEMSInput(i, 
        			inputFromLivingTreesAboveGroundMgHa.getCarbonArray()[i], 
        			true); // true add to humus
        	addCarbonToMEMSInput(i, 
        			inputFromLivingTreesBelowGroundMgHa.getCarbonArray()[i], 
        			false); // false add to soil
        }
    	
        // TODO: use what input params ?  and what input npps ?

        MEMSSite currentSite = sites.get(currentSiteName);
        CATTimeTable timeTable = manager.getTimeTable();
        SoilCarbonPredictorInput inputParameters = currentSite.getInputs();
        inputParameters.reset();
        
        for (int i = 1; i < inputAnnualStocksGCm2.length; i++) {
            int yearZero = timeTable.getDateYrAtThisIndex(i - 1);
            int yearCurrent = timeTable.getDateYrAtThisIndex(i);
            int deltaYear = yearCurrent - yearZero;
            if (deltaYear == 0) {
                outputAnnualStocksMgHa[i] = outputAnnualStocksMgHa[i - 1];
            } else {
                CarbonStock inputStock = inputAnnualStocksGCm2[i]; // TODO Plug the new input in MEMS
                inputParameters.setDailyInput(inputStock.humus, inputStock.soil);
                for (int y = 0; y < deltaYear; y++) {
                    predictor.predictAnnualCStocks(compartments, inputParameters);
                }

                CarbonStock outputStock = outputAnnualStocksMgHa[i];
                outputStock.setCarbon(compartments);
            }
        }
    }

    /**
     * Provide the carbon stock in the soil for a particular year.
     * @param yearIndex the index of the year as defined in the CATTimeTable instance
     * @return a CarbonStock instance which contains the stocks in the soil and the humus.
     */
    public CarbonStock getCarbonStockMgHaForThisYear(int yearIndex) {
        return outputAnnualStocksMgHa[yearIndex];
    }
}
