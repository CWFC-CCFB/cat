package lerfob.mems;

/**
 * Provide the daily change in compartment C4.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq04 extends Equation {

	final Eq17 eq17;
	final Eq18 eq18;
	
	Eq04(SoilCarbonPredictor carbonModel, Eq17 eq17, Eq18 eq18) {
		super(carbonModel);
		this.eq17 = eq17;
		this.eq18 = eq18;
	}

	/**
	 * Provide the daily change in the C stock in compartment C4.
	 * @param N_lit
	 * @param C3
	 * @param C2
	 * @param C4 Initial stock in compartment C4
	 * @return
	 */
	double getDailyChange(double N_lit, double C3, double C2, double C4) {
		return eq17.calculate(N_lit, C3, C2) + eq18.calculate(N_lit, C3, C2) -
				C4 * carbonModel.parmK4;
 	}
	
}
