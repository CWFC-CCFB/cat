package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 40 provides the carbon emissions from compartment C3.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq40 extends Equation {

	Eq40(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	double getCarbonMigrationFromC3ToC7(Matrix compartments) {
		double C3 = compartments.getValueAt(2, 0);
		return (1 - carbonModel.la_3) *
					carbonModel.parmK3 * C3;
	}
}
