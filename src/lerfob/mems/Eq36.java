package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 36 provides sorption capacity.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq36 {

	static double getMaximumSorptionCapacityQ_max(double bulkDensity, double sandProportion, double rockProportion) {
		return bulkDensity * (0.26126 * (100 - sandProportion) + 11.07820) * (1 - rockProportion); // TODO check if 100 should be replaced by 1 before sand proportion
	}
	
}
