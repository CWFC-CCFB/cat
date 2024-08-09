/*
 * This file is part of the mems library.
 *
 * Copyright (C) 2022-23 His Majesty the King in Right of Canada
 * Authors: Jean-Francois Lavoie and Mathieu Fortin, Canadian Wood Fibre Centre
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed with the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * Please see the license at http://www.gnu.org/copyleft/lesser.html.
 */
package lerfob.mems;

import java.lang.Math;

import static java.lang.Math.*;

/**
 * A package class that implements all the equations involved in MEMS v1.0 
 * as static methods.<p>
 * 
 * The dependencies among the equations go as follows:
 * <ul>
 * <li>Eq01 (Eq12, Eq20 (Eq16))
 * <li>Eq02 (Eq13, Eq20 (Eq16))
 * <li>Eq03 (Eq14)
 * <li>Eq04 (Eq17(Eq19(Eq16), Eq22(Eq16), Eq20(Eq16)), Eq18(Eq19(Eq16), Eq21(Eq16), Eq20(Eq16)))
 * <li>Eq05 (Eq23, Eq24, Eq25)
 * <li>Eq06 (Eq15, Eq28(Eq22(Eq16), Eq20(Eq16)), Eq29(Eq21(Eq16), Eq20(Eq16))), Eq30, Eq31, Eq33)
 * <li>Eq07 (Eq38(Eq19(Eq16), Eq22(Eq16), Eq20(Eq16)), Eq39(Eq19(Eq16), Eq21(Eq16), Eq20(Eq16)), Eq40, Eq41, Eq42, Eq43, Eq44, Eq45)
 * <li>Eq08 (Eq32, Eq33, Eq34, Eq37(Eq35, Eq36))
 * <li>Eq09 (Eq37(Eq35, Eq36))
 * <li>Eq10 (Eq26, Eq27)
 * <li>Eq11 ()
 * </ul>
 *  
 * @author Jean-Francois Lavoie and Mathieu Fortin - February 2023
 */
class SoilCarbonPredictorEquation {

	private static final double DividedBy365 = 1d/365;
	private static double[] JulianDaySinValuesCache = new double[365];
	static {
		for (int julianDay = 1; julianDay <= 365; julianDay++) {
			JulianDaySinValuesCache[julianDay - 1] = sin((julianDay * DividedBy365) * 2 * PI - 1.5);
		}
	}
	
	
	// TODO define uk in the doc here
    /**
     * Calculate the daily change in C stock in compartment C1. <p>
     * <ul>
     * <li>The part 'uB * B1 * (1 - la_4) * uk * C1 * parmK1' goes from C1 to C4.
     * <li>The part 'la_4 * uk * C1 * parmK1' goes from C1 to C6.
     * <li>The part '(1 - uB * B1) * (1 - la_4) * uk * C1 * parmK1' goes from C1 to C7.
     * </ul>
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a SoilCarbonPredictorCompartments instance with the initial stocks in each compartment
     * @param C1_i_in the daily input
     * @param uk
     * @return the daily change in C1
     */
    static double Eq01_getDailyChangeC1(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartments compartments,
                                        double C1_i_in,
                                        double uk) {
        return C1_i_in - uk * compartments.C1 * carbonModel.parmK1 * carbonModel.Tmod;
    }

    /**
     * Calculate the daily change in C stock in compartment C2.<p>
     * <ul>
     * <li>The part 'uB * B2 * (1 - la_1) * uk * C2 * parmK2' goes from C2 to C4.
     * <li>The part 'la_1 * uk * C2 * parmK2' goes from C2 to C6.
     * <li>The part '(1 - uB * B2) * (1 - la_1) * uk * C2 * parmK2' goes from C2 to C7.
     * <li>The part 'POM_split * C2 * LIT_frg' goes from C2 to C5.
     * <li>The part '(1 - POM_split) * C2 * LIT_frg' goes from C2 to C10.
     * </ul>
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param C2_i_in the daily input
     * @param uk
     * @return the daily change in C2
     */
    static double Eq02_getDailyChangeC2(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartments compartments,
                                        double C2_i_in,
                                        double uk) {
        return C2_i_in - uk * compartments.C2 * carbonModel.parmK2 * carbonModel.Tmod - compartments.C2 * carbonModel.LIT_frg * carbonModel.Tmod;
    }

