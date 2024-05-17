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
	
    public static enum SiteName implements TextableEnum {
        Montmorency1("Montmorency1", "Montmorency1"),
        Montmorency2("Montmorency2", "Montmorency2");

        SiteName(String englishText, String frenchText) {
            setText(englishText, frenchText);
        }
        @Override
        public void setText(String englishText, String frenchText) {
            REpiceaTranslator.setString(this, englishText, frenchText);
        }
    }
    MetropolisHastingsAlgorithm mha;
    SoilCarbonPredictorInput inputs;

    double MAT;
    double MinTemp;
    double MaxTemp;

    public double getMAT() {
        return MAT;
    }

    public double getTRange() {
        return MaxTemp - MinTemp;
    }

    public SoilCarbonPredictorInput getInputs() {
        return inputs;
    }

    public MetropolisHastingsAlgorithm getMetropolisHastingsAlgorithm() {
        return mha;
    }
    public static void main(String argv[])  throws Exception {
        {
            MEMSSite siteMMF = new MEMSSite();

            siteMMF.MAT = 3.8; // between Jan 1 2013 to Dec 31st 2016 at MM
            siteMMF.MinTemp = -9.48;   // between Jan 1 2013 to Dec 31st 2016 at MM
            siteMMF.MaxTemp = 17.79;   // between Jan 1 2013 to Dec 31st 2016 at MM

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

                XmlSerializer ser = new XmlSerializer(path + "sites" + ObjectUtility.PathSeparator + SiteName.Montmorency1.name() + ".site.zml");
                ser.writeObject(siteMMF);

            } catch (UnmarshallingException e) {
                throw new RuntimeException(e);
            }
        }

        {
            MEMSSite siteMMF = new MEMSSite();

            siteMMF.MAT = 3.8; // between Jan 1 2013 to Dec 31st 2016 at MM
            siteMMF.MinTemp = -9.48;   // between Jan 1 2013 to Dec 31st 2016 at MM
            siteMMF.MaxTemp = 17.79;   // between Jan 1 2013 to Dec 31st 2016 at MM

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

                XmlSerializer ser = new XmlSerializer(path + "sites" + ObjectUtility.PathSeparator + SiteName.Montmorency2.name() + ".site.zml");
                ser.writeObject(siteMMF);

            } catch (UnmarshallingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
