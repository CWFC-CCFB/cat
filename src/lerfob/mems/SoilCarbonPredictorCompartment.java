package lerfob.mems;

import repicea.simulation.HierarchicalLevel;
import repicea.simulation.MonteCarloSimulationCompliantObject;

import java.security.InvalidParameterException;

public class SoilCarbonPredictorCompartment implements MonteCarloSimulationCompliantObject {
    public static final int C1 = 0;     // Water soluble litter
    public static final int C2 = 1;     // Acid-soluble litter
    public static final int C3 = 2;     // Acid-insoluble litter
    public static final int C4 = 3;     // Microbial biomass
    public static final int C5 = 4;     // Coarse, heavy POM
    public static final int C6 = 5;     // Litter layer DOM
    public static final int C7 = 6;     // Emitted CO2
    public static final int C8 = 7;     // Soil layer DOM
    public static final int C9 = 8;     // Mineral-associated OM
    public static final int C10 = 9;    // Light POM
    public static final int C11 = 10;   // Leached DOM
    double[] bins;
    private int realizationID;
    public SoilCarbonPredictorCompartment(double[] initialStock) {
        if (initialStock.length != 11)
            throw new InvalidParameterException();

        bins = initialStock;
    }
    public SoilCarbonPredictorCompartment(double initialValue) {
        bins = new double[11];
        for (int i = 0; i < bins.length; i++)
            bins[i] = initialValue;
    }

    public double getSum() {
        double sum = 0.0;
        for (int i = 0; i < bins.length; i++)
            sum += bins[i];
        return sum;
    }

    public void add(SoilCarbonPredictorCompartment o) {
        for (int i = 0; i < bins.length; i++)
            bins[i] += o.bins[i];
    }

    @Override
    public String getSubjectId() {
        return null;
    }

    @Override
    public HierarchicalLevel getHierarchicalLevel() {
        return null;
    }

    @Override
    public int getMonteCarloRealizationId() {
        return realizationID;
    }
}
