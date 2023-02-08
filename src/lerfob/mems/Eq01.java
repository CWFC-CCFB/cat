package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 1 represents the daily change in carbon in compartment C1.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq01 extends Equation {

	final Eq12 eq12;
	final Eq20 eq20;

	Eq01(SoilCarbonPredictor carbonModel, Eq12 eq12, Eq20 eq20) {
		super(carbonModel);
		this.eq12 = eq12;
		this.eq20 = eq20;
	}
	
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
	double getDailyChangeC1(Matrix compartments, 
			double CT_i, 
			double f_sol, 
			double N_lit) {
		return eq12.getDailyInputInC1(CT_i, f_sol) - 
				eq20.getModifier(compartments, N_lit) * carbonModel.C1 * carbonModel.parmK1;
	}

}
