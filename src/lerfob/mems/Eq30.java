package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 30 provides the carbon stock transferred from compartment C3 
 * to compartment C6.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq30 {

	static double getDailyCarbonStockTransferFromC3ToC6(SoilCarbonPredictor carbonModel, Matrix compartments) {
		double C3 = compartments.getValueAt(2, 0);
		return carbonModel.la_3 * carbonModel.parmK3 * C3;
	}
}
