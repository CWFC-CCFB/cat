package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 34 provides the carbon stock transferred from compartment C10 
 * to compartment C8.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq34 {

	static double getDailyCarbonStockTransferFromC10ToC8(SoilCarbonPredictor carbonModel, Matrix compartments) { 
		double C10 = compartments.getValueAt(9,0);
		return carbonModel.la_3 * carbonModel.parmK10 * C10;
	}
}
