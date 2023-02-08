package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 19 provides the modifier for the chemical control of the litter over the 
 * carbon use efficiency.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq19 {

	/**
	 * Provide the daily modifier for the chemical control of the litter over the 
	 * carbon use efficiency.
	 * @param compartments a Matrix instance with the initial stocks in each compartment
	 * @param N_lit nitrogen concentration of the input material
	 * @return
	 */
	static double getModifier(SoilCarbonPredictor carbonModel, Matrix compartments, double N_lit) {
		return Math.min(1d / (1 + Math.exp(carbonModel.N_max) * (N_lit - carbonModel.N_mid)), 
				1 - Math.exp(-0.7 * (Math.abs(Eq16.getLCI(compartments) - 0.7) * 10)));
	}
	
}
