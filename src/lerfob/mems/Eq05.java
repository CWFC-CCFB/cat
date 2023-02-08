package lerfob.mems;

import repicea.math.Matrix;

/**
 * Provide the daily change in compartment C5.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq05 {
	
	/**
	 * Provide the daily change in the C stock in compartment C5.
	 * @param C2
	 * @param C3
	 * @param C4 
	 * @return
	 */
	static double getDailyChangeC5(SoilCarbonPredictor carbonModel, Matrix compartments) {
		double C5 = compartments.getValueAt(4, 0);
		return Eq23.getDailyCarbonStockTransferFromC4ToC5(carbonModel, compartments) + 
				Eq24.getDailyCarbonStockTransferFromC2ToC5(carbonModel, compartments) + 
				Eq25.getDailyCarbonStockTransferFromC3ToC5(carbonModel, compartments) - 
				carbonModel.parmK5 * C5;
 	}
	
}
