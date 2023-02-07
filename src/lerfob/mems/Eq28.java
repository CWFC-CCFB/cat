package lerfob.mems;

/**
 * Equation 28 provides the carbon stock transferred from compartment C1 
 * to compartment C6.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq28 extends Equation {

	final Eq20 eq20;
	final Eq22 eq22;
	
	Eq28(SoilCarbonPredictor carbonModel, Eq20 eq20, Eq22 eq22) {
		super(carbonModel);
		this.eq20 = eq20;
		this.eq22 = eq22;
	}

	double getCarbonMigrationFromC1(double C1, double N_lit, double C3, double C2) { 
		return eq22.calculate(N_lit, C3, C2) * eq20.calculate(N_lit, C3, C2) * carbonModel.parmK1 * C1;
	}
}
