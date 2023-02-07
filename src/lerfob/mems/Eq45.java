package lerfob.mems;

/**
 * Equation 45 provides the carbon emissions from compartment C10.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq45 extends Equation {

	Eq45(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	double getCarbonMigrationFromC10ToC7(double C10) { 
		return (1 - carbonModel.la_3) *
				carbonModel.parmK3 * C10;
	}
}
