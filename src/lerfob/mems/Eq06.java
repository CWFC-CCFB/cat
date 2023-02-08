package lerfob.mems;

import repicea.math.Matrix;

/**
 * Provide the daily change in compartment C6.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq06 {
	
	/**
	 * Provide the daily change in the C stock in compartment C6.
	 * @return
	 */
	static double getDailyChangeC6(SoilCarbonPredictor carbonModel,
			Matrix compartments, 
			double CT_i, 
			double f_sol, 
			double N_lit) {
		return Eq15.calculate(carbonModel, CT_i, f_sol) +
				Eq28.getDailyCarbonStockTransferFromC1ToC6(carbonModel, compartments,  N_lit) +
				Eq29.getDailyCarbonStockTransferFromC2ToC6(carbonModel, compartments, N_lit) + 
				Eq30.getDailyCarbonStockTransferFromC3ToC6(carbonModel, compartments) + 
				Eq31.getDailyCarbonStockTransferFromC4ToC6(carbonModel, compartments) -
				Eq33.getDailyCarbonStockTransferFromC6ToC8(carbonModel, compartments);
 	}
	
}
