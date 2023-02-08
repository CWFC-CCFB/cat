package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 18 provides the input of C from compartment C2 to compartment C4 through microbial activity.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq18 {

	/**
	 * Return the C stock of C2 assimilated in C4 (C4<sup>C2</sup><sub>ass</sub>)
	 * @param compartments a Matrix instance with the initial stocks in each compartment
	 * @param N_lit
	 * @return a double
	 */
	static double getDailyCarbonStockTransferFromC2ToC4(SoilCarbonPredictor carbonModel, Matrix compartments, double N_lit) {
		return Eq19.getModifier(carbonModel, compartments, N_lit) * carbonModel.parmB2 * (1 - Eq21.getLeachingLA1(carbonModel, compartments, N_lit)) * 
				Eq20.getModifier(carbonModel, compartments, N_lit) * carbonModel.parmK1 * carbonModel.C1;
	}
}
