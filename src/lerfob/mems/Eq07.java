package lerfob.mems;

/**
 * Provide the daily change in compartment C7.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq07 extends Equation {

	final Eq38 eq38;
	final Eq39 eq39;
	final Eq40 eq40;
	final Eq41 eq41;
	final Eq42 eq42;
	final Eq43 eq43;
	final Eq44 eq44;
	final Eq45 eq45;
	
	Eq07(SoilCarbonPredictor carbonModel, 
			Eq38 eq38,
			Eq39 eq39,
			Eq40 eq40,
			Eq41 eq41,
			Eq42 eq42, 
			Eq43 eq43,
			Eq44 eq44,
			Eq45 eq45) {
		super(carbonModel);
		this.eq38 = eq38;
		this.eq39 = eq39;
		this.eq40 = eq40;
		this.eq41 = eq41;
		this.eq42 = eq42;
		this.eq43 = eq43;
		this.eq44 = eq44;
		this.eq45 = eq45;
	}

	/**
	 * Provide the daily change in the C stock in compartment C6.
	 * @return
	 */
	double getC7DailyChange(double CT_i, double f_sol, double C1, 
			double C2, 
			double C3, 
			double C4, 
			double C5, 
			double C6, 
			double C8,
			double C9,
			double C10,
			double N_lit) {
		return eq38.getCarbonMigrationFromC1ToC7(C1, C2, C3, N_lit) +
				eq39.getCarbonMigrationFromC2ToC7(C2, C3, N_lit) +
				eq40.getCarbonMigrationFromC3ToC7(C3) +
				eq41.getCarbonMigrationFromC4ToC7(C4) +
				eq42.getCarbonMigrationFromC5ToC7(C5) +
				eq43.getCarbonMigrationFromC8ToC7(C8) + 
				eq44.getCarbonMigrationFromC9ToC7(C9) +
				eq45.getCarbonMigrationFromC10ToC7(C10);
 	}
	
}
