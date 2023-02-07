package lerfob.mems;

class Eq03 extends Equation {

	final Eq14 eq14;
	
	Eq03(SoilCarbonPredictor carbonModel, Eq14 eq14) {
		super(carbonModel);
		this.eq14 = eq14;
	}

	double calculate(double CT_i, double f_lig) {
		return eq14.calculate(CT_i, f_lig) - carbonModel.C3 * carbonModel.parmK3 - carbonModel.C3 * carbonModel.LIT_frg;
 	}
}
