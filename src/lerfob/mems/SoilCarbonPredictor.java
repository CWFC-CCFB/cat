package lerfob.mems;

import repicea.simulation.REpiceaPredictor;

@SuppressWarnings("serial")
public class SoilCarbonPredictor extends REpiceaPredictor {

	/**
	 * Maximum daily decomposition rate of water soluble carbon in litter deaful value 0.37 (0.16-0.70) Source: Campbell et al. 2016
	 */
	double parmK1 = 0.37; 
	
	/**
	 * La fraction extractible � l'eau froide de l'apport de liti�re extractible � l'eau chaude 
	 * (= fraction de liti�re soluble qui bypass les processus microbien et qui est imm�diatement 
	 * rel�ch�e de la liti�re v�g�tale dans la pool de DOM par lixiviation).
	 * Par defaut 0.15 (0.09-0.21)
	 */
	double f_DOC = 0.15; 
	
	/**
	 * Teneur maximale en N qui influence les taux (au-del�, il n'y a pas de limite) 
	 * de g�n�ration de DOM et d'assimilation microbienne du carbone
	 * Valeur par d�faut : 3 %
	 * R�f�rence : Sinsabaugh et al. (2013) 
	 */
	double N_max = 0.03; // TODO was specified at 3%: is it 3 or .03 that should be hard coded?
	
	/**
	 * Point m�dian de la fonction logistique qui d�crit la limitation de N
	 * Valeur par d�faut : 1.75 %
	 * R�f�rences : Campbell et al. (2016); Soong et al. (2015) 
	 */
	double N_mid = 0.0175; // TODO was specified at 1.75%: is it 1.75 or .0175 that should be hard coded?
			
	/**
	 * Taux de d�composition maximal du carbone de la liti�re soluble dans l�acide (C2)
	 * Valeur par d�faut : 0.009 / jour
	 * Intervalle : 0.0011 � 0.0200 / jour
	 * R�f�rence : Campbell et al. (2016)
	 */
	double parmK2 = 0.009; 
	
	/**
	 * Carbone dans les apports de liti�re structurelle (C2 et C3) transport�s vers la mati�re 
	 * organique particulaire du sol (C5 et C10) � chaque pas de temps
	 * Valeur par d�faut : 0.006 g C par g C d�compos�
	 * Intervalle : 1 � 10-5 � 2 � 10-3 g C par g C d�compos�
	 * R�f�rence : MEMS v1.0
	 */
	double LIT_frg = 0.006;
	
	/**
	 * Taux de d�composition maximal du carbone de la liti�re insoluble dans l�acide (C3)
	 * Valeur par d�faut : 0.0002 / jour
	 * Intervalle : 2 � 10-5 � 1 � 10-3 / jour
	 * R�f�rence : Moorhead et al. (2013) 
	 */
	double parmK3 = 0.0002;
	
	/**
	 * Efficacit� de croissance maximale de l'utilisation microbienne du carbone 
	 * de la liti�re soluble dans l'eau (C1)
	 * Valeur par d�faut : 0.6 g C biomasse microbienne par g d�compos�
	 * Intervalle : 0.4 � 0.7 g C biomasse microbienne par g d�compos�
	 * R�f�rences : Sinsabaugh et al. (2013) 
	 */
	double parmB1 = 0.6;
	
	/**
	 * Quantit� maximale de carbone lixivi� du carbone de la liti�re soluble 
	 * dans l'eau d�compos� (C1) vers la couche de liti�re DOM (C6)
	 * Valeur par d�faut : 0.15 g DOM-C par g C d�compos�
	 * R�f�rences : Campbell et al. (2016) 
	 */
	double E_smax = 0.15;
	
	/**
	 * Quantit� minimale de carbone lixivi� du carbone de la liti�re soluble 
	 * dans l'eau d�compos� (C1) vers la couche de liti�re DOM (C6)
	 * Valeur par d�faut : 0.005 g DOM-C par g C d�compos�
	 * R�f�rences : Campbell et al. (2016) 
	 */
	double E_smin = 0.005;
	
	/**
	 * Indice lignocellulosique maximal qui influence la g�n�ration de DOM � 
	 * partir de la d�composition de la liti�re
	 * Valeur par d�faut : 0.51
	 * R�f�rences : Campbell et al. (2016); Soong et al. (2015) 
	 */
	double LCI_max = 0.51;
	
	/**
	 * Quantit� maximale de carbone lixivi� du carbone de la liti�re soluble 
	 * dans l'acide d�compos� (C2) vers la couche de liti�re DOM (C6)
	 * Valeur par d�faut : 0.15 g DOM-C par g C d�compos�
	 * R�f�rences : Campbell et al. (2016) 
	 */
	double E_Hmax = 0.15;
	
