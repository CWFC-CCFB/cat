package lerfob.mems;
import lerfob.mems.SoilCarbonPredictorCompartment.CompartmentID;
import repicea.math.Matrix;

/**
 * Implements all equations involved in MEMS v1.0 as static methods
 * @author Mathieu Fortin & Jean-Francois Lavoie - Feb 2023
 */
public class SoilCarbonPredictorEquation {
    /**
     * Calculate the daily change in C stock in compartment C1.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param CT_i the daily input
     * @param f_sol
     * @param N_lit
     * @return
     */
    static double Eq01_getDailyChangeC1(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartment compartments,
                                        double CT_i,
                                        double f_sol,
                                        double N_lit) {
        return Eq12_getDailyInputInC1(carbonModel, CT_i, f_sol) -
                Eq20_getModifier(carbonModel, compartments, N_lit) * compartments.getStock(SoilCarbonPredictorCompartment.CompartmentID.C1) * carbonModel.parmK1;
    }

    /**
     * Calculate the daily change in C stock in compartment C2.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param CT_i the daily input
     * @param f_sol
     * @param f_lig
     * @param N_lit
     * @return
     */
    static double Eq02_getDailyChangeC2(SoilCarbonPredictor carbonModel,
                                   SoilCarbonPredictorCompartment compartments,
                                   double CT_i,
                                   double f_sol,
                                   double f_lig,
                                   double N_lit) {
        double C2 = compartments.getStock(CompartmentID.C2);
        return Eq13_getDailyInputInC2(CT_i, f_sol, f_lig) -
                Eq20_getModifier(carbonModel, compartments, N_lit) * C2 * carbonModel.parmK2 -
                C2 * carbonModel.LIT_frg;
    }

    /**
     * Provide the daily change in the C stock in compartment C3.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param CT_i the daily input*
     * @param f_lig
     * @return
     */
    static double Eq03_getDailyChangeC3(SoilCarbonPredictor carbonModel,
                                   SoilCarbonPredictorCompartment compartments,
                                   double CT_i,
                                   double f_lig) {
        double C3 = compartments.getStock(CompartmentID.C3);
        return Eq14_getDailyInputInC3(CT_i, f_lig) -
                C3 * carbonModel.parmK3 -
                C3 * carbonModel.LIT_frg;
    }

    /**
     * Provide the daily change in the C stock in compartment C4.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param N_lit
     * @return
     */
    static double Eq04_getDailyChangeC4(SoilCarbonPredictor carbonModel,
                                   SoilCarbonPredictorCompartment compartments,
                                   double N_lit) {
        return Eq17_getDailyCarbonStockTransferFromC1ToC4(carbonModel, compartments, N_lit) +
                Eq18_getDailyCarbonStockTransferFromC2ToC4(carbonModel, compartments, N_lit) -
                compartments.getStock(CompartmentID.C4) * carbonModel.parmK4;
    }

    /**
     * Provide the daily change in the C stock in compartment C5.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @return
     */
    static double Eq05_getDailyChangeC5(SoilCarbonPredictor carbonModel,
                                   SoilCarbonPredictorCompartment compartments) {
        return Eq23_getDailyCarbonStockTransferFromC4ToC5(carbonModel, compartments) +
                Eq24_getDailyCarbonStockTransferFromC2ToC5(carbonModel, compartments) +
                Eq25_getDailyCarbonStockTransferFromC3ToC5(carbonModel, compartments) -
                carbonModel.parmK5 * compartments.getStock(CompartmentID.C5);
    }

    /**
     * Provide the daily change in the C stock in compartment C6.
     * @return
     */
    static double Eq06_getDailyChangeC6(SoilCarbonPredictor carbonModel,
                                   SoilCarbonPredictorCompartment compartments,
                                   double CT_i,
                                   double f_sol,
                                   double N_lit) {
        return Eq15_calculate(carbonModel, CT_i, f_sol) +
                Eq28_getDailyCarbonStockTransferFromC1ToC6(carbonModel, compartments,  N_lit) +
                Eq29_getDailyCarbonStockTransferFromC2ToC6(carbonModel, compartments, N_lit) +
                Eq30_getDailyCarbonStockTransferFromC3ToC6(carbonModel, compartments) +
                Eq31_getDailyCarbonStockTransferFromC4ToC6(carbonModel, compartments) -
                Eq33_getDailyCarbonStockTransferFromC6ToC8(carbonModel, compartments);
    }

