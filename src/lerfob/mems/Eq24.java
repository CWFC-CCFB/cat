package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 24 provides the carbon stock transferred from compartment C2 
 * to compartment C5.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq24 {

	static double getDailyCarbonStockTransferFromC2ToC5(SoilCarbonPredictor carbonModel, Matrix compartments) { 
		double C2 = compartments.getValueAt(1, 0);
		return carbonModel.POM_split * carbonModel.LIT_frg * C2; 
	}
}
