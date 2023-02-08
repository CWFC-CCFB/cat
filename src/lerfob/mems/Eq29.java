package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 29 provides the carbon stock transferred from compartment C2 
 * to compartment C6.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq29 {

	static double getDailyCarbonStockTransferFromC2ToC6(SoilCarbonPredictor carbonModel, Matrix compartments, double N_lit) { 
		double C2 = compartments.getValueAt(1, 0);
		return Eq21.getLeachingLA1(carbonModel, compartments, N_lit) * 
				Eq20.getModifier(carbonModel, compartments, N_lit) * 
				carbonModel.parmK2 * C2;
	}
}
