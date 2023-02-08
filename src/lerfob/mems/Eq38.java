package lerfob.mems;

import repicea.math.Matrix;

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

	double getCarbonMigrationFromC1ToC7(Matrix compartments, double N_lit) { 
		double C1 = compartments.getValueAt(0, 0);
		return (1 - eq19.getModifier(compartments, N_lit) * carbonModel.parmB1) *
				(1 - eq22.getLeachingLA4(compartments, N_lit)) *
				eq20.getModifier(compartments, N_lit) * carbonModel.parmK1 * C1;
	}
}
