package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 42 provides the carbon emissions from compartment C5.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq42 {

	static double getDailyCarbonStockTransferFromC5ToC7(SoilCarbonPredictor carbonModel, Matrix compartments) {
		double C5 = compartments.getValueAt(4, 0);
		return (1 - carbonModel.la_3) *
				carbonModel.parmK5 * C5;
	}
}
