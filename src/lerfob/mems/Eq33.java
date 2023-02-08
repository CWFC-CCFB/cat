package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 33 provides the carbon stock transferred from compartment C6 
 * to compartment C8.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq33 {

	static double getDailyCarbonStockTransferFromC6ToC8(SoilCarbonPredictor carbonModel, Matrix compartments) { 
		double C6 = compartments.getValueAt(5,0);
		return carbonModel.DOC_frg * C6;
	}
}
