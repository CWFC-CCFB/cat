package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 28 provides the carbon stock transferred from compartment C1 
 * to compartment C6.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq28 {
	
	static double getDailyCarbonStockTransferFromC1ToC6(SoilCarbonPredictor carbonModel, Matrix compartments, double N_lit) { 
		double C1 = compartments.getValueAt(0, 0);
		return Eq22.getLeachingLA4(carbonModel, compartments, N_lit) * 
				Eq20.getModifier(carbonModel, compartments, N_lit) * 
				carbonModel.parmK1 * C1;
	}
}
