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

public class SoilCarbonPredictorMetropolisHastingsModelTest {

	public static void main(String argv[])  throws Exception {
        double MAT = 3.8;  // between Jan 1 2013 to Dec 31st 2016 at MM
        double MinTemp = -9.48; // between Jan 1 2013 to Dec 31st 2016 at MM
        double MaxTemp = 17.79;  // between Jan 1 2013 to Dec 31st 2016 at MM
        double Trange = MaxTemp - MinTemp;

        SoilCarbonPredictorCompartments compartments = new SoilCarbonPredictorCompartments(1.0, MAT, Trange);
        SoilCarbonPredictorInput inputs = new SoilCarbonPredictorInput(SoilCarbonPredictorInput.LandType.MontmorencyForest, 304.0, 54.72, 15, 4.22, 0.7918, 66.97, 3.80);

        SoilCarbonPredictorMetropolisHastingsModel model = new SoilCarbonPredictorMetropolisHastingsModel(compartments, inputs);
        String path = ObjectUtility.getTrueRootPath(SoilCarbonPredictorMetropolisHastingsModelTest.class);
        File f = new File(path);
        String rootPathProject = f.getParentFile().getAbsolutePath();
        path = rootPathProject + File.separator + "test" + 
        		File.separator + "lerfob" +
        		File.separator + "mems" + File.separator;
        f = new File(path);
        boolean folderExists = f.exists() && f.isDirectory();
        if (!folderExists) {
        	throw new InvalidParameterException("Destination folder does not exist:" + path);
        }
        MetropolisHastingsAlgorithm mha;
        String filename = path + "ChronosequenceFOMFormatted.csv";
        model.readFile(filename);
        
        Level l = Level.FINE;
        ConsoleHandler ch = new ConsoleHandler();
        ch.setLevel(l);
        REpiceaLogManager.getLogger("mh.log").setLevel(Level.FINE);
        REpiceaLogManager.getLogger("mh.log").addHandler(ch);
        mha = new MetropolisHastingsAlgorithm(model, "mh.log", "MH");
        mha.getSimulationParameters().nbInternalIter = 200000;
        mha.getSimulationParameters().nbBurnIn = 50000;
        mha.getSimulationParameters().nbAcceptedRealizations = 500000 + mha.getSimulationParameters().nbBurnIn;
        mha.doEstimation();
        
        String mcmcFilename = path + "mcmcMems.zml";
//        XmlDeserializer deser = new XmlDeserializer(mcmcFilename);
//        mha = (MetropolisHastingsAlgorithm) deser.readObject();
        String outputFilename = path + "parameterEstimatesSet.csv";
        mha.exportMetropolisHastingsSample(outputFilename);
        System.out.println(mha.getReport());
        XmlSerializer serializer = new XmlSerializer(mcmcFilename);
        serializer.writeObject(mha);
	}
}
