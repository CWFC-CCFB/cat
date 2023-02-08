package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 31 provides the carbon stock transferred from compartment C4 
 * to compartment C6.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq31 {

	static double getDailyCarbonStockTransferFromC4ToC6(SoilCarbonPredictor carbonModel, Matrix compartments) {
		double C4 = compartments.getValueAt(3, 0);
		return carbonModel.la_2 * carbonModel.parmK4 * C4;
	}
}
