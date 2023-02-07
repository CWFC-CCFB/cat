package lerfob.mems;

class Eq14 extends Equation {

	Eq14(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	/**
	 * Return the input of C in compartment C3.
	 * @param CT_i the daily intake from source i
	 * @param f_lig fraction insoluble dans l'acide de l'apport de litière 
	 * @return the net input of C in compartment C3
	 */
	double calculate(double CT_i, double f_lig) {
		return CT_i * f_lig;
	}
	
}
