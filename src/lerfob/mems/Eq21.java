package lerfob.mems;

import repicea.math.Matrix;

// TODO clarify the terminology here
/**
 * Equation 21 provides the leaching factor...  
 * @author Mathieu Fortin - Feb 2023
 */
class Eq21 extends Equation {

	final Eq16 eq16;
	
	Eq21(SoilCarbonPredictor carbonModel, Eq16 eq16) {
		super(carbonModel);
		this.eq16 = eq16;
	}

	/**
	 * Provide the leaching factor.
	 * @param N_lit the nitrogen content of the input material
	 * @param C3
	 * @param C2
	 * @return
	 */
	double getLeachingLA1(Matrix compartments, double N_lit) {
		return Math.min(carbonModel.E_Hmax - (carbonModel.E_Hmax - carbonModel.E_Hmin)/carbonModel.LCI_max * eq16.getLCI(compartments), 
				carbonModel.E_Hmax - (carbonModel.E_Hmax - carbonModel.E_Hmin) / carbonModel.N_max * N_lit);
	}
}
