package lerfob.mems;

import repicea.math.Matrix;

/**
 * Equation 16 provides the LCI
 * @author Mathieu Fortin - Feb 2023
 */
class Eq16 extends Equation {

	Eq16(SoilCarbonPredictor carbonModel) {
		super(carbonModel);
	}

	/**
	 * Compute the LCI <br>
	 * <br>
	 * 
	 * LCI = [lignine / (lignine + α-cellulose)] (Soong et al., 2015)
	 * 
	 * @param compartments a Matrix instance with the initial stocks in each compartment
	 * @return
	 */
	double getLCI(Matrix compartments) {	// TODO the definition of C3 is inconsistent with the information moreover, this is input whereas the symbol suggests the current stock in the pool
//		 * @param C3 input total au pool C1 (C3?) de carbone de litière insoluble dans l’acide pour le jour j
//		 * @param C2 input total au pool C2 de carbone de litière soluble dans l’acide pour le jour j
		double C3 = compartments.getValueAt(2, 0);
		double C2 = compartments.getValueAt(1, 0);
		return C3 / (C2 + C3);
	}
}
