package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 41 provides the carbon emissions from compartment C4.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq41 {

	static double getDailyCarbonStockTransferFromC4ToC7(SoilCarbonPredictor carbonModel, Matrix compartments) { 
		double C4 = compartments.getValueAt(3, 0);
		return (1 - carbonModel.parmB3) * 
				(1 - carbonModel.la_2) *
				carbonModel.parmK4 * C4;
	}
}
