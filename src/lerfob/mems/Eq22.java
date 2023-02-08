package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 22 provides the proportion of leached carbon from DOM??? 
 * @author Mathieu Fortin - Feb 2023
 */
class Eq22 {


	//TODO check what that is?
	/**
	 * Provide the leaching factor.
	 * @param compartments a Matrix instance with the initial stocks in each compartment
	 * @param N_lit the nitrogen content in the input material
	 * @return
	 */
	static double getLeachingLA4(SoilCarbonPredictor carbonModel, Matrix compartments, double N_lit) {
		return Math.min(carbonModel.E_smax - (carbonModel.E_smax - carbonModel.E_smin) / carbonModel.LCI_max * Eq16.getLCI(compartments), 
				carbonModel.E_smax - (carbonModel.E_smax - carbonModel.E_smin) / carbonModel.N_max * N_lit);
	}
}