    /**
     * Provide the daily change in the C stock in compartment C7.
     * @return
     */
    static double Eq07_getDailyChangeC7(SoilCarbonPredictor carbonModel,
                                   SoilCarbonPredictorCompartment compartments,
                                   double N_lit) {
        return Eq38_getDailyCarbonStockTransferFromC1ToC7(carbonModel, compartments, N_lit) +
                Eq39_getDailyCarbonStockTransferFromC2ToC7(carbonModel, compartments, N_lit) +
                Eq40_getDailyCarbonStockTransferFromC3ToC7(carbonModel, compartments) +
                Eq41_getDailyCarbonStockTransferFromC4ToC7(carbonModel, compartments) +
                Eq42_getDailyCarbonStockTransferFromC5ToC7(carbonModel, compartments) +
                Eq43_getDailyCarbonStockTransferFromC8ToC7(carbonModel, compartments) +
                Eq44_getDailyCarbonStockTransferFromC9ToC7(carbonModel, compartments) +
                Eq45_getDailyCarbonStockTransferFromC10ToC7(carbonModel, compartments);
    }

    /**
     * Provide the daily change in the C stock in compartment C8.
     * @return
     */
    static double Eq08_getDailyChangeC8(SoilCarbonPredictor carbonModel,
                                   SoilCarbonPredictorCompartment compartments,
                                   double N_lit,
                                   double soil_pH,
                                   double bulkDensity,
                                   double sandProportion,
                                   double rockProportion) {
        double C8 = compartments.getStock(CompartmentID.C8);
        return Eq32_getDailyCarbonStockTransferFromC5ToC8(carbonModel, compartments) +
                Eq33_getDailyCarbonStockTransferFromC6ToC8(carbonModel, compartments) +
                Eq34_getDailyCarbonStockTransferFromC10ToC8(carbonModel, compartments) -
                Eq37_getSorption(compartments, soil_pH, bulkDensity, sandProportion, rockProportion) -
                C8 * carbonModel.DOC_lch -
                C8 * carbonModel.parmK8;
    }

    /**
     * Provide the daily change in the C stock in compartment C9.
     * @return
     */
    static double Eq09_getDailyChangeC9(SoilCarbonPredictor carbonModel,
                                   SoilCarbonPredictorCompartment compartments,
                                   double N_lit,
                                   double soil_pH,
                                   double bulkDensity,
                                   double sandProportion,
                                   double rockProportion) {
        return Eq37_getSorption(compartments, soil_pH, bulkDensity, sandProportion, rockProportion) -
                compartments.getStock(CompartmentID.C9) * carbonModel.parmK9;

    }

    /**
     * Provide the daily change in the C stock in compartment C10.
     * @return
     */
    static double Eq10_getDailyChangeC10(SoilCarbonPredictor carbonModel,
                                    SoilCarbonPredictorCompartment compartments) {
        return Eq26_getDailyCarbonStockTransferFromC2ToC10(carbonModel, compartments) +
                Eq27_getDailyCarbonStockTransferFromC3ToC10(carbonModel, compartments) -
                compartments.getStock(CompartmentID.C10) * carbonModel.parmK10;

    }

    /**
     * Provide the daily change in the C stock in compartment C11.
     * @return
     */
    static double Eq11_getDailyChangeC11(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartment compartments) {
        return compartments.getStock(CompartmentID.C8) * carbonModel.DOC_lch;
    }

    /**
     * Calculate the daily input in compartment C1.
     *
     * @param CT_i the daily input from external source i
     * @param f_sol the extractible faction
     * @return the input of hot-water extractible carbon in compartment C1
     */
    static double Eq12_getDailyInputInC1(SoilCarbonPredictor carbonModel, double CT_i, double f_sol) {
        return CT_i * f_sol * (1 - carbonModel.f_DOC);
    }

    /**
     * Calculate the daily input in compartment C2.
     * @param CT_i the input from external source i
     * @param f_sol la fraction extractible � l'eau chaude de l'apport de liti�re
     * @param f_lig fraction insoluble dans l'acide de l'apport de liti�re
     * @return the daily input in compartment C2
     */
    static double Eq13_getDailyInputInC2(double CT_i, double f_sol, double f_lig) {
        return CT_i - (CT_i * (f_sol + f_lig));
    }

    /**
     * Return the input of C in compartment C3.
     * @param CT_i the daily intake from source i
     * @param f_lig fraction insoluble dans l'acide de l'apport de liti�re
     * @return the net input of C in compartment C3
     */
    static double Eq14_getDailyInputInC3(double CT_i, double f_lig) {
        return CT_i * f_lig;
    }

