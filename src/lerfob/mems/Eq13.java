package lerfob.mems;

/**
 * Equation 13 provide the net daily input from external sources in compartment C2.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq13 {
	
	/**
	 * Calculate the daily input in compartment C2.
	 * @param CT_i the input from external source i
	 * @param f_sol la fraction extractible à l'eau chaude de l'apport de litière
	 * @param f_lig fraction insoluble dans l'acide de l'apport de litière 
	 * @return the daily input in compartment C2
	 */
	static double getDailyInputInC2(double CT_i, double f_sol, double f_lig) {
		return CT_i - (CT_i * (f_sol + f_lig));
	}
}
