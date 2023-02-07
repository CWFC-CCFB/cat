package lerfob.mems;

/**
 * Equation 24 provides the carbon stock transferred from compartment C2 
 * to compartment C5.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq24 extends Equation {

	Eq24(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	double getCarbonMigrationFromC2(double C2) { // TODO definition of C2 must be clarified
		return carbonModel.POM_split * carbonModel.LIT_frg * C2; 
	}
}
