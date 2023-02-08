package lerfob.mems;

/**
 * Equation 14 provide the net daily input from external sources in compartment C3.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq14 {
	
	/**
	 * Return the input of C in compartment C3.
	 * @param CT_i the daily intake from source i
	 * @param f_lig fraction insoluble dans l'acide de l'apport de litière 
	 * @return the net input of C in compartment C3
	 */
	static double getDailyInputInC3(double CT_i, double f_lig) {
		return CT_i * f_lig;
	}
	
}
