package lerfob.mems;

import repicea.math.Matrix;

/**
 * Provide the daily change in compartment C11.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq11 {
	/**
	 * Provide the daily change in the C stock in compartment C11.
	 * @return
	 */
	static double getDailyChangeC11(SoilCarbonPredictor carbonModel, Matrix compartments) {
		double C8 = compartments.getValueAt(7, 0);
		return C8 * carbonModel.DOC_lch; 
 	}
	
}
