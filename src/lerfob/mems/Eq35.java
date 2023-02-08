package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 35 provides the "binding affinity" factor.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq35 {

	static double getBindingAffinityL_k_lm(double soil_pH) {
		return Math.pow(10, -0.186 * soil_pH - 0.216);
	}
	
//	double getDefaultBindingAffinityL_k_lm() {
//		return carbonModel.L_k_lm;
//	}
}