    /**
     * Return the input of C in compartment C6.
     * @param CT_i the daily intake from source i
     * @param f_sol hot water extractible fraction
     * @return the net input of C in compartment C3
     */
    static double Eq15_calculate(SoilCarbonPredictor carbonModel, double CT_i, double f_sol) {
        return CT_i * f_sol * carbonModel.f_DOC;
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
    static double Eq16_getLCI(SoilCarbonPredictorCompartment compartments) {	// TODO the definition of C3 is inconsistent with the information moreover, this is input whereas the symbol suggests the current stock in the pool
//		 * @param C3 input total au pool C1 (C3?) de carbone de litière insoluble dans l’acide pour le jour j
//		 * @param C2 input total au pool C2 de carbone de litière soluble dans l’acide pour le jour j
        double C3 = compartments.getStock(CompartmentID.C3);
        return C3 / (compartments.getStock(CompartmentID.C2) + C3);
    }

    /**
     * Return the C stock of C1 assimilated in C4 (C4<sup>C1</sup><sub>ass</sub>)
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param N_lit
     * @return a double
     */
    static double Eq17_getDailyCarbonStockTransferFromC1ToC4(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments,
                                                        double N_lit) {
        return Eq19_getModifier(carbonModel, compartments, N_lit) * carbonModel.parmB1 * (1 - Eq22_getLeachingLA4(carbonModel, compartments, N_lit)) *
                Eq20_getModifier(carbonModel, compartments, N_lit) * carbonModel.parmK1 * compartments.getStock(CompartmentID.C1);
    }

    /**
     * Return the C stock of C2 assimilated in C4 (C4<sup>C2</sup><sub>ass</sub>)
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param N_lit
     * @return a double
     */
    static double Eq18_getDailyCarbonStockTransferFromC2ToC4(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments,
                                                        double N_lit) {
        return Eq19_getModifier(carbonModel, compartments, N_lit) * carbonModel.parmB2 * (1 - Eq21_getLeachingLA1(carbonModel, compartments, N_lit)) *
                Eq20_getModifier(carbonModel, compartments, N_lit) * carbonModel.parmK1 * compartments.getStock(CompartmentID.C1);
    }

    /**
     * Provide the daily modifier for the chemical control of the litter over the
     * carbon use efficiency.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param N_lit nitrogen concentration of the input material
     * @return
     */
    static double Eq19_getModifier(SoilCarbonPredictor carbonModel,
                              SoilCarbonPredictorCompartment compartments,
                              double N_lit) {
        return Math.min(1d / (1 + Math.exp(carbonModel.N_max) * (N_lit - carbonModel.N_mid)),
                1 - Math.exp(-0.7 * (Math.abs(Eq16_getLCI(compartments) - 0.7) * 10)));
    }

    /**
     * Calculate the modifier associated with chemical control.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param N_lit nitrogen concentration of input material
     * @return the modifier
     */
    static double Eq20_getModifier(SoilCarbonPredictor carbonModel,
                              SoilCarbonPredictorCompartment compartments,
                              double N_lit) {
        return Math.min(1d / (1 + Math.exp(-carbonModel.N_max)*(N_lit - carbonModel.N_mid)),
                Math.exp(-3 * Eq16_getLCI(compartments)));
    }

    /**
     * Provide the leaching factor.
     * @param N_lit the nitrogen content of the input material
     * @return
     */
    static double Eq21_getLeachingLA1(SoilCarbonPredictor carbonModel,
                                 SoilCarbonPredictorCompartment compartments,
                                 double N_lit) {
        return Math.min(carbonModel.E_Hmax - (carbonModel.E_Hmax - carbonModel.E_Hmin)/carbonModel.LCI_max * Eq16_getLCI(compartments),
                carbonModel.E_Hmax - (carbonModel.E_Hmax - carbonModel.E_Hmin) / carbonModel.N_max * N_lit);
    }

    //TODO check what that is?
    /**
     * Provide the leaching factor.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param N_lit the nitrogen content in the input material
     * @return
     */
    static double Eq22_getLeachingLA4(SoilCarbonPredictor carbonModel,
                                 SoilCarbonPredictorCompartment compartments,
                                 double N_lit) {
        return Math.min(carbonModel.E_smax - (carbonModel.E_smax - carbonModel.E_smin) / carbonModel.LCI_max * Eq16_getLCI(compartments),
                carbonModel.E_smax - (carbonModel.E_smax - carbonModel.E_smin) / carbonModel.N_max * N_lit);
    }

    /**
     * Equation 23 provides the carbon stock transferred from compartment C4
     * to compartment C5.
     * @param compartments a Matrix instance with the initial stocks in each compartment
    */
    static double Eq23_getDailyCarbonStockTransferFromC4ToC5(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments) {
        return carbonModel.parmB3 * (1 - carbonModel.la_2) * carbonModel.parmK4 * compartments.getStock(CompartmentID.C4);
    }

    /**
     * Equation 24 provides the carbon stock transferred from compartment C2
     * to compartment C5.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq24_getDailyCarbonStockTransferFromC2ToC5(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments) {
        return carbonModel.POM_split * carbonModel.LIT_frg * compartments.getStock(CompartmentID.C2);
    }

    /**
     * Equation 25 provides the carbon stock transferred from compartment C3
     * to compartment C5.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq25_getDailyCarbonStockTransferFromC3ToC5(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments) { // TODO definition of C3 must be clarified
        return carbonModel.POM_split * carbonModel.LIT_frg * compartments.getStock(CompartmentID.C3);
    }

    /**
     * Equation 26 provides the carbon stock transferred from compartment C2
     * to compartment C10.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq26_getDailyCarbonStockTransferFromC2ToC10(SoilCarbonPredictor carbonModel,
                                                         SoilCarbonPredictorCompartment compartments) {
        return (1 - carbonModel.POM_split) * carbonModel.LIT_frg * compartments.getStock(CompartmentID.C2);
    }

    /**
     * Equation 27 provides the carbon stock transferred from compartment C3
     * to compartment C10.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq27_getDailyCarbonStockTransferFromC3ToC10(SoilCarbonPredictor carbonModel,
                                                         SoilCarbonPredictorCompartment compartments) {
        return (1 - carbonModel.POM_split) * carbonModel.LIT_frg * compartments.getStock(CompartmentID.C3);
    }

    /**
     * Equation 28 provides the carbon stock transferred from compartment C1
     * to compartment C6.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq28_getDailyCarbonStockTransferFromC1ToC6(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments,
                                                        double N_lit) {
        return Eq22_getLeachingLA4(carbonModel, compartments, N_lit) *
                Eq20_getModifier(carbonModel, compartments, N_lit) *
                carbonModel.parmK1 * compartments.getStock(CompartmentID.C1);
    }

    /**
     * Equation 29 provides the carbon stock transferred from compartment C2
     * to compartment C6.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq29_getDailyCarbonStockTransferFromC2ToC6(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments,
                                                        double N_lit) {
        return Eq21_getLeachingLA1(carbonModel, compartments, N_lit) *
                Eq20_getModifier(carbonModel, compartments, N_lit) *
                carbonModel.parmK2 * compartments.getStock(CompartmentID.C2);
    }

    /**
     * Equation 30 provides the carbon stock transferred from compartment C3
     * to compartment C6.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq30_getDailyCarbonStockTransferFromC3ToC6(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments) {
        return carbonModel.la_3 * carbonModel.parmK3 * compartments.getStock(CompartmentID.C3);
    }

    /**
     * Equation 31 provides the carbon stock transferred from compartment C4
     * to compartment C6.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq31_getDailyCarbonStockTransferFromC4ToC6(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments) {
        return carbonModel.la_2 * carbonModel.parmK4 * compartments.getStock(CompartmentID.C4);
    }

    /**
     * Provide the daily carbon stock transfer from compartment C5 to compartment C8 (C8<sup>C5</sup><sub>in</sub>).
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @return
     */
    static double Eq32_getDailyCarbonStockTransferFromC5ToC8(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments) {
        return carbonModel.la_3 * carbonModel.parmK5 * compartments.getStock(CompartmentID.C5);
    }

    /**
     * Equation 33 provides the carbon stock transferred from compartment C6
     * to compartment C8.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq33_getDailyCarbonStockTransferFromC6ToC8(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments) {
        return carbonModel.DOC_frg * compartments.getStock(CompartmentID.C6);
    }

    /**
     * Equation 34 provides the carbon stock transferred from compartment C10
     * to compartment C8.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq34_getDailyCarbonStockTransferFromC10ToC8(SoilCarbonPredictor carbonModel,
                                                         SoilCarbonPredictorCompartment compartments) {
        return carbonModel.la_3 * carbonModel.parmK10 * compartments.getStock(CompartmentID.C10);
    }

    /**
     * Equation 35 provides the "binding affinity" factor.
     */
    static double Eq35_getBindingAffinityL_k_lm(double soil_pH) {
        return Math.pow(10, -0.186 * soil_pH - 0.216);
    }

//	double getDefaultBindingAffinityL_k_lm() {
//		return carbonModel.L_k_lm;
//	}

    /**
     * Equation 36 provides sorption capacity.*
     */
    static double Eq36_getMaximumSorptionCapacityQ_max(double bulkDensity, double sandProportion, double rockProportion) {
        return bulkDensity * (0.26126 * (100 - sandProportion) + 11.07820) * (1 - rockProportion); // TODO check if 100 should be replaced by 1 before sand proportion
    }

    /**
     * Equation 37 provides the sorption.
     */
    static double Eq37_getSorption(SoilCarbonPredictorCompartment compartments,
                              double soil_pH,
                              double bulkDensity,
                              double sandProportion,
                              double rockProportion) {
        double C8 = compartments.getStock(CompartmentID.C8);
        double C9 = compartments.getStock(CompartmentID.C9);
        double L_k_lm = Eq35_getBindingAffinityL_k_lm(soil_pH);
        double q_max = Eq36_getMaximumSorptionCapacityQ_max(bulkDensity, sandProportion, rockProportion);
        return C8 * (L_k_lm * q_max * C8 / (1 + L_k_lm * C8) - C9) / q_max;
    }

    /**
     * Equation 38 provides the carbon emissions from compartment C1.
     */
    static double Eq38_getDailyCarbonStockTransferFromC1ToC7(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments,
                                                        double N_lit) {
        return (1 - Eq19_getModifier(carbonModel, compartments, N_lit) * carbonModel.parmB1) *
                (1 - Eq22_getLeachingLA4(carbonModel, compartments, N_lit)) *
                Eq20_getModifier(carbonModel, compartments, N_lit) * carbonModel.parmK1 * compartments.getStock(CompartmentID.C1);
    }

    /**
     * Equation 39 provides the carbon emissions from compartment C2.
     */
    static double Eq39_getDailyCarbonStockTransferFromC2ToC7(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments,
                                                        double N_lit) {
        return (1 - Eq19_getModifier(carbonModel, compartments, N_lit) * carbonModel.parmB2) *
                (1 - Eq21_getLeachingLA1(carbonModel, compartments, N_lit)) *
                Eq20_getModifier(carbonModel, compartments, N_lit) * carbonModel.parmK2 * compartments.getStock(CompartmentID.C2);
    }

    /**
     * Equation 40 provides the carbon emissions from compartment C3.
     */
    static double Eq40_getDailyCarbonStockTransferFromC3ToC7(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments) {
        return (1 - carbonModel.la_3) *
                carbonModel.parmK3 * compartments.getStock(CompartmentID.C3);
    }

    /**
     * Equation 41 provides the carbon emissions from compartment C4.
     */
    static double Eq41_getDailyCarbonStockTransferFromC4ToC7(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments) {
        return (1 - carbonModel.parmB3) *
                (1 - carbonModel.la_2) *
                carbonModel.parmK4 * compartments.getStock(CompartmentID.C4);
    }

    /**
     * Equation 42 provides the carbon emissions from compartment C5.
     */
    static double Eq42_getDailyCarbonStockTransferFromC5ToC7(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments) {
        return (1 - carbonModel.la_3) *
                carbonModel.parmK5 * compartments.getStock(CompartmentID.C5);
    }

    /**
     * Equation 43 provides the carbon emissions from compartment C8.
     */
    static double Eq43_getDailyCarbonStockTransferFromC8ToC7(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments) {
        return carbonModel.parmK8 * compartments.getStock(CompartmentID.C8);
    }

    /**
     * Equation 44 provides the carbon emissions from compartment C9.
     */
    static double Eq44_getDailyCarbonStockTransferFromC9ToC7(SoilCarbonPredictor carbonModel,
                                                        SoilCarbonPredictorCompartment compartments) {
        return carbonModel.parmK9 * compartments.getStock(CompartmentID.C9);
    }

    /**
     * Equation 45 provides the carbon emissions from compartment C10.
     */
    static double Eq45_getDailyCarbonStockTransferFromC10ToC7(SoilCarbonPredictor carbonModel,
                                                         SoilCarbonPredictorCompartment compartments) {
        return (1 - carbonModel.la_3) *
                carbonModel.parmK3 * compartments.getStock(CompartmentID.C10);
    }
}
