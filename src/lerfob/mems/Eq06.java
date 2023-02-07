package lerfob.mems;

/**
 * Provide the daily change in compartment C6.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq06 extends Equation {

	final Eq15 eq15;
	final Eq28 eq28;
//	final Eq29 eq29;
//	final Eq30 eq30;
//	final Eq31 eq31;
//	final Eq33 eq33;
	
	Eq06(SoilCarbonPredictor carbonModel, 
			Eq15 eq15,
			Eq28 eq28
//			Eq29 eq29,
//			Eq30 eq30,
//			Eq31 eq31, 
//			Eq33 eq33
			) {
		super(carbonModel);
		this.eq15 = eq15;
		this.eq28 = eq28;
//		this.eq29 = eq29;
//		this.eq30 = eq30;
//		this.eq31 = eq31;
//		this.eq33 = eq33;
	}

	/**
	 * Provide the daily change in the C stock in compartment C6.
	 * @return
	 */
	double getC6DailyChange(double CT_i, double f_sol, double C1, double C2, double C3, double N_lit) {
		return eq15.calculate(CT_i, f_sol) * eq28.getCarbonMigrationFromC1(C1,  N_lit,  C3,  C2);
 	}
	
}
