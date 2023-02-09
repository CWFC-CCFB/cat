package lerfob.mems;

import repicea.math.Matrix;
import repicea.simulation.HierarchicalLevel;
import repicea.simulation.MonteCarloSimulationCompliantObject;

import java.security.InvalidParameterException;

public class SoilCarbonPredictorCompartment implements MonteCarloSimulationCompliantObject {
    public static enum CompartmentID {
        C1,
        C2,
        C3,
        C4,
        C5,
        C6,
        C7,
        C8,
        C9,
        C10,
        C11;
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
