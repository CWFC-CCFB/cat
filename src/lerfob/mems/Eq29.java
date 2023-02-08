package lerfob.mems;

import repicea.math.Matrix;

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

	double getCarbonMigrationFromC2(Matrix compartments, double N_lit) { 
		double C2 = compartments.getValueAt(1, 0);
		return eq21.getLeachingLA1(compartments, N_lit) * 
				eq20.getModifier(compartments, N_lit) * 
				carbonModel.parmK2 * C2;
	}
}
