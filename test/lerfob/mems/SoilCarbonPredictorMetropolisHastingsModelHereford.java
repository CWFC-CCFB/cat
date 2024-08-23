/*
 * This file is part of the mems library.
 *
 * Copyright (C) 2022-23 His Majesty the King in Right of Canada
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
package lerfob.mems;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import biosimclient.BioSimClient;
import biosimclient.BioSimDataSet;
import biosimclient.BioSimEnums.ClimateModel;
import biosimclient.BioSimEnums.RCP;
import biosimclient.BioSimPlot;
import biosimclient.BioSimPlotImpl;
import repicea.serial.xml.XmlSerializer;
import repicea.stats.estimators.mcmc.MetropolisHastingsAlgorithm;
import repicea.util.ObjectUtility;
import repicea.util.REpiceaLogManager;

/**
 * This class is used to fit MEMS to ground observations from
 * Hereford forest using the Metropolis-Hastings algorithm.<p>
 * @author Mathieu Fortin - August 2024
 */
public class SoilCarbonPredictorMetropolisHastingsModelHereford {

	private static Map<Integer, Integer> CumulativeDaysPerMonth = new HashMap<Integer,Integer>();
	static {
		CumulativeDaysPerMonth.put(1, 0);
		CumulativeDaysPerMonth.put(2, 31);
		CumulativeDaysPerMonth.put(3, 59);
		CumulativeDaysPerMonth.put(4, 90);
		CumulativeDaysPerMonth.put(5, 120);
		CumulativeDaysPerMonth.put(6, 151);
		CumulativeDaysPerMonth.put(7, 181);
		CumulativeDaysPerMonth.put(8, 212);
		CumulativeDaysPerMonth.put(9, 243);
		CumulativeDaysPerMonth.put(10, 273);
		CumulativeDaysPerMonth.put(11, 304);
		CumulativeDaysPerMonth.put(12, 334);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private static double[] getMeanDailyTemperature() throws Exception {
		BioSimPlot p = new BioSimPlotImpl(49.15,-71.80, Double.NaN);
		LinkedHashMap<String, Object> biosimReturn = BioSimClient.generateWeather(1991, 
				2010, 
				Arrays.asList(new BioSimPlot[] {p}), 
				RCP.RCP45, 
				ClimateModel.RCM4, 
				Arrays.asList(new String[] {"Climatic_Daily"}), 
				null);
		BioSimDataSet dailyClimate = (BioSimDataSet) ((LinkedHashMap) biosimReturn.get("Climatic_Daily")).get(p);
		List<Integer> years = (List) dailyClimate.getFieldValues(dailyClimate.getFieldNames().indexOf("Year"));
		List<Integer> months = (List) dailyClimate.getFieldValues(dailyClimate.getFieldNames().indexOf("Month"));
		List<Integer> days = (List) dailyClimate.getFieldValues(dailyClimate.getFieldNames().indexOf("Day"));
		List<Double> temperature = (List) dailyClimate.getFieldValues(dailyClimate.getFieldNames().indexOf("Tair"));
		double[] meanTemperature = new double[365];
		List<Integer> differentYears = new ArrayList<Integer>();
		for (int i = 0; i < years.size(); i++) {
			int thisYear = years.get(i);
			if (!differentYears.contains(thisYear)) {
				differentYears.add(thisYear);
			}
			int thisMonth = months.get(i);
			int thisDay = days.get(i);
			if (thisMonth != 2 || thisDay != 29) {
				int julianDay = CumulativeDaysPerMonth.get(months.get(i)) + days.get(i);
				meanTemperature[julianDay - 1] += temperature.get(i);
			} 
		}
		
		double nbYearsFactor = 1d / differentYears.size();
		for (int i = 0; i < meanTemperature.length; i++) {
			meanTemperature[i] *= nbYearsFactor;
		}
		return meanTemperature;
	}
	
	
	
	public static void main(String argv[])  throws Exception {
        Level l = Level.FINE;
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(l);
        REpiceaLogManager.getLogger("mh.log").setLevel(Level.FINE);
        REpiceaLogManager.getLogger("mh.log").addHandler(ch);   // TODO some lines are repeated twice if this line is uncommented MF20240822
        REpiceaLogManager.logMessage("mh.log", Level.INFO, "From main", "Starting MEMS calibration...");

        MetropolisHastingsAlgorithm mha;

        REpiceaLogManager.logMessage("mh.log", Level.INFO, "From main", "Retrieving average climate from BioSIM Web API...");
		double[] meanDailyTemperature = getMeanDailyTemperature(); 

        REpiceaLogManager.logMessage("mh.log", Level.INFO, "From main", "Preparing soil input...");

        double aboveGroundNPPgCm2 = 337d;
        double belowGroundNPPgCm2 = 44d; 
        double depth_cm = 15d;
        SoilCarbonPredictorCompartments compartments = new SoilCarbonPredictorCompartments(1.0, meanDailyTemperature, true); // true: air temperature 
        SoilCarbonPredictorInput inputs = new SoilCarbonPredictorInput(SoilCarbonPredictorInput.LandType.MontmorencyForest, 
        		aboveGroundNPPgCm2, 
        		belowGroundNPPgCm2,	
        		depth_cm, 
        		4.24, 
        		0.5951, 
        		36.625, 
        		20.84); // the last four doubles have been validated MF20240814

        SoilCarbonPredictorMetropolisHastingsModel model = new SoilCarbonPredictorMetropolisHastingsModel(compartments, inputs);
        
        String path = ObjectUtility.getPackagePath(SoilCarbonPredictorMetropolisHastingsModelHereford.class) + "data" + File.separator;
        File f = new File(path);
        String filename = path + "HerefordFormatted.csv";
        REpiceaLogManager.logMessage("mh.log", Level.INFO, "From main", "Loading file " + filename);
        boolean folderExists = f.exists() && f.isDirectory();
        if (!folderExists) {
        	throw new InvalidParameterException("Destination folder does not exist:" + path);
        }
        model.readFile(filename);
        
        mha = new MetropolisHastingsAlgorithm(model, "mh.log", "MH");
        
        mha.getSimulationParameters().nbInitialGrid = 10000;
        mha.getSimulationParameters().nbInternalIter = 200000;
        mha.getSimulationParameters().nbBurnIn = 20000;
        mha.getSimulationParameters().nbAcceptedRealizations = 500000 + mha.getSimulationParameters().nbBurnIn;
        REpiceaLogManager.logMessage("mh.log", Level.INFO, "main", "Running MCMC simulation...");
        mha.doEstimation();
        
        String mcmcFilename = path + "mcmcMems_Hereford.zml";
//        XmlDeserializer deser = new XmlDeserializer(mcmcFilename);
//        mha = (MetropolisHastingsAlgorithm) deser.readObject();
        String outputFilename = path + "parameterEstimatesSet_Hereford.csv";
        REpiceaLogManager.logMessage("mh.log", Level.INFO, "From main", "Exporting parameter sets to " + outputFilename);
        mha.exportMetropolisHastingsSample(outputFilename);
        System.out.println(mha.getReport());
        REpiceaLogManager.logMessage("mh.log", Level.INFO, "From main", "Serializing MEMSSite in " + mcmcFilename);
        XmlSerializer serializer = new XmlSerializer(mcmcFilename);
        serializer.writeObject(new MEMSSite(mha, inputs));
	}
}
