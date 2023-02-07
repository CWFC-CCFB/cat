package lerfob.mems;

public class Eq16 extends Equation {

	Eq16(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	/**
	 * Compute the LCI <br>
	 * <br>
	 * 
	 * LCI = [lignine / (lignine + α-cellulose)] (Soong et al., 2015)
	 * 
	 * @param C3 input total au pool C1 de carbone de litière insoluble dans l’acide pour le jour j
	 * @param C2 input total au pool C2 de carbone de litière soluble dans l’acide pour le jour j
	 * @return
	 */
	double calculate(double C3, double C2) {	// TODO the definition of C3 is inconsistent with the information moreover, this is input whereas the symbol suggests the current stock in the pool
		return C3 / (C2 + C3);
	}
}
