package lerfob.mems;

/**
 * Equation 25 provides the carbon stock transferred from compartment C3 
 * to compartment C5.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq25 extends Equation {

	Eq25(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	double getCarbonMigrationFromC2(double C3) { // TODO definition of C3 must be clarified
		return carbonModel.POM_split * carbonModel.LIT_frg * C3; 
	}
}
