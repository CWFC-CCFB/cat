package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 26 provides the carbon stock transferred from compartment C2
 * to compartment C10.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq26 {

	static double getDailyCarbonStockTransferFromC2ToC10(SoilCarbonPredictor carbonModel, Matrix compartments) {
		double C2 = compartments.getValueAt(1, 0);
		return (1 - carbonModel.POM_split) * carbonModel.LIT_frg * C2; 
	}
}
