package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 18 provides the input of C from compartment C2 to compartment C4 through microbial activity.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq18 extends Equation {

	final Eq19 eq19;
	final Eq20 eq20;
	final Eq21 eq21;
	
	Eq18(SoilCarbonPredictor carbonModel, Eq19 eq19, Eq20 eq20, Eq21 eq21) {
		super(carbonModel);
		this.eq19 = eq19;
		this.eq20 = eq20;
		this.eq21 = eq21;
	}

	/**
	 * Return the C stock of C2 assimilated in C4 (C4<sup>C2</sup><sub>ass</sub>)
	 * @param compartments a Matrix instance with the initial stocks in each compartment
	 * @param N_lit
	 * @return a double
	 */
	double getC2AssimilatedInC4(Matrix compartments, double N_lit) {
		return eq19.getModifier(compartments, N_lit) * carbonModel.parmB2 * (1 - eq21.getLeachingLA1(compartments, N_lit)) * 
				eq20.getModifier(compartments, N_lit) * carbonModel.parmK1 * carbonModel.C1;
	}
}
