package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 25 provides the carbon stock transferred from compartment C3 
 * to compartment C5.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq25 {

	static double getDailyCarbonStockTransferFromC3ToC5(SoilCarbonPredictor carbonModel, Matrix compartments) { // TODO definition of C3 must be clarified
		double C3 = compartments.getValueAt(2, 0);
		return carbonModel.POM_split * carbonModel.LIT_frg * C3; 
	}
}
