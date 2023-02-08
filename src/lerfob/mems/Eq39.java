package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 39 provides the carbon emissions from compartment C2.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq39 {

	static double getDailyCarbonStockTransferFromC2ToC7(SoilCarbonPredictor carbonModel, Matrix compartments, double N_lit) { 
		double C2 = compartments.getValueAt(1, 0);
		return (1 - Eq19.getModifier(carbonModel, compartments, N_lit) * carbonModel.parmB2) *
				(1 - Eq21.getLeachingLA1(carbonModel, compartments, N_lit)) *
				Eq20.getModifier(carbonModel, compartments, N_lit) * carbonModel.parmK2 * C2;
	}
}
