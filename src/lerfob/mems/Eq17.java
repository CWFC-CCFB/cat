package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 17 provides the input of C from compartment C1 to compartment C4 through microbial activity.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq17 extends Equation {

	final Eq19 eq19;
	final Eq20 eq20;
	final Eq22 eq22;
	
	Eq17(SoilCarbonPredictor carbonModel, Eq19 eq19, Eq20 eq20, Eq22 eq22) {
		super(carbonModel);
		this.eq19 = eq19;
		this.eq20 = eq20;
		this.eq22 = eq22;
	}

	/**
	 * Return the C stock of C1 assimilated in C4 (C4<sup>C1</sup><sub>ass</sub>)
	 * @param compartments a Matrix instance with the initial stocks in each compartment
	 * @param N_lit
	 * @return a double
	 */
	double getC1AssimilatedInC4(Matrix compartments, double N_lit) {
		return eq19.getModifier(compartments, N_lit) * carbonModel.parmB1 * (1 - eq22.getLeachingLA4(compartments, N_lit)) * 
				eq20.getModifier(compartments, N_lit) * carbonModel.parmK1 * carbonModel.C1;
	}
}
