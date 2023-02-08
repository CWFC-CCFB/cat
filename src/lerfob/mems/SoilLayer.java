package lerfob.mems;

import repicea.math.Matrix;

/**
 * The SoilLayer class represents a layer in the soil. 
 * @author Mathieu Fortin - Feb 2023
 */
class SoilLayer {

	final SoilCarbonPredictor carbonModel;
	final String description;
	
	SoilLayer(SoilCarbonPredictor carbonModel, String description) {
		this.carbonModel = carbonModel;
		this.description = description;
	}
	

	Matrix predictCstockChanges(Matrix compartments, 
			double N_lit, 
			double CT_i, 
			double f_sol,
			double f_lig,
			double soil_pH,
			double bulkDensity,
			double sandProportion,
			double rockProportion) {
		Matrix pred = new Matrix(compartments.m_iRows, 1);
		pred.setValueAt(0, 0, Eq01.getDailyChangeC1(carbonModel, compartments, CT_i, f_sol, N_lit));
		pred.setValueAt(1, 0, Eq02.getDailyChangeC2(carbonModel, compartments, CT_i, f_sol, f_lig, N_lit));
		pred.setValueAt(2, 0, Eq03.getDailyChangeC3(carbonModel, CT_i, f_lig));
		pred.setValueAt(3, 0, Eq04.getDailyChangeC4(carbonModel, compartments, N_lit));
		pred.setValueAt(4, 0, Eq05.getDailyChangeC5(carbonModel, compartments));
		pred.setValueAt(5, 0, Eq06.getDailyChangeC6(carbonModel, compartments, CT_i, f_sol, N_lit));
		pred.setValueAt(6, 0, Eq07.getDailyChangeC7(carbonModel, compartments, N_lit));
		pred.setValueAt(7, 0, Eq08.getDailyChangeC8(carbonModel, compartments, N_lit, soil_pH, bulkDensity, sandProportion, rockProportion));
		pred.setValueAt(8, 0, Eq09.getDailyChangeC9(carbonModel, compartments, N_lit, soil_pH, bulkDensity, sandProportion, rockProportion));
		pred.setValueAt(9, 0, Eq10.getDailyChangeC10(carbonModel, compartments));
		pred.setValueAt(10, 0, Eq11.getDailyChangeC11(carbonModel, compartments));
		return pred;
	}
	
	
}
