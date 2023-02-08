package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 4 provides the daily change in C stock in compartment C4.
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
	double getDailyChange(Matrix compartments, double N_lit) {
		double C4 = compartments.getValueAt(3, 0);
		return eq17.getC1AssimilatedInC4(compartments, N_lit) + 
				eq18.getC2AssimilatedInC4(compartments, N_lit) -
				C4 * carbonModel.parmK4;
 	}
	
}
