package lerfob.mems;
import lerfob.mems.SoilCarbonPredictorCompartment;

/**
 * Implements all equations involved in MEMS v1.0 as static methods
 * @author Mathieu Fortin & Jean-Francois Lavoie - Feb 2023
 *
 * Dependencies:
 *  Eq01 (Eq12, Eq20 (Eq16))
 *  Eq02 (Eq13, Eq20 (Eq16))
 *  Eq03 (Eq14)
 *  Eq04 (Eq17(Eq19(Eq16), Eq22(Eq16), Eq20(Eq16)), Eq18(Eq19(Eq16), Eq21(Eq16), Eq20(Eq16)))
 *  Eq05 (Eq23, Eq24, Eq25)
 *  Eq06 (Eq15, Eq28(Eq22(Eq16), Eq20(Eq16)), Eq29(Eq21(Eq16), Eq20(Eq16))), Eq30, Eq31, Eq33)
 *  Eq07 (Eq38(Eq19(Eq16), Eq22(Eq16), Eq20(Eq16)), Eq39(Eq19(Eq16), Eq21(Eq16), Eq20(Eq16)), Eq40, Eq41, Eq42, Eq43, Eq44, Eq45)
 *  Eq08 (Eq32, Eq33, Eq34, Eq37(Eq35, Eq36))
 *  Eq09 (Eq37(Eq35, Eq36))
 *  Eq10 (Eq26, Eq27)
 *  Eq11 ()
 */
public class SoilCarbonPredictorEquation {
    /**
     * Calculate the daily change in C stock in compartment C1. <br>
     * <br>
     * La partie uB * B1 * (1 - la_4) * uk * C1 * parmK1 s'en va de C1 à C4
     * La partie la_4 * uk * C1 * parmK1 s'en va de C1 à C6
     * La partie (1 - uB * B1) * (1 - la_4) * uk * C1 * parmK1 s'en va de C1 à C7
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param C1_i_in the daily input
     * @param uk
     * @return
     */
    static double Eq01_getDailyChangeC1(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartment compartments,
                                        double C1_i_in,
                                        double uk) {
        return C1_i_in - uk * compartments.bins[SoilCarbonPredictorCompartment.C1] * carbonModel.parmK1;
    }

    /**
     * Calculate the daily change in C stock in compartment C2. <br>
     * <br>
     * La partie uB * B2 * (1 - la_1) * uk * C2 * parmK2 s'en va de C2 à C4
     * La partie la_1 * uk * C2 * parmK2 s'en va de C2 à C6
     * La partie (1 - uB * B2) * (1 - la_1) * uk * C2 * parmK2 s'en va de C2 à C7
     * La partie POM_split * C2 * LIT_frg s'en va de C2 à C5
     * La partie (1 - POM_split) * C2 * LIT_frg s'en va de C2 à C10
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param C2_i_in
     * @param uk
     * @return
     */
    static double Eq02_getDailyChangeC2(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartment compartments,
                                        double C2_i_in,
                                        double uk) {
        double C2 = compartments.bins[SoilCarbonPredictorCompartment.C2];
        return C2_i_in - uk * C2 * carbonModel.parmK2 - C2 * carbonModel.LIT_frg;
    }

    /**
     * Provide the daily change in the C stock in compartment C3. <br>
     * <br>
     * La partie C3 * LIT_frg s'en va à C5 et à C10
     * La partie C3 * parmK3 s'en va à C6 et C7
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param C3_i_in the daily input
     * @return
     */
    static double Eq03_getDailyChangeC3(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartment compartments,
                                        double C3_i_in) {

        double C3 = compartments.bins[SoilCarbonPredictorCompartment.C3];

        //TODO : Implement variable K3 here using Eq46 ?
        // Alors que les taux de décroissance maximaux (kx) pour la plupart des pools sont des constantes fixes,
        // Campbell et al. (2016) ont suggéré que k3 est mieux estimé par rapport au taux de décomposition maximal
        // du pool de litière accessible aux microbes (C2) (k2) [équation 46]
        return C3_i_in - C3 * carbonModel.parmK3 - C3 * carbonModel.LIT_frg;
    }

