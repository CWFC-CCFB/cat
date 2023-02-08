package lerfob.mems;

import repicea.math.Matrix;

/**
 * Provide the daily change in compartment C6.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq06 extends Equation {

	final Eq15 eq15;
	final Eq28 eq28;
	final Eq29 eq29;
	final Eq30 eq30;
	final Eq31 eq31;
	final Eq33 eq33;
	
	Eq06(SoilCarbonPredictor carbonModel, 
			Eq15 eq15,
			Eq28 eq28,
			Eq29 eq29,
			Eq30 eq30,
			Eq31 eq31, 
			Eq33 eq33) {
		super(carbonModel);
		this.eq15 = eq15;
		this.eq28 = eq28;
		this.eq29 = eq29;
		this.eq30 = eq30;
		this.eq31 = eq31;
		this.eq33 = eq33;
	}

	/**
	 * Provide the daily change in the C stock in compartment C6.
	 * @return
	 */
	double getC6DailyChange(Matrix compartments, double CT_i, double f_sol, double N_lit) {
		double C3 = compartments.getValueAt(2, 0);
		double C4 = compartments.getValueAt(3, 0);
		double C6 = compartments.getValueAt(5, 0);
		return eq15.calculate(CT_i, f_sol) +
				eq28.getCarbonMigrationFromC1(compartments,  N_lit) +
				eq29.getCarbonMigrationFromC2(compartments, N_lit) + 
				eq30.getCarbonMigrationFromC3(C3) + 
				eq31.getCarbonMigrationFromC4(C4) -
				eq33.getCarbonMigrationToC8(C6);
 	}
	
}
