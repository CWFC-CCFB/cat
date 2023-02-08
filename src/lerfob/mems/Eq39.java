package lerfob.mems;

import repicea.math.Matrix;

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

	double getCarbonMigrationFromC2ToC7(Matrix compartments, double N_lit) { 
		double C2 = compartments.getValueAt(1, 0);
		return (1 - eq19.getModifier(compartments, N_lit) * carbonModel.parmB2) *
				(1 - eq21.getLeachingLA1(compartments, N_lit)) *
				eq20.getModifier(compartments, N_lit) * carbonModel.parmK2 * C2;
	}
}
