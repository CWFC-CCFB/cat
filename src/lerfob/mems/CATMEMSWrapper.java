package lerfob.mems;

import lerfob.carbonbalancetool.CATTimeTable;
import repicea.serial.UnmarshallingException;
import repicea.serial.xml.XmlDeserializer;
import repicea.stats.estimators.mcmc.MetropolisHastingsAlgorithm;
import repicea.util.ObjectUtility;

public class CATMEMSWrapper {
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
    public void PrepareSimulation(CATTimeTable timeTable) {
        // load the mcmc params
        String path = ObjectUtility.getRelativePackagePath(getClass()) + "data" + ObjectUtility.PathSeparator;
        String filename = path + "mcmcMems_Montmorency.zml";
//        System.out.println("Filename is " + filename);
        XmlDeserializer dser = new XmlDeserializer(filename);
        
        MetropolisHastingsAlgorithm mha = null;
        try {
            mha = (MetropolisHastingsAlgorithm)dser.readObject();

            // run a simulation to reach stability into compartment bins
            // todo: use what input params ?  and what input npps ?
            this.timeTable = timeTable;
            int nbYears = timeTable.size();

            double MAT = 3.8;  // between Jan 1 2013 to Dec 31st 2016 at MM
            double MinTemp = -9.48; // between Jan 1 2013 to Dec 31st 2016 at MM
            double MaxTemp = 17.79;  // between Jan 1 2013 to Dec 31st 2016 at MM
            double Trange = MaxTemp - MinTemp;

            compartments = new SoilCarbonPredictorCompartments(1.0, MAT, Trange);

            SoilCarbonPredictorInput inputs = new SoilCarbonPredictorInput(SoilCarbonPredictorInput.LandType.MontmorencyForest, 304.0, 54.72, 15, 4.22, 0.7918, 66.97, 3.80);
            predictor = new SoilCarbonPredictor(false);
            // read the fit params from mha and set them to the Predictor
            predictor.SetParms(mha.getFinalParameterEstimates());

            for (int i = 0; i < 1000; i++) {
                predictor.predictAnnualCStocks(compartments, inputs);
            }

            // prepare the carbon stock array
            inputAnnualStocksGCm2 = new CarbonStock[nbYears];
            outputAnnualStocksMgHa = new CarbonStock[nbYears];

            for (int i = 0; i < nbYears; i++) {
                inputAnnualStocksGCm2[i] = new CarbonStock(0.0, 0.0);
                outputAnnualStocksMgHa[i] = new CarbonStock(0.0, 0.0);
            }

            outputAnnualStocksMgHa[0].SetCarbon(compartments);

        } catch (UnmarshallingException e) {
            throw new RuntimeException(e);
        }
    }

    public void AddCarbonInput(int index, double value, boolean addToHumus) {
        if (addToHumus)
            inputAnnualStocksGCm2[index].humus += value * CarbonStock.factorMgHaToGCm2;
        else
            inputAnnualStocksGCm2[index].soil += value * CarbonStock.factorMgHaToGCm2;
    }

    public void Simulate() {
        // todo: use what input params ?  and what input npps ?

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
                SoilCarbonPredictorInput inputs = new SoilCarbonPredictorInput(SoilCarbonPredictorInput.LandType.MontmorencyForest
                        , inputStock.humus * annualFactor
                        , inputStock.soil * annualFactor
                        ,15,
                        4.22,
                        0.7918,
                        66.97,
                        3.80);

                for (int y = 0; y < deltaYear; y++) {
                    predictor.predictAnnualCStocks(compartments, inputs);
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
