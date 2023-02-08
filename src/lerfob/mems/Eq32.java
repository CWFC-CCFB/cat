package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 32 provides the carbon stock transferred from compartment C5 
 * to compartment C8.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq32 {

	/**
	 * Provide the daily carbon stock transfer from compartment C5 to compartment C8 (C8<sup>C5</sup><sub>in</sub>).
	 * @param compartments a Matrix instance with the initial stocks in each compartment
	 * @return
	 */
	static double getDailyCarbonStockTransferFromC5ToC8(SoilCarbonPredictor carbonModel, Matrix compartments) { 
		double C5 = compartments.getValueAt(4, 0);
		return carbonModel.la_3 * carbonModel.parmK5 * C5;
	}
}
