package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 43 provides the carbon emissions from compartment C8.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq43 extends Equation {

	Eq43(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	double getCarbonMigrationFromC8ToC7(Matrix compartments) { 
		double C8 = compartments.getValueAt(7, 0);
		return carbonModel.parmK8 * C8;
	}
}
