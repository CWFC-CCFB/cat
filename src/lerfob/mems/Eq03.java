package lerfob.mems;

/**
 * Equation 3 represents the daily change in C stock in compartment C3.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq03 extends Equation {

	final Eq14 eq14;
	
	Eq03(SoilCarbonPredictor carbonModel, Eq14 eq14) {
		super(carbonModel);
		this.eq14 = eq14;
	}

	double getDailyChangeC3(double CT_i, double f_lig) {
		return eq14.getDailyInputInC3(CT_i, f_lig) - 
				carbonModel.C3 * carbonModel.parmK3 - 
				carbonModel.C3 * carbonModel.LIT_frg;
 	}
}
