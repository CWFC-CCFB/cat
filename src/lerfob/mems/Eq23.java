package lerfob.mems;

/**
 * Equation 23 provides the carbon stock transferred from compartment C4 
 * to compartment C5.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq23 extends Equation {

	Eq23(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	double getCarbonMigrationFromC4(double C4) {
		return carbonModel.parmB3 * (1 - carbonModel.la_2) * carbonModel.parmK4 * C4;
	}
}
