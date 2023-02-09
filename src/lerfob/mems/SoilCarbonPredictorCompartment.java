package lerfob.mems;

import repicea.math.Matrix;
import repicea.simulation.HierarchicalLevel;
import repicea.simulation.MonteCarloSimulationCompliantObject;

import java.security.InvalidParameterException;

public class SoilCarbonPredictorCompartment implements MonteCarloSimulationCompliantObject {
    public static enum CompartmentID {
        C1,     // Water soluble litter
        C2,     // Acid-soluble litter
        C3,     // Acid-insoluble litter
        C4,     // Microbial biomass
        C5,     // Coarse, heavy POM
        C6,     // Litter layer DOM
        C7,     // Emitted CO2
        C8,     // Soil layer DOM
        C9,     // Mineral-associated OM
        C10,    // Light POM
        C11;    // Leached DOM
    }
    Matrix compartments;
    private int realizationID;
    public SoilCarbonPredictorCompartment(Matrix initialStock) {
        if (initialStock.m_iRows != 11 || initialStock.m_iCols != 1)
            throw new InvalidParameterException();

        compartments = initialStock;
    }
    public SoilCarbonPredictorCompartment(SoilCarbonPredictorCompartment o) {
        compartments = o.compartments.getDeepClone();
    }


    public double getStock(CompartmentID id) {
        return compartments.getValueAt(id.ordinal(), 0);
    }

    public void setStock(CompartmentID id, double value) {
        compartments.setValueAt(id.ordinal(), 0, value);
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
