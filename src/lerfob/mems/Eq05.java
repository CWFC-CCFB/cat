package lerfob.mems;

/**
 * Provide the daily change in compartment C5.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq05 extends Equation {

	final Eq23 eq23;
	final Eq24 eq24;
	final Eq25 eq25;
	
	Eq05(SoilCarbonPredictor carbonModel, Eq23 eq23, Eq24 eq24, Eq25 eq25) {
		super(carbonModel);
		this.eq23 = eq23;
		this.eq24 = eq24;
		this.eq25 = eq25;
	}

	/**
	 * Provide the daily change in the C stock in compartment C5.
	 * @param C2
	 * @param C3
	 * @param C4 
	 * @return
	 */
	double getC5DailyChange(double C2, double C3, double C4, double C5) {
		return eq23.getCarbonMigrationFromC4(C4) + eq24.getCarbonMigrationFromC2(C2) + 
				eq25.getCarbonMigrationFromC2(C3) - carbonModel.parmK5 * C5;
 	}
	
}
