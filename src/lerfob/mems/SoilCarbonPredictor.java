package lerfob.mems;

import repicea.simulation.REpiceaPredictor;

@SuppressWarnings("serial")
public class SoilCarbonPredictor extends REpiceaPredictor {

	/**
	 * Maximum daily decomposition rate of water soluble carbon in litter deaful value 0.37 (0.16-0.70) Source: Campbell et al. 2016
	 */
	double parmK1 = 0.37; 
	
	/**
	 * La fraction extractible à l'eau froide de l'apport de litière extractible à l'eau chaude 
	 * (= fraction de litière soluble qui bypass les processus microbien et qui est immédiatement 
	 * relâchée de la litière végétale dans la pool de DOM par lixiviation).
	 * Par defaut 0.15 (0.09-0.21)
	 */
	double f_DOC = 0.15; 
	
	/**
	 * Teneur maximale en N qui influence les taux (au-delà, il n'y a pas de limite) 
	 * de génération de DOM et d'assimilation microbienne du carbone
	 * Valeur par défaut : 3 %
	 * Référence : Sinsabaugh et al. (2013) 
	 */
	double N_max = 0.03; // TODO was specified at 3%: is it 3 or .03 that should be hard coded?
	
	/**
	 * Point médian de la fonction logistique qui décrit la limitation de N
	 * Valeur par défaut : 1.75 %
	 * Références : Campbell et al. (2016); Soong et al. (2015) 
	 */
	double N_mid = 0.0175; // TODO was specified at 1.75%: is it 1.75 or .0175 that should be hard coded?
			
	/**
	 * Taux de décomposition maximal du carbone de la litière soluble dans l’acide (C2)
	 * Valeur par défaut : 0.009 / jour
	 * Intervalle : 0.0011 – 0.0200 / jour
	 * Référence : Campbell et al. (2016)
	 */
	double parmK2 = 0.009; 
	
	/**
	 * Carbone dans les apports de litière structurelle (C2 et C3) transportés vers la matière 
	 * organique particulaire du sol (C5 et C10) à chaque pas de temps
	 * Valeur par défaut : 0.006 g C par g C décomposé
	 * Intervalle : 1 × 10-5 – 2 × 10-3 g C par g C décomposé
	 * Référence : MEMS v1.0
	 */
	double LIT_frg = 0.006;
	
	/**
	 * Taux de décomposition maximal du carbone de la litière insoluble dans l’acide (C3)
	 * Valeur par défaut : 0.0002 / jour
	 * Intervalle : 2 × 10-5 – 1 × 10-3 / jour
	 * Référence : Moorhead et al. (2013) 
	 */
	double parmK3 = 0.0002;
	
	/**
	 * Efficacité de croissance maximale de l'utilisation microbienne du carbone 
	 * de la litière soluble dans l'eau (C1)
	 * Valeur par défaut : 0.6 g C biomasse microbienne par g décomposé
	 * Intervalle : 0.4 – 0.7 g C biomasse microbienne par g décomposé
	 * Références : Sinsabaugh et al. (2013) 
	 */
	double parmB1 = 0.6;
	
	/**
	 * Quantité maximale de carbone lixivié du carbone de la litière soluble 
	 * dans l'eau décomposé (C1) vers la couche de litière DOM (C6)
	 * Valeur par défaut : 0.15 g DOM-C par g C décomposé
	 * Références : Campbell et al. (2016) 
	 */
	double E_smax = 0.15;
	
	/**
	 * Quantité minimale de carbone lixivié du carbone de la litière soluble 
	 * dans l'eau décomposé (C1) vers la couche de litière DOM (C6)
	 * Valeur par défaut : 0.005 g DOM-C par g C décomposé
	 * Références : Campbell et al. (2016) 
	 */
	double E_smin = 0.005;
	
	/**
	 * Indice lignocellulosique maximal qui influence la génération de DOM à 
	 * partir de la décomposition de la litière
	 * Valeur par défaut : 0.51
	 * Références : Campbell et al. (2016); Soong et al. (2015) 
	 */
	double LCI_max = 0.51;
	
	/**
	 * Quantité maximale de carbone lixivié du carbone de la litière soluble 
	 * dans l'acide décomposé (C2) vers la couche de litière DOM (C6)
	 * Valeur par défaut : 0.15 g DOM-C par g C décomposé
	 * Références : Campbell et al. (2016) 
	 */
	double E_Hmax = 0.15;
	
	/**
	 * Quantité minimale de carbone lixivié du carbone de la litière soluble 
	 * dans l'acide décomposé (C2) vers la couche de litière DOM (C6) 
	 * Valeur par défaut : 0.005 g DOM-C par g C décomposé
	 * Références : Campbell et al. (2016) 
	 */
	double E_Hmin = 0.005;
	
