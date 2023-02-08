package lerfob.mems;

/**
 * Equation 3 represents the daily change in C stock in compartment C3.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq03 {

	static double getDailyChangeC3(SoilCarbonPredictor carbonModel, double CT_i, double f_lig) {
		return Eq14.getDailyInputInC3(CT_i, f_lig) - 
				carbonModel.C3 * carbonModel.parmK3 - 
				carbonModel.C3 * carbonModel.LIT_frg;
 	}
}
