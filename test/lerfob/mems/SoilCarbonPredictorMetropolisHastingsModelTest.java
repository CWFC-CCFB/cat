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
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;

import repicea.serial.xml.XmlSerializer;
import repicea.stats.estimators.mcmc.MetropolisHastingsAlgorithm;
import repicea.util.ObjectUtility;
import repicea.util.REpiceaLogManager;

/**
 * This class can be used to fit mems to ground observations using
 * the Metropolis-Hastings algorithm.
 */
public class SoilCarbonPredictorMetropolisHastingsModelTest {

	public static void main(String argv[])  throws Exception {
		MetropolisHastingsAlgorithm mha;
		
		// Read current file;
//		String refPath = ObjectUtility.getPackagePath(SoilCarbonPredictorMetropolisHastingsModelTest.class) + "data" + File.separator;
//		XmlDeserializer deser = new XmlDeserializer(refPath + "mcmcMems_Montmorency_NEW.zml");
//		Object o = deser.readObject();
//		mha = (MetropolisHastingsAlgorithm) deser.readObject();
//		System.out.println(mha.getReport());
		
		
        double MAT = 3.8;  // between Jan 1 2013 to Dec 31st 2016 at MM
        double MinTemp = -9.48; // between Jan 1 2013 to Dec 31st 2016 at MM
        double MaxTemp = 17.79;  // between Jan 1 2013 to Dec 31st 2016 at MM
        double Trange = MaxTemp - MinTemp;

        SoilCarbonPredictorCompartments compartments = new SoilCarbonPredictorCompartments(1.0, MAT, Trange);
        SoilCarbonPredictorInput inputs = new SoilCarbonPredictorInput(SoilCarbonPredictorInput.LandType.MontmorencyForest, 
        		304.0, 
        		304.0 * 0.5,	// According to Jackson et al. (1997) Fine root productions is approximately 33% of total NPP 
        		15, 
        		4.22, 
        		0.7918, 
        		66.97, 
        		3.80);

        SoilCarbonPredictorMetropolisHastingsModel model = new SoilCarbonPredictorMetropolisHastingsModel(compartments, inputs);
        String path = ObjectUtility.getPackagePath(SoilCarbonPredictorMetropolisHastingsModelTest.class) + "data" + File.separator;
        File f = new File(path);
        boolean folderExists = f.exists() && f.isDirectory();
        if (!folderExists) {
        	throw new InvalidParameterException("Destination folder does not exist:" + path);
        }
        String filename = path + "ChronosequenceFOMFormatted.csv";
        model.readFile(filename);
        
        mha = new MetropolisHastingsAlgorithm(model, "mh.log", "MH");
        
        Level l = Level.FINE;
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(l);
        REpiceaLogManager.getLogger("mh.log").setLevel(Level.FINE);
        REpiceaLogManager.getLogger("mh.log").addHandler(ch);
        REpiceaLogManager.getLogger("mh.log").log(Level.FINE, "Starting MEMS calibration...");
        mha.getSimulationParameters().nbInitialGrid = 10000;
        mha.getSimulationParameters().nbInternalIter = 200000;
        mha.getSimulationParameters().nbBurnIn = 20000;
        mha.getSimulationParameters().nbAcceptedRealizations = 500000 + mha.getSimulationParameters().nbBurnIn;
        mha.doEstimation();
        
        String mcmcFilename = path + "mcmcMems_Montmorency.zml";
//        XmlDeserializer deser = new XmlDeserializer(mcmcFilename);
//        mha = (MetropolisHastingsAlgorithm) deser.readObject();
        String outputFilename = path + "parameterEstimatesSet_Montmorency.csv";
        mha.exportMetropolisHastingsSample(outputFilename);
        System.out.println(mha.getReport());
        XmlSerializer serializer = new XmlSerializer(mcmcFilename);
        serializer.writeObject(mha);
	}
}
