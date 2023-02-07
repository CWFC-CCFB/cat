package lerfob.mems;

/**
 * Equation 21 provides an "estimation du carbone dans la génération de DOM 
 * par lessivage des bassins de litière en décomposition le jour j" 
 * @author Mathieu Fortin - Feb 2023
 */
class Eq21 extends Equation {

	final Eq16 eq16;
	
	Eq21(SoilCarbonPredictor carbonModel, Eq16 eq16) {
		super(carbonModel);
		this.eq16 = eq16;
	}

	/**
	 * 
	 * @param N_lit the nitrogen content of the input material
	 * @param C3
	 * @param C2
	 * @return
	 */
	double calculate(double N_lit, double C3, double C2) {
		return Math.min(carbonModel.E_Hmax - (carbonModel.E_Hmax - carbonModel.E_Hmin)/carbonModel.LCI_max * eq16.calculate(C3, C2), 
				carbonModel.E_Hmax - (carbonModel.E_Hmax - carbonModel.E_Hmin) / carbonModel.N_max * N_lit);
	}
}
