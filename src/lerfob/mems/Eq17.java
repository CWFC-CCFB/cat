package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 17 provides the input of C from compartment C1 to compartment C4 through microbial activity.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq17 {
	
	/**
	 * Return the C stock of C1 assimilated in C4 (C4<sup>C1</sup><sub>ass</sub>)
	 * @param compartments a Matrix instance with the initial stocks in each compartment
	 * @param N_lit
	 * @return a double
	 */
	static double getDailyCarbonStockTransferFromC1ToC4(SoilCarbonPredictor carbonModel, Matrix compartments, double N_lit) {
		return Eq19.getModifier(carbonModel, compartments, N_lit) * carbonModel.parmB1 * (1 - Eq22.getLeachingLA4(carbonModel, compartments, N_lit)) * 
				Eq20.getModifier(carbonModel, compartments, N_lit) * carbonModel.parmK1 * carbonModel.C1;
	}
}
