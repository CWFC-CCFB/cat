package lerfob.mems;

/**
 * Equation 39 provides the carbon emissions from compartment C2.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq39 extends Equation {

	final Eq19 eq19;
	final Eq20 eq20;
	final Eq21 eq21;
	
	Eq39(SoilCarbonPredictor carbonModel, Eq19 eq19, Eq20 eq20, Eq21 eq21) {
		super(carbonModel);
		this.eq19 = eq19;
		this.eq20 = eq20;
		this.eq21 = eq21;
	}

	double getCarbonMigrationFromC2ToC7(double C2, double C3, double N_lit) { 
		return (1 - eq19.calculate(N_lit,  C3,  C2) * carbonModel.parmB2) *
				(1 - eq21.calculate(N_lit, C3, C2)) *
				eq20.calculate(N_lit, C3, C2) * carbonModel.parmK2 * C2;
	}
}
