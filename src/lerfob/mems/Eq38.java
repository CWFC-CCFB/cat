package lerfob.mems;

/**
 * Equation 38 provides the carbon emissions from compartment C1.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq38 extends Equation {

	final Eq19 eq19;
	final Eq20 eq20;
	final Eq22 eq22;
	
	Eq38(SoilCarbonPredictor carbonModel, Eq19 eq19, Eq20 eq20, Eq22 eq22) {
		super(carbonModel);
		this.eq19 = eq19;
		this.eq20 = eq20;
		this.eq22 = eq22;
	}

	double getCarbonMigrationFromC1ToC7(double C1, double C2, double C3, double N_lit) { 
		return (1 - eq19.calculate(N_lit,  C3,  C2) * carbonModel.parmB1) *
				(1 - eq22.calculate(N_lit, C3, C2)) *
				eq20.calculate(N_lit, C3, C2) * carbonModel.parmK1 * C1;
	}
}
