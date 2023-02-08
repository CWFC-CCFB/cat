package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 2 represents the daily change in C stock in compartment C2.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq02 {

	/**
	 * Calculate the daily change in C stock in compartment C2.
	 * @param compartments a Matrix instance with the initial stocks in each compartment
	 * @param CT_i the daily input
	 * @param f_sol
	 * @param f_lig
	 * @param N_lit
	 * @return
	 */
	static double getDailyChangeC2(SoilCarbonPredictor carbonModel,
			Matrix compartment, 
			double CT_i, 
			double f_sol, 
			double f_lig, 
			double N_lit) {
		return Eq13.getDailyInputInC2(CT_i, f_sol, f_lig) - 
				Eq20.getModifier(carbonModel, compartment, N_lit) * carbonModel.C2 * carbonModel.parmK2 - 
				carbonModel.C2 * carbonModel.LIT_frg;
	}

}
