package lerfob.mems;

/**
 * Equation 12 provides the daily input of hot-water extractible carbon from the
 * litter. 
 * @author Mathieu Fortin - Feb 2023
 *
 */
class Eq12 extends Equation {

	Eq12(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	/**
	 * Calculate the daily input in compartment C1.
	 * 
	 * @param CT_i the daily input from external source i
	 * @param f_sol the extractible faction 
	 * @return the input of hot-water extractible carbon in compartment C1
	 */
	double getDailyInputInC1(double CT_i, double f_sol) {
		return CT_i * f_sol * (1 - carbonModel.f_DOC);
	}

}
