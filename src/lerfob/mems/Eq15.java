package lerfob.mems;

/**
 * Provide the transfer of C from the litter to compartment C6.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq15 extends Equation {

	Eq15(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	/**
	 * Return the input of C in compartment C6.
	 * @param CT_i the daily intake from source i
	 * @param f_sol hot water extractible fraction
	 * @return the net input of C in compartment C3
	 */
	double calculate(double CT_i, double f_sol) {
		return CT_i * f_sol * carbonModel.f_DOC;
	}
	
}
