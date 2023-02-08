package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 45 provides the carbon emissions from compartment C10.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq45 {

	static double getDailyCarbonStockTransferFromC10ToC7(SoilCarbonPredictor carbonModel,
			Matrix compartments) { 
		double C10 = compartments.getValueAt(9, 0);
		return (1 - carbonModel.la_3) *
				carbonModel.parmK3 * C10;
	}
}
