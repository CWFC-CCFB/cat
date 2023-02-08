package lerfob.mems;

import repicea.math.Matrix;

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

	double getCarbonMigrationFromC1(Matrix compartments, double N_lit) { 
		double C1 = compartments.getValueAt(0, 0);
		return eq22.getLeachingLA4(compartments, N_lit) * 
				eq20.getModifier(compartments, N_lit) * 
				carbonModel.parmK1 * C1;
	}
}
