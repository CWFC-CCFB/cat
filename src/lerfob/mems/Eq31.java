package lerfob.mems;

/**
 * Equation 31 provides the carbon stock transferred from compartment C4 
 * to compartment C6.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq31 extends Equation {

	Eq31(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	double getCarbonMigrationFromC4(double C4) { 
		return carbonModel.la_2 * carbonModel.parmK4 * C4;
	}
}
