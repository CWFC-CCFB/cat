package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 20 provides a modifier that represents the chemical controls of
 * the litter over the carbon use efficiency (CUE) of microbial activity.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq20 extends Equation {

	final Eq16 eq16;
	
	Eq20(SoilCarbonPredictor carbonModel, Eq16 eq16) {
		super(carbonModel);
		this.eq16 = eq16;
	}

	/**
	 * Calculate the modifier associated with chemical control.
	 * @param compartments a Matrix instance with the initial stocks in each compartment
	 * @param N_lit nitrogen concentration of input material
	 * @return the modifier
	 */
	double getModifier(Matrix compartments, double N_lit) {		
		return Math.min(1d / (1 + Math.exp(-carbonModel.N_max)*(N_lit - carbonModel.N_mid)), 
				Math.exp(-3 * eq16.getLCI(compartments)));
	}
	
}