    /**
     * Calculate the daily change in C stock in compartment C3.<p>
     * <ul>
     * <li>The part 'C3 * LIT_frg' goes to C5 and C10.
     * <li>The part 'C3 * parmK3' goes to C6 and C7.
     * </ul>
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param C3_i_in the daily input
     * @return the daily change in C3
     */
    static double Eq03_getDailyChangeC3(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartments compartments,
                                        double C3_i_in) {
        // Alors que les taux de decroissance maximaux (kx) pour la plupart des pools sont des constantes fixes,
        // Campbell et al. (2016) ont suggere que k3 est mieux estime par rapport au taux de decomposition maximal
        // du pool de litiere accessible aux microbes (C2) (k2) [equation 46]
        return C3_i_in - compartments.C3 * carbonModel.parmK3 * carbonModel.Tmod - compartments.C3 * carbonModel.LIT_frg * carbonModel.Tmod;
    }

    /**
     * Calculate the daily change in the C stock in compartment C4.<p>
     * <ul>
     * <li>The part 'B3 * (1 - la_2) * parmK4 * C4' goes from C4 to C5.
     * <li>The part 'la_2 * parmK4 * C4' goes from C4 to C6.
     * <li>The part '(1 - B3) * (1 - la_2) * parmK4 * C4' goes from C4 to C7.
     * </ul>
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param C4_C1_ass
     * @param C4_C2_ass
     * @return the daily change in C4
     */
    static double Eq04_getDailyChangeC4(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartments compartments,
                                        double C4_C1_ass,
                                        double C4_C2_ass) {
        return C4_C1_ass + C4_C2_ass - compartments.C4 * carbonModel.parmK4 * carbonModel.Tmod;
    }

    /**
     * Calculate the daily change in C stock in compartment C5.<p>
     * <ul>
     * <li>The part 'la_3 * parmK5 * C5' goes from C5 to C8.
     * <li>The part '(1 - la_3) * parmK5 * C5' goes grom C5 to C7.
     * </ul>
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param C5_C4_gen
     * @param C5_C2_frg
     * @param C5_C3_frg
     * @return the daily change in C5
     */
    static double Eq05_getDailyChangeC5(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartments compartments,
                                        double C5_C4_gen,
                                        double C5_C2_frg,
                                        double C5_C3_frg) {
        return C5_C4_gen + C5_C2_frg + C5_C3_frg - carbonModel.parmK5 * carbonModel.Tmod * compartments.C5;
    }

    /**
     * Calculate the daily change in C stock in compartment C6.<p>
     * <ul>
     * <li>The part 'DOC_frg * C6' goes from C6 to C8.
     * </ul>
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param C6_i_in
     * @param C6_C1_in
     * @param C6_C2_in
     * @param C6_C3_in
     * @param C6_C4_in
     * @param C8_C6_in
     * @return the daily change in C6
     */
    static double Eq06_getDailyChangeC6(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartments compartments,
                                        double C6_i_in,
                                        double C6_C1_in,
                                        double C6_C2_in,
                                        double C6_C3_in,
                                        double C6_C4_in,
                                        double C8_C6_in) {
        return C6_i_in + C6_C1_in + C6_C2_in + C6_C3_in + C6_C4_in - C8_C6_in;
    }

