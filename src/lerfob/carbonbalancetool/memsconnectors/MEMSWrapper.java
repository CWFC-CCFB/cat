/*
 * This file is part of the CAT library.
 *
 * Copyright (C) 2024 His Majesty the King in Right of Canada
 * Authors: Jean-Francois Lavoie and Mathieu Fortin, Canadian Wood Fibre Centre
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
package lerfob.carbonbalancetool.memsconnectors;

import lerfob.carbonbalancetool.CATTimeTable;
import lerfob.mems.SoilCarbonPredictor;
import lerfob.mems.SoilCarbonPredictorCompartments;
import lerfob.mems.SoilCarbonPredictorInput;
import repicea.serial.UnmarshallingException;
import repicea.serial.xml.XmlDeserializer;
import repicea.stats.estimators.mcmc.MetropolisHastingsAlgorithm;
import repicea.util.ObjectUtility;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class MEMSWrapper {
	
    public class CarbonStock {
        public final static double factorGCm2ToMgHa = 0.01d;
        public final static double factorMgHaToGCm2 = 1.0d / factorGCm2ToMgHa;
        public CarbonStock(double humus, double soil) {
            this.humus = humus;
            this.soil = soil;
        }

        public void SetCarbon(SoilCarbonPredictorCompartments compartments) {
            humus = compartments.getLitterBinsgCm2() * factorGCm2ToMgHa;
            soil = compartments.getSoilBinsgCm2() * factorGCm2ToMgHa;
        }

        public double humus;
        public double soil;
    }
    CarbonStock[] inputAnnualStocksGCm2;
    CarbonStock[] outputAnnualStocksMgHa;
    SoilCarbonPredictor predictor;
    SoilCarbonPredictorCompartments compartments;
    CATTimeTable timeTable;
    static ConcurrentHashMap<MEMSSite.SiteName, MEMSSite> sites = new ConcurrentHashMap<MEMSSite.SiteName, MEMSSite>();

    MEMSSite.SiteName currentSiteName;

    public MEMSWrapper() {
    }
    public static List<MEMSSite.SiteName> getSitesList() {
        return Arrays.asList(MEMSSite.SiteName.values());
    }
    public void PrepareSimulation(CATTimeTable timeTable, MEMSSite.SiteName siteName) {

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

        // run a simulation to reach stability into compartment bins
        // todo: use what input params ?  and what input npps ?
        this.timeTable = timeTable;
        int nbYears = timeTable.size();

        compartments = new SoilCarbonPredictorCompartments(1.0, currentSite.getMAT(), currentSite.getTRange());

        predictor = new SoilCarbonPredictor(false);
        // read the fit params from mha and set them to the Predictor
        predictor.SetParms(currentSite.getMetropolisHastingsAlgorithm().getFinalParameterEstimates());

        for (int i = 0; i < 1000; i++) {
            predictor.predictAnnualCStocks(compartments, currentSite.getInputs());
        }

        // prepare the carbon stock array
        inputAnnualStocksGCm2 = new CarbonStock[nbYears];
        outputAnnualStocksMgHa = new CarbonStock[nbYears];

        for (int i = 0; i < nbYears; i++) {
            inputAnnualStocksGCm2[i] = new CarbonStock(0.0, 0.0);
            outputAnnualStocksMgHa[i] = new CarbonStock(0.0, 0.0);
        }

        outputAnnualStocksMgHa[0].SetCarbon(compartments);
    }

    public void AddCarbonInput(int index, double value, boolean addToHumus) {
        if (addToHumus)
            inputAnnualStocksGCm2[index].humus += value * CarbonStock.factorMgHaToGCm2;
        else
            inputAnnualStocksGCm2[index].soil += value * CarbonStock.factorMgHaToGCm2;
    }

    public void Simulate() {
        // todo: use what input params ?  and what input npps ?

        MEMSSite currentSite = sites.get(currentSiteName);

        for (int i = 1; i < inputAnnualStocksGCm2.length; i++) {
            int yearZero = timeTable.getDateYrAtThisIndex(i - 1);
            int yearCurrent = timeTable.getDateYrAtThisIndex(i);
            int deltaYear = yearCurrent - yearZero;
            if (deltaYear == 0) {
                outputAnnualStocksMgHa[i] = outputAnnualStocksMgHa[i - 1];
            }
            else {
                double annualFactor = 1.0d / deltaYear;
                CarbonStock inputStock = inputAnnualStocksGCm2[i];

                for (int y = 0; y < deltaYear; y++) {
                    predictor.predictAnnualCStocks(compartments, currentSite.getInputs());
                }

                CarbonStock outputStock = outputAnnualStocksMgHa[i];
                outputStock.SetCarbon(compartments);
            }
        }
    }

    public CarbonStock GetCarbonOutput(int year) {
        return outputAnnualStocksMgHa[year];
    }
}
