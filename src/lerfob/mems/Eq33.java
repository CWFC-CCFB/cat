package lerfob.mems;

/**
 * Equation 33 provides the carbon stock transferred from compartment C6 
 * to compartment C8.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq33 extends Equation {

	Eq33(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	double getCarbonMigrationToC8(double C6) { 
		return carbonModel.DOC_frg * C6;
	}
}
