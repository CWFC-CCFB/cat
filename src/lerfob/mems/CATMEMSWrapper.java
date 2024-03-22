package lerfob.mems;

public class CATMEMSWrapper {
    public class CarbonStock {
        public CarbonStock(double humus, double soil) {
            this.humus = humus;
            this.soil = soil;
        }

        public double humus;
        public double soil;
    }
    CarbonStock[] inputAnnualStocks;
    CarbonStock[] outputAnnualStocks;
    SoilCarbonPredictor predictor;
    SoilCarbonPredictorCompartments compartments;
    public void PrepareSimulation(int nbYears) {
        // run a simulation to reach stability into compartment bins
        // todo: use what input params ?  and what input npps ?

        double MAT = 3.8;  // between Jan 1 2013 to Dec 31st 2016 at MM
        double MinTemp = -9.48; // between Jan 1 2013 to Dec 31st 2016 at MM
        double MaxTemp = 17.79;  // between Jan 1 2013 to Dec 31st 2016 at MM
        double Trange = MaxTemp - MinTemp;

        compartments = new SoilCarbonPredictorCompartments(1.0, MAT, Trange);

        SoilCarbonPredictorInput inputs = new SoilCarbonPredictorInput(SoilCarbonPredictorInput.LandType.MontmorencyForest, 304.0, 54.72, 15, 4.22, 0.7918, 66.97, 3.80);
        predictor = new SoilCarbonPredictor(false);
        for (int i = 0; i < 1000; i++) {
            predictor.predictAnnualCStocks(compartments, inputs);
        }

        // prepare the carbon stock array
        inputAnnualStocks = new CarbonStock[nbYears];
        outputAnnualStocks = new CarbonStock[nbYears];
        for (int i = 0; i < nbYears; i++) {
            inputAnnualStocks[i] = new CarbonStock(0.0, 0.0);
            outputAnnualStocks[i] = new CarbonStock(0.0, 0.0);
        }
    }

    public void AddCarbonInputToHumus(int year, double value) {
        inputAnnualStocks[year].humus += value;
    }

    public void AddCarbonInputToSoil(int year, double value) {
        inputAnnualStocks[year].soil += value;
    }

    public void Simulate() {
        // todo: use what input params ?  and what input npps ?

        for (int i = 0; i < inputAnnualStocks.length; i++) {
            CarbonStock inputStock = inputAnnualStocks[i];
            SoilCarbonPredictorInput inputs = new SoilCarbonPredictorInput(SoilCarbonPredictorInput.LandType.MontmorencyForest, inputStock.humus, inputStock.soil, 15, 4.22, 0.7918, 66.97, 3.80);
            predictor.predictAnnualCStocks(compartments, inputs);
            CarbonStock outputStock = outputAnnualStocks[i];
            outputStock.humus = compartments.getLitterBinsgCm2();
            outputStock.soil = compartments.getSoilBinsgCm2();
        }
    }

    public CarbonStock GetCarbonOutput(int year) {
        return outputAnnualStocks[year];
    }
}
