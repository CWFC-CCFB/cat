package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 2 represents the daily change in C stock in compartment C2.
 * @author Mathieu Fortin - Feb 2023
 */
public class Eq02 extends Equation {

	final Eq13 eq13;
	final Eq20 eq20;
	
	Eq02(SoilCarbonPredictor carbonModel, Eq13 eq13, Eq20 eq20) {
		super(carbonModel);
		this.eq13 = eq13;
		this.eq20 = eq20;
	}

	/**
	 * Calculate the daily change in C stock in compartment C2.
	 * @param compartments a Matrix instance with the initial stocks in each compartment
	 * @param CT_i the daily input
	 * @param f_sol
	 * @param f_lig
	 * @param N_lit
	 * @return
	 */
	double getDailyChangeC2(Matrix compartment, 
			double CT_i, 
			double f_sol, 
			double f_lig, 
			double N_lit) {
		return eq13.getDailyInputInC2(CT_i, f_sol, f_lig) - 
				eq20.getModifier(compartment, N_lit) * carbonModel.C2 * carbonModel.parmK2 - 
				carbonModel.C2 * carbonModel.LIT_frg;
	}

}
