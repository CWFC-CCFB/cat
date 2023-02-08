package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 1 represents the daily change in carbon in compartment C1.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq01 {	
	/**
	 * Calculate the daily change in C stock in compartment C1.
	 * @param compartments a Matrix instance with the initial stocks in each compartment
	 * @param CT_i the daily input
	 * @param f_sol
	 * @param N_lit
	 * @param C3
	 * @param C2
	 * @return
	 */
	static double getDailyChangeC1(SoilCarbonPredictor carbonModel, 
			Matrix compartments, 
			double CT_i, 
			double f_sol, 
			double N_lit) {
		return Eq12.getDailyInputInC1(carbonModel, CT_i, f_sol) - 
				Eq20.getModifier(carbonModel, compartments, N_lit) * carbonModel.C1 * carbonModel.parmK1;
	}

}