	/**
	 * Efficacité de croissance maximale de l'utilisation microbienne 
	 * du carbone de litière structurelle soluble dans l'acide (C2)
	 * Valeur par défaut : 0.5 g C biomasse microbienne par g décomposé
	 * Intervalle : 0.3 – 0.6 g C biomasse microbienne par g décomposé
	 * Références : Sinsabaugh et al. (2013) 
	 */
	double parmB2 = 0.5;
	
	/**
	 * Taux de décomposition maximal du carbone de la biomasse microbienne (C4)
	 * Valeur par défaut : 0.57 / jour
	 * Intervalle : 0.11 – 0.97 / jour
	 * Références : Campbell et al. (2016)
	 */
	double parmK4 = 0.57;

	
	/**
	 * Production de matière organique particulaire lourde et grossière (C5) 
	 * à partir de la décomposition du carbone de la biomasse microbienne (C4)
	 * Valeur par défaut : 0.33 g C produits microbiens par g C décomposé
	 * Intervalle : 0.028 – 0.79 g C produits microbiens par g C décomposé
	 * Références : Campbell et al. (2016) 
	 */
	double parmB3 = 0.33;

	/**
	 * Carbone lixivié du carbone de la biomasse microbienne en décomposition (C4)
	 * Valeur par défaut : 0.19 g DOM-C par g C décomposé
	 * Intervalle : 0.022 – 0.42 g DOM-C par g C décomposé
	 * Références : Campbell et al. (2016) 
	 */
	double la_2 = 0.19;
	
	/**
	 * Fraction des apports de litière fragmentés qui forment de la matière organique particulaire (POM) lourde (C5) [échelle entre 0 et 1]
	 * Valeur par défaut : 0.30
	 * Intervalle : 0.07 – 0.83
	 * Références : Poeplau and Don (2013); Soong et al. (2016)
	 */
	double POM_split = .3;
	
	/**
	 * Taux de décomposition maximal des particules lourdes et 
	 * grossières de matière organique du sol (C5)
	 * Valeur par défaut : 0.0005 / jour
	 * Intervalle : 6 × 10-5 – 1 × 10-3 / jour
	 * Références : Campbell et al. (2016); Del Galdo et al. (2003) 
	 */
	double parmK5 = 0.0005;
	
	
	/**
	 * Carbone lixivié du carbone de la litière insoluble 
	 * dans l'acide et du carbone de la matière organique 
	 * particulaire lourde et grossière (C3 et C5)
	 * Valeur par défaut : 0.038 g C-DOM par g C décomposé
	 * Intervalle : 0.014 – 0.050 g C-DOM par g C décomposé
	 * Référence : Campbell et al. (2016); Soong et al. (2015) 
	 */
	double la_3 = 0.038; 
	
	/**
	 * Carbone dans la couche de DOM de la litière (C6) 
	 * transporté vers le DOM du sol (C8) à chaque pas de temps
	 * Valeur par défaut : 0.8 g C-DOM par g C-DOM
	 * Intervalle : 0.2 – 0.99 g C-DOM par g C-DOM
	 * Références : MEMS v1.0
	 */
	double DOC_frg = 0.8;
	
	/**
	 * Taux de décomposition maximal du carbone 
	 * organique dissout (DOM) du sol (C8)
	 * Valeur par défaut : 0.00144 / jour
	 * Références : Kalbitz et al. (2005) 
	 */
	double parmK8 = 0.00144;
	
	/**
	 * Taux de décomposition maximal de la matière organique 
	 * du sol associée aux minéraux (C9)
	 * Valeur par défaut : 2.2 × 10-5 / jour
	 * Intervalle : 1 × 10-5 – 4 × 10-5 / jour
	 * Références : Del Galdo et al. (2003) 
	 */
	double parmK9 = 2.2E-5;
	
	/**
	 * Taux de décomposition maximal des particules légères de matière organique du sol (C10)
	 * Valeur par défaut : 2.96 × 10-5 / jour
	 * Intervalle : 4 × 10-3 – 1 × 10-4 / jour
	 * Références : Del Galdo et al. (2003) 
	 */
	double parmK10 = 2.96E-5;
	
	/**
	 *Binding affinity for carbon in soil DOM (C8) sorption to mineral surfaces (C9) of soil layer L. 
	 */
	double L_k_lm = 0.25;
	
	/**
	 * Taux spécifique maximal de lessivage pour représenter le transport vertical du carbone dans la MAOM 
	 * à travers le profil du sol
	 * Valeur par défaut : 0.00438 g C par jour
	 * Intervalle : 1 × 10-5 – 0.02 g C par jour
	 * Référence : Trumbore et al. (1992) 
	 */
	double DOC_lch = 0.00438;
	
	double C1;
	double C2;
	double C3;
	
	public SoilCarbonPredictor(boolean isVariabilityEnabled) {
		super(isVariabilityEnabled, isVariabilityEnabled, isVariabilityEnabled);
		// TODO we normally call init here
	}

	@Override
	protected void init() {
		// TODO Auto-generated method stub

	}

}
