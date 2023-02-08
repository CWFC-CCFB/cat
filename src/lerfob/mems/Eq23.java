package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 23 provides the carbon stock transferred from compartment C4 
 * to compartment C5.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq23 {

	static double getDailyCarbonStockTransferFromC4ToC5(SoilCarbonPredictor carbonModel, Matrix compartments) {
		double C4 = compartments.getValueAt(3,0);
		return carbonModel.parmB3 * (1 - carbonModel.la_2) * carbonModel.parmK4 * C4;
	}
}
