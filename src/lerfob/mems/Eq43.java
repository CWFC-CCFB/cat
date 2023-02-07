package lerfob.mems;

/**
 * Equation 43 provides the carbon emissions from compartment C8.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq43 extends Equation {

	Eq43(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	double getCarbonMigrationFromC8ToC7(double C8) { 
		return carbonModel.parmK8 * C8;
	}
}
