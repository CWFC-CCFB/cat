package lerfob.mems;

/**
 * Equation 44 provides the carbon emissions from compartment C9.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq44 extends Equation {

	Eq44(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	double getCarbonMigrationFromC9ToC7(double C9) { 
		return carbonModel.parmK9 * C9;
	}
}
