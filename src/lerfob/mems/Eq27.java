package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 26 provides the carbon stock transferred from compartment C3
 * to compartment C10.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq27 {


	static double getDailyCarbonStockTransferFromC3ToC10(SoilCarbonPredictor carbonModel, Matrix compartments) {
		double C3 = compartments.getValueAt(2, 0);
		return (1 - carbonModel.POM_split) * carbonModel.LIT_frg * C3; 
	}
}