    /**
     * Provide the daily change in the C stock in compartment C4. <br>
     * <br>
     * La partie B3 * (1 - la_2) * parmK4 * C4 s'en va de C4 à C5
     * La partie la_2 * parmK4 * C4 s'en va de C4 à C6
     * La partie (1 - B3) * (1 - la_2) * parmK4 * C4 s'en va de C4 à C7
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param C4_C1_ass
     * @param C4_C2_ass
     * @return
     */
    static double Eq04_getDailyChangeC4(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartment compartments,
                                        double C4_C1_ass,
                                        double C4_C2_ass) {
        return C4_C1_ass + C4_C2_ass - compartments.bins[SoilCarbonPredictorCompartment.C4] * carbonModel.parmK4;
    }

    /**
     * Provide the daily change in the C stock in compartment C5. <br>
     * <br>
     * La partie la_3 * parmK5 * C5 va de C5 à C8
     * La partie (1 - la_3) * parmK5 * C5 va de C5 à C7
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param C5_C4_gen
     * @param C5_C2_frg
     * @param C5_C3_frg
     * @return
     */
    static double Eq05_getDailyChangeC5(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartment compartments,
                                        double C5_C4_gen,
                                        double C5_C2_frg,
                                        double C5_C3_frg) {
        return C5_C4_gen + C5_C2_frg + C5_C3_frg - carbonModel.parmK5 * compartments.bins[SoilCarbonPredictorCompartment.C5];
    }

    /**
     * Provide the daily change in the C stock in compartment C6. <br>
     * <br>
     * La partie DOC_frg * C6 va de C6 à C8
     *
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param C6_i_in
     * @param C6_C1_in
     * @param C6_C2_in
     * @param C6_C3_in
     * @param C6_C4_in
     * @param C8_C6_in
     * @return
     */
    static double Eq06_getDailyChangeC6(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartment compartments,
                                        double C6_i_in,
                                        double C6_C1_in,
                                        double C6_C2_in,
                                        double C6_C3_in,
                                        double C6_C4_in,
                                        double C8_C6_in) {
        return C6_i_in + C6_C1_in + C6_C2_in + C6_C3_in + C6_C4_in - C8_C6_in;
    }

    /**
     * Provide the daily change in the C stock in compartment C7.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param C1_C02
     * @param C2_C02
     * @param C3_C02
     * @param C4_C02
     * @param C5_C02
     * @param C8_C02
     * @param C9_C02
     * @param C10_C02
     * @return
     */
    static double Eq07_getDailyChangeC7(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartment compartments,
                                        double C1_C02,
                                        double C2_C02,
                                        double C3_C02,
                                        double C4_C02,
                                        double C5_C02,
                                        double C8_C02,
                                        double C9_C02,
                                        double C10_C02) {
        return C1_C02 + C2_C02 + C3_C02 + C4_C02 + C5_C02 + C8_C02 + C9_C02 + C10_C02;
    }

    /**
     * Provide the daily change in the C stock in compartment C8. <br>
     * <br>
     * La partie sorption va de C8 à C9
     * La partie DOC_lch * C8 va de C8 à C11
     * La partie parmK8 * C8 va de C8 à C7
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param C8_C5_in
     * @param C8_C6_in
     * @param C8_C10_in
     * @param sorption
     * @return
     */
    static double Eq08_getDailyChangeC8(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartment compartments,
                                        double C8_C5_in,
                                        double C8_C6_in,
                                        double C8_C10_in,
                                        double sorption) {
        double C8 = compartments.bins[SoilCarbonPredictorCompartment.C8];
        return C8_C5_in + C8_C6_in + C8_C10_in - sorption - C8 * carbonModel.DOC_lch - C8 * carbonModel.parmK8;
    }

    /**
     * Provide the daily change in the C stock in compartment C9. <br>
     * <br>
     * La partie C9 * parmK9 va de C9 à C7
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param sorption
     * @return
     */
    static double Eq09_getDailyChangeC9(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartment compartments,
                                        double sorption) {
        return sorption - compartments.bins[SoilCarbonPredictorCompartment.C9] * carbonModel.parmK9;
    }

    /**
     * Provide the daily change in the C stock in compartment C10. <br>
     * <br>
     * La partie la_3 * C10 * parmK10 va de C10 à C8
     *
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param C10_C2_frg
     * @param C10_C3_frg
     * @return
     */
    static double Eq10_getDailyChangeC10(SoilCarbonPredictor carbonModel,
                                         SoilCarbonPredictorCompartment compartments,
                                         double C10_C2_frg,
                                         double C10_C3_frg) {
        return  C10_C2_frg + C10_C3_frg - compartments.bins[SoilCarbonPredictorCompartment.C10] * carbonModel.parmK10;
    }

