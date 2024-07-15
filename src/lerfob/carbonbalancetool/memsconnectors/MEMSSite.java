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

import lerfob.mems.SoilCarbonPredictor;
import lerfob.mems.SoilCarbonPredictorInput;
import repicea.serial.UnmarshallingException;
import repicea.serial.xml.XmlDeserializer;
import repicea.serial.xml.XmlSerializer;
import repicea.stats.estimators.mcmc.MetropolisHastingsAlgorithm;
import repicea.util.ObjectUtility;
import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.TextableEnum;

public class MEMSSite {
	
    public static enum SiteType implements TextableEnum {
        Montmorency1("Montmorency1", "Montmorency1"),
        Montmorency2("Montmorency2", "Montmorency2");

        SiteType(String englishText, String frenchText) {
            setText(englishText, frenchText);
        }
        @Override
        public void setText(String englishText, String frenchText) {
            REpiceaTranslator.setString(this, englishText, frenchText);
        }
    }
    MetropolisHastingsAlgorithm mha;
    SoilCarbonPredictorInput inputs;

//    double MAT;
//    double MinTemp;
//    double MaxTemp;

//    public double getMAT() {
//        return MAT;
//    }
//
//    public double getTRange() {
//        return MaxTemp - MinTemp;
//    }

    protected SoilCarbonPredictorInput getInputs() {
        return inputs;
    }

    public MetropolisHastingsAlgorithm getMetropolisHastingsAlgorithm() {
        return mha;
    }
    
    public static void main(String argv[])  throws Exception {
        {
            MEMSSite siteMMF = new MEMSSite();

//            siteMMF.MAT = 3.8; // between Jan 1 2013 to Dec 31st 2016 at MM
//            siteMMF.MinTemp = -9.48;   // between Jan 1 2013 to Dec 31st 2016 at MM
//            siteMMF.MaxTemp = 17.79;   // between Jan 1 2013 to Dec 31st 2016 at MM

            siteMMF.inputs = new SoilCarbonPredictorInput(SoilCarbonPredictorInput.LandType.MontmorencyForest,
                    304.0,
                    54.72,
                    15,
                    4.22,
                    0.7918,
                    66.97,
                    3.80);

            String path = ObjectUtility.getRelativePackagePath(SoilCarbonPredictor.class) + "data" + ObjectUtility.PathSeparator;
            String filename = path + "mcmcMems_Montmorency.zml";
            //        System.out.println("Filename is " + filename);
            XmlDeserializer dser = new XmlDeserializer(filename);

            try {
                siteMMF.mha = (MetropolisHastingsAlgorithm) dser.readObject();

                XmlSerializer ser = new XmlSerializer(path + "sites" + ObjectUtility.PathSeparator + SiteType.Montmorency1.name() + ".site.zml");
                ser.writeObject(siteMMF);

            } catch (UnmarshallingException e) {
                throw new RuntimeException(e);
            }
        }

        {
            MEMSSite siteMMF = new MEMSSite();

//            siteMMF.MAT = 3.8; // between Jan 1 2013 to Dec 31st 2016 at MM
//            siteMMF.MinTemp = -9.48;   // between Jan 1 2013 to Dec 31st 2016 at MM
//            siteMMF.MaxTemp = 17.79;   // between Jan 1 2013 to Dec 31st 2016 at MM

            siteMMF.inputs = new SoilCarbonPredictorInput(SoilCarbonPredictorInput.LandType.MontmorencyForest,
                    304.0,
                    54.72,
                    15,
                    4.22,
                    0.7918,
                    66.97,
                    3.80);

            String path = ObjectUtility.getRelativePackagePath(SoilCarbonPredictor.class) + "data" + ObjectUtility.PathSeparator;
            String filename = path + "mcmcMems_Montmorency_NEW.zml";
            //        System.out.println("Filename is " + filename);
            XmlDeserializer dser = new XmlDeserializer(filename);

            try {
                siteMMF.mha = (MetropolisHastingsAlgorithm) dser.readObject();

                XmlSerializer ser = new XmlSerializer(path + "sites" + ObjectUtility.PathSeparator + SiteType.Montmorency2.name() + ".site.zml");
                ser.writeObject(siteMMF);

            } catch (UnmarshallingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
