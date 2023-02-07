package lerfob.mems;

/**
 * Equation 17 provides the fraction of C1 litter pool that 
 * is assimilated in the C4 pool through microbial activity.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq17 extends Equation {

	final Eq19 eq19;
	final Eq20 eq20;
	final Eq22 eq22;
	
	Eq17(SoilCarbonPredictor carbonModel, Eq19 eq19, Eq20 eq20, Eq22 eq22) {
		super(carbonModel);
		this.eq19 = eq19;
		this.eq20 = eq20;
		this.eq22 = eq22;
	}

	/**
	 * Return the fraction of C1 litter pool that is assimilated in the C4 (C4<sup>C1</sup><sub>ass</sub>)
	 * @param N_lit
	 * @param C3
	 * @param C2
	 * @param LCI_lit
	 * @return a double
	 */
	double calculate(double N_lit, double C3, double C2) {
		return eq19.calculate(N_lit, C3, C2) * carbonModel.parmB1 * (1-eq22.calculate(N_lit, C3, C2)) * 
				eq20.calculate(N_lit, C3, C2) * carbonModel.parmK1 * carbonModel.C1;
	}
}
