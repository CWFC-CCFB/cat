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
public class SoilCarbonPredictorMetropolisHastingsModelMontmorencyForest {

	public static void main(String argv[])  throws Exception {
		
		
//		String path = ObjectUtility.getPackagePath(SoilCarbonPredictor.class) + 
//				"data" + File.separator + "sites" + File.separator;
//		String mcmcFilename = path + "mcmcMems_MontmorencyBL0_95CO0_10.zml";
//		XmlDeserializer deser = new XmlDeserializer(mcmcFilename);
//		MEMSSite mha = (MEMSSite) deser.readObject();
//		System.out.println(mha.mha.getReport());
//		int u = 0;
		
		
		
		MetropolisHastingsAlgorithm mha;
		
        double MAT = 3.8;  // between Jan 1 2013 to Dec 31st 2016 at MM
        double MinTemp = -9.48; // between Jan 1 2013 to Dec 31st 2016 at MM
        double MaxTemp = 17.79;  // between Jan 1 2013 to Dec 31st 2016 at MM
        double Trange = MaxTemp - MinTemp;

        double aboveGroundNPPgCm2 = 149d;
        double depth_cm = 15d;
        double belowGroundNPPgCm2 = 44d; 
        
        
        SoilCarbonPredictorCompartments compartments = new SoilCarbonPredictorCompartments(1.0, MAT, Trange);
        SoilCarbonPredictorInput inputs = new SoilCarbonPredictorInput(SoilCarbonPredictorInput.LandType.MontmorencyForest, 
        		aboveGroundNPPgCm2, 
        		belowGroundNPPgCm2,	
        		depth_cm, 
        		4.22, 
        		0.7918, 
        		66.97, 
        		3.80);

//        String xmlFilename = ObjectUtility.getPackagePath(MEMSSite.class) + 
//        		"data" + File.separator + 
//        		"sites" + File.separator +
//        		"mcmcMems_" + SiteType.MontmorencyBL0_95CO0_10.name() + ".zml";
//        XmlDeserializer deser = new XmlDeserializer(xmlFilename);
//        Object o = deser.readObject();
//        XmlSerializer ser = new XmlSerializer(xmlFilename);
//        ser.writeObject(new MEMSSite(o, inputs));
//        int u = 0;
        
        
        SoilCarbonPredictorMetropolisHastingsModel model = new SoilCarbonPredictorMetropolisHastingsModel(compartments, inputs);
        String path = ObjectUtility.getPackagePath(SoilCarbonPredictorMetropolisHastingsModelMontmorencyForest.class) + "data" + File.separator;
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
        serializer.writeObject(new MEMSSite(mha, inputs));
	}
}
