package lerfob.mems;

import repicea.math.Matrix;

// TODO clarify the terminology here
/**
 * Equation 21 provides the leaching factor...  
 * @author Mathieu Fortin - Feb 2023
 */
class Eq21 {

	/**
	 * Provide the leaching factor.
	 * @param N_lit the nitrogen content of the input material
	 * @return
	 */
	static double getLeachingLA1(SoilCarbonPredictor carbonModel, Matrix compartments, double N_lit) {
		return Math.min(carbonModel.E_Hmax - (carbonModel.E_Hmax - carbonModel.E_Hmin)/carbonModel.LCI_max * Eq16.getLCI(compartments), 
				carbonModel.E_Hmax - (carbonModel.E_Hmax - carbonModel.E_Hmin) / carbonModel.N_max * N_lit);
	}
}
