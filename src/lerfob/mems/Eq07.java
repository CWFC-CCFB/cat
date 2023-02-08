package lerfob.mems;

import repicea.math.Matrix;

/**
 * Provide the daily change in compartment C7.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq07 {

	/**
	 * Provide the daily change in the C stock in compartment C7.
	 * @return
	 */
	static double getDailyChangeC7(SoilCarbonPredictor carbonModel, Matrix compartments, double N_lit) {
		return Eq38.getDailyCarbonStockTransferFromC1ToC7(carbonModel, compartments, N_lit) +
				Eq39.getDailyCarbonStockTransferFromC2ToC7(carbonModel, compartments, N_lit) +
				Eq40.getDailyCarbonStockTransferFromC3ToC7(carbonModel, compartments) +
				Eq41.getDailyCarbonStockTransferFromC4ToC7(carbonModel, compartments) +
				Eq42.getDailyCarbonStockTransferFromC5ToC7(carbonModel, compartments) +
				Eq43.getDailyCarbonStockTransferFromC8ToC7(carbonModel, compartments) + 
				Eq44.getDailyCarbonStockTransferFromC9ToC7(carbonModel, compartments) +
				Eq45.getDailyCarbonStockTransferFromC10ToC7(carbonModel, compartments);
 	}
	
}
