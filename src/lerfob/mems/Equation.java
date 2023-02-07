package lerfob.mems;

abstract class Equation {

	final SoilCarbonPredictor carbonModel;
	
	Equation(SoilCarbonPredictor carbonModel) {
		this.carbonModel = carbonModel;
	}
	
}