	/**
	 * Quantit� minimale de carbone lixivi� du carbone de la liti�re soluble 
	 * dans l'acide d�compos� (C2) vers la couche de liti�re DOM (C6) 
	 * Valeur par d�faut : 0.005 g DOM-C par g C d�compos�
	 * R�f�rences : Campbell et al. (2016) 
	 */
	double E_Hmin = 0.005;
	
	/**
	 * Efficacit� de croissance maximale de l'utilisation microbienne 
	 * du carbone de liti�re structurelle soluble dans l'acide (C2)
	 * Valeur par d�faut : 0.5 g C biomasse microbienne par g d�compos�
	 * Intervalle : 0.3 � 0.6 g C biomasse microbienne par g d�compos�
	 * R�f�rences : Sinsabaugh et al. (2013) 
	 */
	double parmB2 = 0.5;
	
	/**
	 * Taux de d�composition maximal du carbone de la biomasse microbienne (C4)
	 * Valeur par d�faut : 0.57 / jour
	 * Intervalle : 0.11 � 0.97 / jour
	 * R�f�rences : Campbell et al. (2016)
	 */
	double parmK4 = 0.57;

	
	/**
	 * Production de mati�re organique particulaire lourde et grossi�re (C5) 
	 * � partir de la d�composition du carbone de la biomasse microbienne (C4)
	 * Valeur par d�faut : 0.33 g C produits microbiens par g C d�compos�
	 * Intervalle : 0.028 � 0.79 g C produits microbiens par g C d�compos�
	 * R�f�rences : Campbell et al. (2016) 
	 */
	double parmB3 = 0.33;

	/**
	 * Carbone lixivi� du carbone de la biomasse microbienne en d�composition (C4)
	 * Valeur par d�faut : 0.19 g DOM-C par g C d�compos�
	 * Intervalle : 0.022 � 0.42 g DOM-C par g C d�compos�
	 * R�f�rences : Campbell et al. (2016) 
	 */
	double la_2 = 0.19;
	
	/**
	 * Fraction des apports de liti�re fragment�s qui forment de la mati�re organique particulaire (POM) lourde (C5) [�chelle entre 0 et 1]
	 * Valeur par d�faut : 0.30
	 * Intervalle : 0.07 � 0.83
	 * R�f�rences : Poeplau and Don (2013); Soong et al. (2016)
	 */
	double POM_split = .3;
	
	/**
	 * Taux de d�composition maximal des particules lourdes et 
	 * grossi�res de mati�re organique du sol (C5)
	 * Valeur par d�faut : 0.0005 / jour
	 * Intervalle : 6 � 10-5 � 1 � 10-3 / jour
	 * R�f�rences : Campbell et al. (2016); Del Galdo et al. (2003) 
	 */
	double parmK5 = 0.0005;
	
	
	/**
	 * Carbone lixivi� du carbone de la liti�re insoluble 
	 * dans l'acide et du carbone de la mati�re organique 
	 * particulaire lourde et grossi�re (C3 et C5)
	 * Valeur par d�faut : 0.038 g C-DOM par g C d�compos�
	 * Intervalle : 0.014 � 0.050 g C-DOM par g C d�compos�
	 * R�f�rence : Campbell et al. (2016); Soong et al. (2015) 
	 */
	double la_3 = 0.038; 
	
	/**
	 * Carbone dans la couche de DOM de la liti�re (C6) 
	 * transport� vers le DOM du sol (C8) � chaque pas de temps
	 * Valeur par d�faut : 0.8 g C-DOM par g C-DOM
	 * Intervalle : 0.2 � 0.99 g C-DOM par g C-DOM
	 * R�f�rences : MEMS v1.0
	 */
	double DOC_frg = 0.8;
	
	/**
	 * Taux de d�composition maximal du carbone 
	 * organique dissout (DOM) du sol (C8)
	 * Valeur par d�faut : 0.00144 / jour
	 * R�f�rences : Kalbitz et al. (2005) 
	 */
	double parmK8 = 0.00144;
	
	/**
	 * Taux de d�composition maximal de la mati�re organique 
	 * du sol associ�e aux min�raux (C9)
	 * Valeur par d�faut : 2.2 � 10-5 / jour
	 * Intervalle : 1 � 10-5 � 4 � 10-5 / jour
	 * R�f�rences : Del Galdo et al. (2003) 
	 */
	double parmK9 = 2.2E-5;
	
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