    /**
     * Provide the daily change in the C stock in compartment C11.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @return
     */
    static double Eq11_getDailyChangeC11(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartment compartments) {
        return compartments.bins[SoilCarbonPredictorCompartment.C8] * carbonModel.DOC_lch;
    }

    /**
     * Calculate the daily input in compartment C1.
     *
     * @param inputs the input data from source i
     * @return the input of hot-water extractible carbon in compartment C1
     */
    static double Eq12_getDailyInputInC1(SoilCarbonPredictor carbonModel, SoilCarbonPredictorInput inputs) {
        return inputs.CT_i * inputs.f_sol * (1 - carbonModel.f_DOC);
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
    static double Eq16_getLCI(SoilCarbonPredictorCompartment compartments) {
        double C3 = compartments.bins[SoilCarbonPredictorCompartment.C3];
        return C3 / (compartments.bins[SoilCarbonPredictorCompartment.C2] + C3);
    }

    /**
     * Return the C stock of C1 assimilated in C4 (C4<sup>C1</sup><sub>ass</sub>)
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param uB
     * @param la_4
     * @param uk
     * @return a double
     */
    static double Eq17_getDailyCarbonStockTransferFromC1ToC4(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments,
                                                             double uB,
                                                             double la_4,
                                                             double uk) {
        return uB * carbonModel.parmB1 * (1 - la_4) * uk * carbonModel.parmK1 * compartments.bins[SoilCarbonPredictorCompartment.C1];
    }

    /**
     * Return the C stock of C2 assimilated in C4 (C4<sup>C2</sup><sub>ass</sub>)
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param uB
     * @param la_1
     * @param uk
     * @return a double
     */
    static double Eq18_getDailyCarbonStockTransferFromC2ToC4(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments,
                                                             double uB,
                                                             double la_1,
                                                             double uk) {
        return uB * carbonModel.parmB2 * (1 - la_1) * uk * carbonModel.parmK2 * compartments.bins[SoilCarbonPredictorCompartment.C2];
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
        double t1 = 1d / (1 + Math.exp(-carbonModel.N_max) * (N_lit - carbonModel.N_mid));
        double t2 = 1 - Math.exp(-0.7 * (Math.abs(Eq16_getLCI(compartments) - 0.7) * 10));
        return Math.min(1d / (1 + Math.exp(-carbonModel.N_max) * (N_lit - carbonModel.N_mid)),
                1 - Math.exp(-0.7 * (Math.abs(Eq16_getLCI(compartments) - 0.7) * 10))); //TODO: fix call to param
    }

    /**
     * Calculate the modifier associated with chemical control.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param N_lit nitrogen concentration of input material
     * @param LCI_lit
     * @return the modifier
     */
    static double Eq20_getModifier(SoilCarbonPredictor carbonModel,
                                   SoilCarbonPredictorCompartment compartments,
                                   double N_lit,
                                   double LCI_lit) {
        return Math.min(1d / (1 + Math.exp(-carbonModel.N_max)*(N_lit - carbonModel.N_mid)), Math.exp(-3 * LCI_lit));
    }

    /**
     * Provides the proportion of C leached from the decomposition of C2.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param N_lit the nitrogen content of the input material
     * @return
     */
    static double Eq21_getLeachingLA1(SoilCarbonPredictor carbonModel,
                                      SoilCarbonPredictorCompartment compartments,
                                      double N_lit) {
        return Math.min(carbonModel.E_Hmax - (carbonModel.E_Hmax - carbonModel.E_Hmin)/carbonModel.LCI_max * Eq16_getLCI(compartments),
                carbonModel.E_Hmax - (carbonModel.E_Hmax - carbonModel.E_Hmin) / carbonModel.N_max * N_lit);
    }

    /**
     * Provides the proportion of C leached from the decomposition of C1.
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
        return carbonModel.parmB3 * (1 - carbonModel.la_2) * carbonModel.parmK4 * compartments.bins[SoilCarbonPredictorCompartment.C4];
    }

    /**
     * Equation 24 provides the carbon stock transferred from compartment C2
     * to compartment C5.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq24_getDailyCarbonStockTransferFromC2ToC5(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments) {
        return carbonModel.POM_split * carbonModel.LIT_frg * compartments.bins[SoilCarbonPredictorCompartment.C2];
    }

    /**
     * Equation 25 provides the carbon stock transferred from compartment C3
     * to compartment C5.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq25_getDailyCarbonStockTransferFromC3ToC5(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments) { // TODO definition of C3 must be clarified
        return carbonModel.POM_split * carbonModel.LIT_frg * compartments.bins[SoilCarbonPredictorCompartment.C3];
    }

    /**
     * Equation 26 provides the carbon stock transferred from compartment C2
     * to compartment C10.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq26_getDailyCarbonStockTransferFromC2ToC10(SoilCarbonPredictor carbonModel,
                                                              SoilCarbonPredictorCompartment compartments) {
        return (1 - carbonModel.POM_split) * carbonModel.LIT_frg * compartments.bins[SoilCarbonPredictorCompartment.C2];
    }

    /**
     * Equation 27 provides the carbon stock transferred from compartment C3
     * to compartment C10.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq27_getDailyCarbonStockTransferFromC3ToC10(SoilCarbonPredictor carbonModel,
                                                              SoilCarbonPredictorCompartment compartments) {
        return (1 - carbonModel.POM_split) * carbonModel.LIT_frg * compartments.bins[SoilCarbonPredictorCompartment.C3];
    }

    /**
     * Equation 28 provides the carbon stock transferred from compartment C1
     * to compartment C6.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param la_4
     * @param uk
     */
    static double Eq28_getDailyCarbonStockTransferFromC1ToC6(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments,
                                                             double la_4,
                                                             double uk) {
        return la_4 * uk * carbonModel.parmK1 * compartments.bins[SoilCarbonPredictorCompartment.C1];
    }

    /**
     * Equation 29 provides the carbon stock transferred from compartment C2
     * to compartment C6.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param la_1
     * @param uk
     */
    static double Eq29_getDailyCarbonStockTransferFromC2ToC6(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments,
                                                             double la_1,
                                                             double uk) {
        return la_1 * uk * carbonModel.parmK2 * compartments.bins[SoilCarbonPredictorCompartment.C2];
    }

    /**
     * Equation 30 provides the carbon stock transferred from compartment C3
     * to compartment C6.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq30_getDailyCarbonStockTransferFromC3ToC6(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments) {
        return carbonModel.la_3 * carbonModel.parmK3 * compartments.bins[SoilCarbonPredictorCompartment.C3];
    }

    /**
     * Equation 31 provides the carbon stock transferred from compartment C4
     * to compartment C6.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq31_getDailyCarbonStockTransferFromC4ToC6(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments) {
        return carbonModel.la_2 * carbonModel.parmK4 * compartments.bins[SoilCarbonPredictorCompartment.C4];
    }

    /**
     * Provide the daily carbon stock transfer from compartment C5 to compartment C8 (C8<sup>C5</sup><sub>in</sub>).
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @return
     */
    static double Eq32_getDailyCarbonStockTransferFromC5ToC8(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments) {
        return carbonModel.la_3 * carbonModel.parmK5 * compartments.bins[SoilCarbonPredictorCompartment.C5];
    }

    /**
     * Equation 33 provides the carbon stock transferred from compartment C6
     * to compartment C8.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq33_getDailyCarbonStockTransferFromC6ToC8(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments) {
        return carbonModel.DOC_frg * compartments.bins[SoilCarbonPredictorCompartment.C6];
    }

    /**
     * Equation 34 provides the carbon stock transferred from compartment C10
     * to compartment C8.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq34_getDailyCarbonStockTransferFromC10ToC8(SoilCarbonPredictor carbonModel,
                                                              SoilCarbonPredictorCompartment compartments) {
        return carbonModel.la_3 * carbonModel.parmK10 * compartments.bins[SoilCarbonPredictorCompartment.C10];
    }

    /**
     * Equation 35 provides the "binding affinity" factor.
     * @param soil_pH
     */
    static double Eq35_getBindingAffinityL_k_lm(double soil_pH) {
        //TODO Value range [0.07, 0.02] is inconsistent with the default L_k_lm parameter (0.25 g C per day)
        return Math.pow(10, -0.186 * soil_pH - 0.216);
    }

//	double getDefaultBindingAffinityL_k_lm() {
//		return carbonModel.L_k_lm;
//	}

    /**
     * Equation 36 provides sorption capacity.*
     * @param bulkDensity
     * @param sandProportion
     * @param rockProportion
     */
    static double Eq36_getMaximumSorptionCapacityQ_max(double bulkDensity,
                                                       double sandProportion,
                                                       double rockProportion) {
        // (1 - rockProportion) has been changed to 100 - rockProportion after validating the equation in the paper
        return bulkDensity * (0.26126 * (100 - sandProportion) + 11.07820) * (100 - rockProportion);
    }

    /**
     * Equation 37 provides the sorption.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param K_lm
     * @param Q_max
     */
    static double Eq37_getSorption(SoilCarbonPredictorCompartment compartments,
                                   double K_lm,
                                   double Q_max) {
        double C8 = compartments.bins[SoilCarbonPredictorCompartment.C8];
        double C9 = compartments.bins[SoilCarbonPredictorCompartment.C9];
        // TODO : check if K_lm and j_K_lm are really the same variable here
        return C8 * (K_lm * Q_max * C8 / (1 + K_lm * C8) - C9) / Q_max;
    }

    /**
     * Equation 38 provides the carbon emissions from compartment C1.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param uB
     * @param la_4
     * @param uk
     */
    static double Eq38_getDailyCarbonStockTransferFromC1ToC7(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments,
                                                             double uB,
                                                             double la_4,
                                                             double uk
                                                             ) {
        return (1 - uB * carbonModel.parmB1) * (1 - la_4) * uk * carbonModel.parmK1 * compartments.bins[SoilCarbonPredictorCompartment.C1];
    }

    /**
     * Equation 39 provides the carbon emissions from compartment C2.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     * @param uB
     * @param la_1
     * @param uk
     */
    static double Eq39_getDailyCarbonStockTransferFromC2ToC7(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments,
                                                             double uB,
                                                             double la_1,
                                                             double uk) {
        return (1 - uB * carbonModel.parmB2) * (1 - la_1) * uk * carbonModel.parmK2 * compartments.bins[SoilCarbonPredictorCompartment.C2];
    }

    /**
     * Equation 40 provides the carbon emissions from compartment C3.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq40_getDailyCarbonStockTransferFromC3ToC7(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments) {
        return (1 - carbonModel.la_3) * carbonModel.parmK3 * compartments.bins[SoilCarbonPredictorCompartment.C3];
    }

    /**
     * Equation 41 provides the carbon emissions from compartment C4.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq41_getDailyCarbonStockTransferFromC4ToC7(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments) {
        return (1 - carbonModel.parmB3) * (1 - carbonModel.la_2) * carbonModel.parmK4 * compartments.bins[SoilCarbonPredictorCompartment.C4];
    }

    /**
     * Equation 42 provides the carbon emissions from compartment C5.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq42_getDailyCarbonStockTransferFromC5ToC7(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments) {
        return (1 - carbonModel.la_3) * carbonModel.parmK5 * compartments.bins[SoilCarbonPredictorCompartment.C5];
    }

    /**
     * Equation 43 provides the carbon emissions from compartment C8.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq43_getDailyCarbonStockTransferFromC8ToC7(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments) {
        return carbonModel.parmK8 * compartments.bins[SoilCarbonPredictorCompartment.C8];
    }

    /**
     * Equation 44 provides the carbon emissions from compartment C9.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq44_getDailyCarbonStockTransferFromC9ToC7(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartment compartments) {
        return carbonModel.parmK9 * compartments.bins[SoilCarbonPredictorCompartment.C9];
    }

    /**
     * Equation 45 provides the carbon emissions from compartment C10.
     * @param compartments a Matrix instance with the initial stocks in each compartment
     */
    static double Eq45_getDailyCarbonStockTransferFromC10ToC7(SoilCarbonPredictor carbonModel,
                                                              SoilCarbonPredictorCompartment compartments) {
        // Note : parmK3 was changed to parmK10 so that the outputs balance properly
        return (1 - carbonModel.la_3) * carbonModel.parmK10 * compartments.bins[SoilCarbonPredictorCompartment.C10];
    }
}
