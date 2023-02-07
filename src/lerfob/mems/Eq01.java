package lerfob.mems;

class Eq01 extends Equation {

	final Eq12 eq12;
	final Eq20 eq20;

	Eq01(SoilCarbonPredictor carbonModel, Eq12 eq12, Eq20 eq20) {
		super(carbonModel);
		this.eq12 = eq12;
		this.eq20 = eq20;
	}
	
	/**
	 * Calculate the daily change in C stock in compartment C1.
	 * @param CT_i the daily input
	 * @param f_sol
	 * @param N_lit
	 * @param C3
	 * @param C2
	 * @return
	 */
	double calculate(double CT_i, double f_sol, double N_lit, double C3, double C2) {
		return eq12.calculate(CT_i, f_sol) - eq20.calculate(N_lit, C3, C2) * carbonModel.C1 * carbonModel.parmK1;
	}

}
