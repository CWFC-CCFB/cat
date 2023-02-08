package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 38 provides the carbon emissions from compartment C1.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq38  {

	static double getDailyCarbonStockTransferFromC1ToC7(SoilCarbonPredictor carbonModel, Matrix compartments, double N_lit) { 
		double C1 = compartments.getValueAt(0, 0);
		return (1 - Eq19.getModifier(carbonModel, compartments, N_lit) * carbonModel.parmB1) *
				(1 - Eq22.getLeachingLA4(carbonModel, compartments, N_lit)) *
				Eq20.getModifier(carbonModel, compartments, N_lit) * carbonModel.parmK1 * C1;
	}
}
