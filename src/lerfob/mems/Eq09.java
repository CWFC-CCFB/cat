package lerfob.mems;

import repicea.math.Matrix;

/**
 * Provide the daily change in compartment C9.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq09 {

	/**
	 * Provide the daily change in the C stock in compartment C9.
	 * @return
	 */
	static double getDailyChangeC9(SoilCarbonPredictor carbonModel,
			Matrix compartments, 
			double N_lit,
			double soil_pH,
			double bulkDensity,
			double sandProportion,
			double rockProportion) {
		double C9 = compartments.getValueAt(8, 0);
		return Eq37.getSorption(compartments, soil_pH, bulkDensity, sandProportion, rockProportion) - 
				C9 * carbonModel.parmK9; 
				
 	}
	
}
