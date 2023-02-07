package lerfob.mems;

class Eq19 extends Equation {

	final Eq16 eq16;
	
	Eq19(SoilCarbonPredictor carbonModel, Eq16 eq16) {
		super(carbonModel);
		this.eq16 = eq16;
	}

	/**
	 * Provide the "modificateur de taux pour représenter les contrôles chimiques 
	 * de la litière (lignocellulose index [LCI] et la disponibilité en azote) sur 
	 * l’efficacité d’utilisation microbienne (carbon use efficiency : CUE), pour le jour j" 
	 * @param N_lit nitrogen concentration of the input material
	 * @param C3
	 * @param C2
	 * @return
	 */
	double calculate(double N_lit, double C3, double C2) {
		return Math.min(1d/(1+Math.exp(carbonModel.N_max)*(N_lit - carbonModel.N_mid)), 
				1-Math.exp(-0.7*(Math.abs(eq16.calculate(C3, C2) - 0.7)*10)));
	}
	
}
