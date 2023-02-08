package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 44 provides the carbon emissions from compartment C9.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq44 extends Equation {

	Eq44(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	double getCarbonMigrationFromC9ToC7(Matrix compartments) {
		double C9 = compartments.getValueAt(8, 0);
		return carbonModel.parmK9 * C9;
	}
}
