package lerfob.mems;

public class SoilCarbonPredictorInput {

    double N_lit;			// teneur en azote du matériau d’entrée en % [0-100]
    double CT_i;			// apport quotidien total de carbone provenant de la source externe i le jour j
    double f_sol;			// la fraction extractible à l'eau chaude de l'apport de litière
    double f_lig;			// fraction insoluble dans l'acide de l'apport de litière
    double soil_pH;			// pH du sol simulé
    double bulkDensity;		// bulk density (densité volumétrique) du sol simulé
    double sandProportion;	// contenu en sable (%) du sol simulé [0-100]
    double rockProportion;  // fraction de roches (%) du sol simulé [0-100]
    public SoilCarbonPredictorInput(double N_lit, double CT_i, double f_sol, double f_lig, double soil_pH, double bulkDensity, double sandProportion, double rockProportion)
    {
        this.N_lit = N_lit;
        this.CT_i = CT_i;
        this.f_sol = f_sol;
        this.f_lig = f_lig;
        this.soil_pH = soil_pH;
        this.bulkDensity = bulkDensity;
        this.sandProportion = sandProportion;
        this.rockProportion = rockProportion;
    }
}
