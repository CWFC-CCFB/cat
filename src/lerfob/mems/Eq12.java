package lerfob.mems;

class Eq12 extends Equation {

	Eq12(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	/**
	 * Calculate the input in compartment C1.
	 * 
	 * @param CT_i the daily input from external source i
	 * @param la fraction extractible à l'eau chaude de l'apport de litière  
	 * @return the input the compartment C1
	 */
	double calculate(double CT_i, double f_sol) {
		return CT_i * f_sol * (1 - carbonModel.f_DOC);
	}

}
