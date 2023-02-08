package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 37 provides the sorption.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq37 {

	static double getSorption(Matrix compartments, 
			double soil_pH, 
			double bulkDensity, 
			double sandProportion, 
			double rockProportion) { 
		double C8 = compartments.getValueAt(7,0);
		double C9 = compartments.getValueAt(8,0);
		double L_k_lm = Eq35.getBindingAffinityL_k_lm(soil_pH);
		double q_max = Eq36.getMaximumSorptionCapacityQ_max(bulkDensity, sandProportion, rockProportion);
		return C8 * (L_k_lm * q_max * C8 / (1 + L_k_lm * C8) - C9) / q_max;
	}
}
