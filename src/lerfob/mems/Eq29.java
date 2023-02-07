package lerfob.mems;

/**
 * Equation 29 provides the carbon stock transferred from compartment C2 
 * to compartment C6.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq29 extends Equation {

	final Eq20 eq20;
	final Eq21 eq21;
	
	Eq29(SoilCarbonPredictor carbonModel, Eq20 eq20, Eq21 eq21) {
		super(carbonModel);
		this.eq20 = eq20;
		this.eq21 = eq21;
	}

	double getCarbonMigrationFromC2(double N_lit, double C3, double C2) { 
		return eq21.calculate(N_lit, C3, C2) * eq20.calculate(N_lit, C3, C2) * carbonModel.parmK2 * C2;
	}
}
