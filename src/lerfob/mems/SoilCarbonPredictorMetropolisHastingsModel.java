/*
 * This file is part of the mems library.
 *
 * Copyright (C) 2022-24 His Majesty the King in Right of Canada
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

import repicea.math.Matrix;
import repicea.math.utility.GaussianUtility;
import repicea.stats.data.DataSet;
import repicea.stats.data.GenericStatisticalDataStructure;
import repicea.stats.distributions.GaussianDistribution;
import repicea.stats.estimators.mcmc.*;
import repicea.stats.distributions.UniformDistribution;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An implementation of MetropolisHastingsCompatibleModel for MEMS.
 * @author Jean-Francois Lavoie - March 2024
 */
public class SoilCarbonPredictorMetropolisHastingsModel implements MetropolisHastingsCompatibleModel {

//    protected final MetropolisHastingsAlgorithm mh;
//    protected MetropolisHastingsParameters mhSimParms;
    protected GenericStatisticalDataStructure gsds;

    private Matrix VectorY;
    private Matrix MatrixX;
    SoilCarbonPredictor predictor;
    SoilCarbonPredictorCompartments compartments;
    SoilCarbonPredictorInput input;

    double meanSoil;
    double meanLitter;

    SoilCarbonPredictorMetropolisHastingsModel(SoilCarbonPredictorCompartments compartments, SoilCarbonPredictorInput input) {
        this.compartments = compartments;
        this.input = input;

        predictor = new SoilCarbonPredictor(false);
    }

    // MetropolisHastingsCompatibleModel implementation
    @Override
    public double getLogLikelihood(Matrix parms) {
        predictor.SetParms(parms);

        SoilCarbonPredictorCompartments localCompartments = compartments.clone();

        for (int i = 0; i < 100; i++) {
            predictor.predictAnnualCStocks(localCompartments, input);
        }

        meanSoil = localCompartments.getSoilBinsgCm2() / 100.0;
        meanLitter = localCompartments.getLitterBinsgCm2() / 100.0;

        double llk = 0.0;

        for (int i = 0; i < getNbSubjects(); i++) {
            llk += Math.log(getLikelihoodOfThisSubject(parms, i));
        }

        return llk;
    }

    @Override
    public int getNbSubjects() {
        return gsds.getNumberOfObservations();
    }

    @Override
    public double getLikelihoodOfThisSubject(Matrix parms, int subjectId) {
        boolean isLitter = MatrixX.getValueAt(subjectId, 0) == 0d;
        double mu = isLitter ? meanLitter : meanSoil;
        double s2 =  isLitter ? parms.getValueAt(SoilCarbonPredictor.MCParam.sigma2Litter.ordinal(), 0) : parms.getValueAt(SoilCarbonPredictor.MCParam.sigma2Soil.ordinal(), 0);

        double y = VectorY.getValueAt(subjectId, 0);
        return GaussianUtility.getProbabilityDensity(y, mu, s2);
    }

    @Override
    public GaussianDistribution getStartingParmEst(double coefVar) {
        SoilCarbonPredictor.MCParam[] testarray = SoilCarbonPredictor.MCParam.values();
        Matrix parmEst = new Matrix(SoilCarbonPredictor.MCParam.values().length,1);
        for (SoilCarbonPredictor.MCParam value : SoilCarbonPredictor.MCParam.values()) {
            parmEst.setValueAt(value.ordinal(), 0, value.getInitialValue());
        }

        Matrix varianceDiag = new Matrix(parmEst.m_iRows,1);
        for (int i = 0; i < varianceDiag.m_iRows; i++) {
            varianceDiag.setValueAt(i, 0, Math.pow(parmEst.getValueAt(i, 0) * coefVar, 2d));
        }

        GaussianDistribution gd = new GaussianDistribution(parmEst, varianceDiag.matrixDiagonal());

        return gd;
    }

    // AbstractEstimator implementation
    @Override
    public List<String> getOtherParameterNames() {
        return new ArrayList<String>();
    }

    @Override
    public int getNumberOfObservations() {
        return gsds.getNumberOfObservations();
    }
    
    @Override
    public List<String> getEffectList() {
    	return Arrays.asList(SoilCarbonPredictor.MCParam.values()).stream().map(p -> p.name()).collect(Collectors.toList());
    }
    
    @Override
    public boolean isInterceptModel() {
        return gsds.isInterceptModel();
    }

    void readFile(String filename) throws Exception {
        DataSet ds = new DataSet(filename, true);

        gsds = new GenericStatisticalDataStructure(ds);
        gsds.setInterceptModel(false);
        gsds.setModelDefinition("SocMgC_ha~SoilDepthCm");
        VectorY = gsds.constructVectorY();
        MatrixX = gsds.constructMatrixX();
    }
    
	@Override
	public void setPriorDistributions(MetropolisHastingsPriorHandler handler) {
		handler.clear();
        for (SoilCarbonPredictor.MCParam value : SoilCarbonPredictor.MCParam.values()) {
            handler.addFixedEffectDistribution(new UniformDistribution(value.getRangeMin(), value.getRangeMax()), value.ordinal());
        }
	}
	
	
}
