package lerfob.mems;

import repicea.math.Matrix;

/**
 * Provide the daily change in compartment C8.
 * @author Mathieu Fortin - Feb 2023
 */
class Eq08 {

	/**
	 * Provide the daily change in the C stock in compartment C8.
	 * @return
	 */
	static double getDailyChangeC8(SoilCarbonPredictor carbonModel,
			Matrix compartments, 
			double N_lit,
			double soil_pH,
			double bulkDensity,
			double sandProportion,
			double rockProportion) {
		double C8 = compartments.getValueAt(7, 0);
		return Eq32.getDailyCarbonStockTransferFromC5ToC8(carbonModel, compartments) + 
				Eq33.getDailyCarbonStockTransferFromC6ToC8(carbonModel, compartments) +
				Eq34.getDailyCarbonStockTransferFromC10ToC8(carbonModel, compartments) -
				Eq37.getSorption(compartments, soil_pH, bulkDensity, sandProportion, rockProportion) - 
				C8 * carbonModel.DOC_lch -
				C8 * carbonModel.parmK8; 
 	}
	
}
