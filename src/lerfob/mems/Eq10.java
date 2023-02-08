package lerfob.mems;

import repicea.math.Matrix;

/**
 * Provide the daily change in compartment C10.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq10 {
	
	/**
	 * Provide the daily change in the C stock in compartment C10.
	 * @return
	 */
	static double getDailyChangeC10(SoilCarbonPredictor carbonModel, Matrix compartments) {
		double C10 = compartments.getValueAt(9, 0);
		return Eq26.getDailyCarbonStockTransferFromC2ToC10(carbonModel, compartments) + 
				Eq27.getDailyCarbonStockTransferFromC3ToC10(carbonModel, compartments) - 
				C10 * carbonModel.parmK10; 
				
 	}
	
}