    /**
     * Calculate the daily change in C stock in compartment C7.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param C1_C02
     * @param C2_C02
     * @param C3_C02
     * @param C4_C02
     * @param C5_C02
     * @param C8_C02
     * @param C9_C02
     * @param C10_C02
     * @return the daily change in C7
     */
    static double Eq07_getDailyChangeC7(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartments compartments,
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
     * Calculate the daily change in C stock in compartment C8.<p>
     * <ul>
     * <li>The part 'sorption' goes from C8 to C9.
     * <li>The part 'DOC_lch * C8' goes from C8 to C11.
     * <li>The part 'parmK8 * C8' goes from C8 to C7.
     * </ul>
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param C8_C5_in
     * @param C8_C6_in
     * @param C8_C10_in
     * @param sorption
     * @return the daily change in C8
     */
    static double Eq08_getDailyChangeC8(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartments compartments,
                                        double C8_C5_in,
                                        double C8_C6_in,
                                        double C8_C10_in,
                                        double sorption) {
        double C8 = compartments.C8;
        return C8_C5_in + C8_C6_in + C8_C10_in - sorption * carbonModel.Tmod - C8 * carbonModel.DOC_lch * carbonModel.Tmod - C8 * carbonModel.parmK8 * carbonModel.Tmod;
    }

    /**
     * Calculate the daily change in C stock in compartment C9.<p>
     * <ul>
     * <li>The part 'C9 * parmK9' goes from C9 to C7.
     * </ul>
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param sorption
     * @return the daily change in C9
     */
    static double Eq09_getDailyChangeC9(SoilCarbonPredictor carbonModel,
                                        SoilCarbonPredictorCompartments compartments,
                                        double sorption) {
        return sorption * carbonModel.Tmod - compartments.C9 * carbonModel.parmK9 * carbonModel.Tmod;
    }

    /**
     * Calculate the daily change in C stock in compartment C10.<p>
     * <ul>
     * <li>The part 'la_3 * C10 * parmK10' goes from C10 to C8.
     * </ul>
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param C10_C2_frg
     * @param C10_C3_frg
     * @return the daily change in C10
     */
    static double Eq10_getDailyChangeC10(SoilCarbonPredictor carbonModel,
                                         SoilCarbonPredictorCompartments compartments,
                                         double C10_C2_frg,
                                         double C10_C3_frg) {
        return  C10_C2_frg + C10_C3_frg - compartments.C10 * carbonModel.parmK10 * carbonModel.Tmod;
    }

    /**
     * Calculate the daily change in C stock in compartment C11.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the daily change in C11
     */
    static double Eq11_getDailyChangeC11(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        return compartments.C8 * carbonModel.DOC_lch * carbonModel.Tmod;
    }

    /**
     * Calculate the daily input in compartment C1.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param inputs a SoilCarbonPredictorInput instance
     * @return the input of hot-water extractable carbon in compartment C1
     */
    static double Eq12_getDailyInputInC1(SoilCarbonPredictor carbonModel, SoilCarbonPredictorInput inputs) {
        return inputs.CT_i * inputs.landType.f_sol * (1 - carbonModel.f_DOC);
    }

    /**
     * Calculate the daily input in compartment C2.
     * @param CT_i the daily input from external source i
     * @param f_sol Hot-water extractable fraction from the litter
     * @param f_lig Acid-insoluble fraction from the litter
     * @return the daily input in compartment C2
     */
    static double Eq13_getDailyInputInC2(double CT_i, double f_sol, double f_lig) {
        return CT_i - (CT_i * (f_sol + f_lig));
    }

    /**
     * Calculate the daily input in compartment C3.
     * @param CT_i the daily input from external source i
     * @param f_lig Acid-insoluble fraction from the litter
     * @return the daily input in compartment C3
     */
    static double Eq14_getDailyInputInC3(double CT_i, double f_lig) {
        return CT_i * f_lig;
    }

    /**
     * Calculate the daily input in compartment C6.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param CT_i the daily input from external source i
     * @param f_sol Hot-water extractable fraction from the litter
     * @return the daily input in compartment C3
     */
    static double Eq15_calculate(SoilCarbonPredictor carbonModel, double CT_i, double f_sol) {
        return CT_i * f_sol * carbonModel.f_DOC;
    }

    /**
     * Compute the LCI parameter.<p>
     * 
     * The LCI is defined as the ratio between acid-insoluble and acid-soluble + acid-insoluble, that is<p>
     * LCI = [lignine / (lignine + cellulose)] (Soong et al., 2015)
     *
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the LCI parameter
     */
    static double Eq16_getLCI(SoilCarbonPredictorCompartments compartments) {
        return compartments.C3 / (compartments.C2 + compartments.C3);
    }

    /**
     * Calculate the C stock of C1 assimilated in C4 (C4<sup>C1</sup><sub>ass</sub>)
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param uB
     * @param la_4
     * @param uk
     * @return a double
     */
    static double Eq17_getDailyCarbonStockTransferFromC1ToC4(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartments compartments,
                                                             double uB,
                                                             double la_4,
                                                             double uk) {
        return uB * carbonModel.parmB1 * (1 - la_4) * uk * carbonModel.parmK1 * carbonModel.Tmod * compartments.C1;
    }

    /**
     * Calculate the C stock of C2 assimilated in C4 (C4<sup>C2</sup><sub>ass</sub>)
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param uB
     * @param la_1
     * @param uk
     * @return a double
     */
    static double Eq18_getDailyCarbonStockTransferFromC2ToC4(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartments compartments,
                                                             double uB,
                                                             double la_1,
                                                             double uk) {
        return uB * carbonModel.parmB2 * (1 - la_1) * uk * carbonModel.parmK2 * carbonModel.Tmod * compartments.C2;
    }

        
    /**
     * Calculate the daily modifier for the chemical control of the litter over the carbon use efficiency.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param N_lit nitrogen concentration of the input material
     * @return the modifier
     */
    static double Eq19_getModifier(SoilCarbonPredictor carbonModel,
                                   SoilCarbonPredictorCompartments compartments,
                                   double N_lit) {
//        double t1 = 1d / (1 + Math.exp(-carbonModel.N_max) * (N_lit - carbonModel.N_mid));
//        double t2 = 1 - Math.exp(-0.7 * (Math.abs(Eq16_getLCI(compartments) - 0.7) * 10));
        return Math.min(1d / (1 + Math.exp(-carbonModel.N_max) * (N_lit - carbonModel.N_mid)),
                1 - Math.exp(-0.7 * (Math.abs(Eq16_getLCI(compartments) - 0.7) * 10))); 
    }

    /**
     * Calculate the modifier associated with chemical control.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param N_lit nitrogen concentration of input material
     * @param LCI_lit
     * @return the modifier
     */
    static double Eq20_getModifier(SoilCarbonPredictor carbonModel,
                                   SoilCarbonPredictorCompartments compartments,
                                   double N_lit,
                                   double LCI_lit) {
        return Math.min(1d / (1 + Math.exp(-carbonModel.N_max)*(N_lit - carbonModel.N_mid)), Math.exp(-3 * LCI_lit));
    }

    /**
     * Calculate the proportion of C leached from the decomposition of C2.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param N_lit the nitrogen content of the input material
     * @return the proportion of leached C
     */
    static double Eq21_getLeachingLA1(SoilCarbonPredictor carbonModel,
                                      SoilCarbonPredictorCompartments compartments,
                                      double N_lit) {
        return Math.min(carbonModel.E_Hmax - (carbonModel.E_Hmax - carbonModel.E_Hmin)/carbonModel.LCI_max * Eq16_getLCI(compartments),
                carbonModel.E_Hmax - (carbonModel.E_Hmax - carbonModel.E_Hmin) / carbonModel.N_max * N_lit);
    }

    /**
     * Provides the proportion of C leached from the decomposition of C1.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param N_lit the nitrogen content in the input material
     * @return
     */
    static double Eq22_getLeachingLA4(SoilCarbonPredictor carbonModel,
                                      SoilCarbonPredictorCompartments compartments,
                                      double N_lit) {
        return Math.min(carbonModel.E_smax - (carbonModel.E_smax - carbonModel.E_smin) / carbonModel.LCI_max * Eq16_getLCI(compartments),
                carbonModel.E_smax - (carbonModel.E_smax - carbonModel.E_smin) / carbonModel.N_max * N_lit);
    }

    /**
     * Calculate the daily transfer from C4 to C5.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C4 to C5
     */
    static double Eq23_getDailyCarbonStockTransferFromC4ToC5(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        return carbonModel.parmB3 * (1 - carbonModel.la_2) * carbonModel.parmK4 * carbonModel.Tmod * compartments.C4;
    }

    /**
     * Calculate the daily transfer from C2 to C5.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C2 to C5
     */
    static double Eq24_getDailyCarbonStockTransferFromC2ToC5(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        return carbonModel.POM_split * carbonModel.LIT_frg * compartments.C2 * carbonModel.Tmod;
    }

    /**
     * Calculate the daily transfer from C3 to C5.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C3 to C5
     */
    static double Eq25_getDailyCarbonStockTransferFromC3ToC5(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) { 
    	// TODO definition of C3 must be clarified
        return carbonModel.POM_split * carbonModel.LIT_frg * compartments.C3 * carbonModel.Tmod;
    }

    /**
     * Calculate the daily transfer from C2 to C10.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C2 to C10
     */
    static double Eq26_getDailyCarbonStockTransferFromC2ToC10(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        return (1 - carbonModel.POM_split) * carbonModel.LIT_frg * compartments.C2 * carbonModel.Tmod;
    }

    /**
     * Calculate the daily transfer from C3 to C10.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C3 to C10
     */
    static double Eq27_getDailyCarbonStockTransferFromC3ToC10(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        return (1 - carbonModel.POM_split) * carbonModel.LIT_frg * compartments.C3 * carbonModel.Tmod;
    }

    /**
     * Calculate the daily transfer from C1 to C6.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param la_4
     * @param uk
     * @return the C stock transferred from C1 to C6
     */
    static double Eq28_getDailyCarbonStockTransferFromC1ToC6(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartments compartments,
                                                             double la_4,
                                                             double uk) {
        return la_4 * uk * carbonModel.parmK1 * carbonModel.Tmod * compartments.C1;
    }

    /**
     * Calculate the daily transfer from C2 to C6.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param la_1
     * @param uk
     * @return the C stock transferred from C2 to C6
     */
    static double Eq29_getDailyCarbonStockTransferFromC2ToC6(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartments compartments,
                                                             double la_1,
                                                             double uk) {
        return la_1 * uk * carbonModel.parmK2 * carbonModel.Tmod * compartments.C2;
    }

    /**
     * Calculate the daily transfer from C3 to C6.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C3 to C6
     */
    static double Eq30_getDailyCarbonStockTransferFromC3ToC6(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        return carbonModel.la_3 * carbonModel.parmK3 * carbonModel.Tmod * compartments.C3;
    }

    /**
     * Calculate the daily transfer from C4 to C6.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C4 to C6
     */
    static double Eq31_getDailyCarbonStockTransferFromC4ToC6(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        return carbonModel.la_2 * carbonModel.parmK4 * carbonModel.Tmod * compartments.C4;
    }

    /**
     * Calculate the daily transfer from C5 to C8 (C8<sup>C5</sup><sub>in</sub>).
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C5 to C8
     */
    static double Eq32_getDailyCarbonStockTransferFromC5ToC8(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        return carbonModel.la_3 * carbonModel.parmK5 * carbonModel.Tmod * compartments.C5;
    }

    /**
     * Calculate the daily transfer from C6 to C8.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C6 to C8
     */
    static double Eq33_getDailyCarbonStockTransferFromC6ToC8(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        return carbonModel.DOC_frg * compartments.C6 * carbonModel.Tmod;
    }

    /**
     * Calculate the daily transfer from C10 to C8.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C10 to C8
     */
    static double Eq34_getDailyCarbonStockTransferFromC10ToC8(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        return carbonModel.la_3 * carbonModel.parmK10 * carbonModel.Tmod * compartments.C10;
    }

    /**
     * Calculate the 'binding affinity' factor.
     * @param soil_pH soil pH
     * @return the binding affinity factor
     */
    static double Eq35_getBindingAffinityL_k_lm(double soil_pH) {
        //TODO Value range [0.07, 0.02] is inconsistent with the default L_k_lm parameter (0.25 g C per day)
        return Math.pow(10, -0.186 * soil_pH - 0.216);
    }

//	double getDefaultBindingAffinityL_k_lm() {
//		return carbonModel.L_k_lm;
//	}

    /**
     * Calculate the sorption capacity.
     * @param bulkDensity the bulk density 
     * @param sandProportion sand proportion in percentage
     * @param rockProportion rock proportion in percentage
     */
    static double Eq36_getMaximumSorptionCapacityQ_max(double bulkDensity, double sandProportion, double rockProportion) {
        // (1 - rockProportion) has been changed to 100 - rockProportion after validating the equation in the paper
        return bulkDensity * (0.26126 * (100 - sandProportion) + 11.07820) * (100 - rockProportion);
    }

    /**
     * Calculate the sorption.
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param K_lm the binding affinity factor (Eq.35)
     * @param Q_max the sorption capacity (Eq.36)
     * return the actual sorption
     */
    static double Eq37_getSorption(SoilCarbonPredictorCompartments compartments, double K_lm, double Q_max) {
        double C8 = compartments.C8;
        double C9 = compartments.C9;
        // TODO : check if K_lm and j_K_lm are really the same variable here
        return C8 * (K_lm * Q_max * C8 / (1 + K_lm * C8) - C9) / Q_max;
    }

    /**
     * Calculate the daily transfer from C1 to C7.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param uB
     * @param la_4
     * @param uk
     * @return the C stock transferred from C1 to C7
     */
    static double Eq38_getDailyCarbonStockTransferFromC1ToC7(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartments compartments,
                                                             double uB,
                                                             double la_4,
                                                             double uk) {
        return (1 - uB * carbonModel.parmB1) * (1 - la_4) * uk * carbonModel.parmK1 * carbonModel.Tmod * compartments.C1;
    }

    /**
     * Calculate the daily transfer from C2 to C7.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @param uB
     * @param la_1
     * @param uk
     * @return the C stock transferred from C2 to C7
     */
    static double Eq39_getDailyCarbonStockTransferFromC2ToC7(SoilCarbonPredictor carbonModel,
                                                             SoilCarbonPredictorCompartments compartments,
                                                             double uB,
                                                             double la_1,
                                                             double uk) {
        return (1 - uB * carbonModel.parmB2) * (1 - la_1) * uk * carbonModel.parmK2 * carbonModel.Tmod * compartments.C2;
    }

    /**
     * Calculate the daily transfer from C3 to C7.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C3 to C7
     */
    static double Eq40_getDailyCarbonStockTransferFromC3ToC7(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        return (1 - carbonModel.la_3) * carbonModel.parmK3 * carbonModel.Tmod * compartments.C3;
    }

    /**
     * Calculate the daily transfer from C4 to C7.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C4 to C7
     */
    static double Eq41_getDailyCarbonStockTransferFromC4ToC7(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        return (1 - carbonModel.parmB3) * (1 - carbonModel.la_2) * carbonModel.parmK4 * carbonModel.Tmod * compartments.C4;
    }

    /**
     * Calculate the daily transfer from C5 to C7.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C5 to C7
     */
    static double Eq42_getDailyCarbonStockTransferFromC5ToC7(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        return (1 - carbonModel.la_3) * carbonModel.parmK5 * carbonModel.Tmod * compartments.C5;
    }

    /**
     * Calculate the daily transfer from C8 to C7.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C8 to C7
     */
    static double Eq43_getDailyCarbonStockTransferFromC8ToC7(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        return carbonModel.parmK8 * carbonModel.Tmod * compartments.C8;
    }

    /**
     * Calculate the daily transfer from C9 to C7.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C9 to C7
     */
    static double Eq44_getDailyCarbonStockTransferFromC9ToC7(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        return carbonModel.parmK9 * carbonModel.Tmod * compartments.C9;
    }

    /**
     * Calculate the daily transfer from C10 to C7.
     * @param carbonModel a SoiCarbonPredictor instance
     * @param compartments a MSoilCarbonPredictorCompartmentsatrix instance with the initial stocks in each compartment
     * @return the C stock transferred from C10 to C7
     */
    static double Eq45_getDailyCarbonStockTransferFromC10ToC7(SoilCarbonPredictor carbonModel, SoilCarbonPredictorCompartments compartments) {
        // Note : parmK3 was changed to parmK10 so that the outputs balance properly
        return (1 - carbonModel.la_3) * carbonModel.parmK10 * carbonModel.Tmod * compartments.C10;
    }

    /**
     * Calculate K3 estimate
     * @param carbonModel a SoilCarbonPredictor instance
     * @return the estimate for param K3
     */
    static double Eq46_getK3Estimate(SoilCarbonPredictor carbonModel, double LCI_lit) {
        return carbonModel.parmK2 * (0.2 / (1 + (200.0 / Math.exp(8.15 * LCI_lit))));
    }

    /**
     * Calculate the temperature modifier
     * @param carbonModel a SoiCarbonPredictor instance
     * @param soilT the soil temperature
     * @return the temperature modifier (Tmod) to be used as a scaling factor for soil compartment estimation
     */
    static double Eq48_getTemperatureModifier(SoilCarbonPredictor carbonModel, double soilT) {
        return Math.exp(Math.pow(-(soilT / (carbonModel.Topt + carbonModel.Tlag)), carbonModel.Tshp)) * Math.pow(carbonModel.Tq10, (soilT - carbonModel.Tref) / carbonModel.Tref);
    }

    /**
     * Calculate the temperature modifier (mems2 version)
     * @param carbonModel a SoiCarbonPredictor instance
     * @param soilT the soil temperature
     * @return the temperature modifier (Tmod / Teff) to be used as a scaling factor for soil compartment estimation
     */
    static double Mems2_Eq26_getTemperatureModifier(SoilCarbonPredictor carbonModel, double soilT) {
        return (PI / 2.0 + atan((carbonModel.coeff_t1 * (soilT - carbonModel.coeff_t2)) * PI / 180.0)) / PI;
    }

    static double Weibull_getTemperatureModifier(SoilCarbonPredictor carbonModel, double soilT) {
//        return soilT <= 0.0 ? 0.0 : 1.0 - Math.exp(-Math.pow(soilT * 0.125, 2.0));
    	return soilT <= 0.0 ? 0.0 : 1.0 - Math.exp(-soilT * 0.125 * soilT * 0.125);
    }

    /**
     * Calculate the soil temperature
     * @param julianDay the julian day index to get the temperature for
     * @param MAT Mean Annual Temperature to be used
     * @param Trange Annual Soil Temperature range (Celsius)
     * @return the soil temperature (soilT) for this specific julian day
     */
    static double Eq49_getSoilTemperatureFromMeanAndRange(int julianDay, double MAT, double Trange) {
        return (Trange * .5) * JulianDaySinValuesCache[julianDay - 1] + MAT;
//        return (Trange * .5) * sin((julianDay * DividedBy365) * 2 * PI - 1.5) + MAT;
    }

    /**
     * Calculate the corrected below ground carbon input (topsoil carbon only is being considered)
     * @param bCTij belowground carbon input of material i on day j
     * @param depth depth (in cm)
     * @param Rdep50 soil depth from the surface at which 50 % of the root biomass is proportioned (in cm)
     * @param Rdepmax maximum rooting depth (in cm)
     * @return the reduced belowground carbon input of material i on day j (topsoil carbon only is being considered)
     */
    static double Eq53_getCorrectedBelowGroundCarbonInput(double bCTij, double depth, double Rdep50, double Rdepmax) {
        return bCTij * ((depth * (Rdep50 + Rdepmax)) / (Rdepmax * (Rdep50 + depth)));
    }
}
