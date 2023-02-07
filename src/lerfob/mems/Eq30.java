package lerfob.mems;

/**
 * Equation 30 provides the carbon stock transferred from compartment C3 
 * to compartment C6.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq30 extends Equation {

	Eq30(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	double getCarbonMigrationFromC3(double C3) { 
		return carbonModel.la_3 * carbonModel.parmK3 * C3;
	}
}
