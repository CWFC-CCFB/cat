package lerfob.mems;

public class Eq02 extends Equation {

	final Eq13 eq13;
	final Eq20 eq20;
	
	Eq02(SoilCarbonPredictor carbonModel, Eq13 eq13, Eq20 eq20) {
		super(carbonModel);
		this.eq13 = eq13;
		this.eq20 = eq20;
	}

	double calculate(double CT_i, double f_sol, double f_lig, double N_lit, double C3, double C2) {
		return eq13.calculate(CT_i, f_sol, f_lig) - eq20.calculate(N_lit, C3, C2) * carbonModel.C2 * carbonModel.parmK2 - carbonModel.C2 * carbonModel.LIT_frg;
	}

}
