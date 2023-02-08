package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 4 provides the daily change in C stock in compartment C4.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq04 {

	/**
	 * Provide the daily change in the C stock in compartment C4.
	 * @param N_lit
	 * @param C3
	 * @param C2
	 * @param C4 Initial stock in compartment C4
	 * @return
	 */
	static double getDailyChangeC4(SoilCarbonPredictor carbonModel, Matrix compartments, double N_lit) {
		double C4 = compartments.getValueAt(3, 0);
		return Eq17.getDailyCarbonStockTransferFromC1ToC4(carbonModel, compartments, N_lit) + 
				Eq18.getDailyCarbonStockTransferFromC2ToC4(carbonModel, compartments, N_lit) -
				C4 * carbonModel.parmK4;
 	}
	
}
