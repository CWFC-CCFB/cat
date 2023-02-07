package lerfob.mems;

class Eq13 extends Equation {

	
	Eq13(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	/**
	 * Calculate the daily input in compartment C2.
	 * @param CT_i the input from external source i
	 * @param f_sol la fraction extractible � l'eau chaude de l'apport de liti�re
	 * @param f_lig fraction insoluble dans l'acide de l'apport de liti�re 
	 * @return the daily input in compartment C2
	 */
	double calculate(double CT_i, double f_sol, double f_lig) {
		return CT_i - (CT_i * (f_sol + f_lig));
	}
}
