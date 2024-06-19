/*
 * This file is part of the CAT library.
 *
 * Copyright (C) 2024 His Majesty the King in Right of Canada
 * Author: Mathieu Fortin, Canadian Forest Service, 
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * This library is distributed with the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied
 * warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * Please see the license at http://www.gnu.org/copyleft/lesser.html.
 */
package lerfob.carbonbalancetool.memsconnectors;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

import lerfob.carbonbalancetool.CATCompartment.CompartmentInfo;
import lerfob.carbonbalancetool.CATSimulationResult;
import lerfob.carbonbalancetool.CarbonAccountingTool;
import lerfob.carbonbalancetool.CarbonAccountingTool.CATMode;
import lerfob.carbonbalancetool.CarbonAccountingToolTest;
import lerfob.carbonbalancetool.io.CATGrowthSimulationPlot;
import lerfob.carbonbalancetool.io.CATGrowthSimulationRecordReader;
import lerfob.carbonbalancetool.io.CATGrowthSimulationTreeWithDBH;
import repicea.io.tools.ImportFieldManager;
import repicea.math.Matrix;
import repicea.math.SymmetricMatrix;
import repicea.simulation.covariateproviders.treelevel.TreeStatusProvider.StatusClass;
import repicea.stats.estimates.Estimate;
import repicea.util.ObjectUtility;

public class MEMSConnectorsTest {

	@SuppressWarnings("serial")
	static class CATGrowthSimulationRecordReaderHacked extends CATGrowthSimulationRecordReader {
		
		@Override
		protected CATGrowthSimulationTreeHacked createTree(CATGrowthSimulationPlot plot, 
				StatusClass statusClass, 
				double treeOverbarkVolumeDm3, 
				double numberOfTrees, 
				String originalSpeciesName,
				Double dbhCm) {
			return new CATGrowthSimulationTreeHacked(plot, statusClass, treeOverbarkVolumeDm3, numberOfTrees, originalSpeciesName, dbhCm);
		}
	}
	
	static class CATGrowthSimulationTreeHacked extends CATGrowthSimulationTreeWithDBH implements MEMSCompatibleTree {

		CATGrowthSimulationTreeHacked(CATGrowthSimulationPlot plot, 
				StatusClass statusClass, 
				double treeVolumeDm3,
				double numberOfTrees, 
				String originalSpeciesName,
				double dbhCm) {
			super(plot, statusClass, treeVolumeDm3, numberOfTrees, originalSpeciesName, dbhCm);
		}

		@Override
		public double getStemBasalAreaM2() {
			return Math.PI * getDbhCm() * getDbhCm() * 0.000025;
		}

		@Override
		public double getAnnualFoliarBiomassProductionMgYr() {
			return 0.15 * Math.pow(10, 1.18) * 0.001; // 10 cm2 of cross section growth is assumed for the test
		}
	}
	
	@Test
	public void testMEMSIntegration01() throws Exception {
		String filename = ObjectUtility.getPackagePath(CarbonAccountingToolTest.class) + "io" + File.separator + "MathildeTreeExport.csv";
		String ifeFilename = ObjectUtility.getPackagePath(getClass()) + "MathildeTreeExportWithDBH.ife";
//		String refFilename = ObjectUtility.getPackagePath(getClass()) + "io" + File.separator + "ExampleYieldTableReference.xml";
		CarbonAccountingTool cat = new CarbonAccountingTool(CATMode.SCRIPT);
		cat.initializeTool(null);
		CATGrowthSimulationRecordReaderHacked recordReader = new CATGrowthSimulationRecordReaderHacked();
		ImportFieldManager ifm = ImportFieldManager.createImportFieldManager(ifeFilename, filename);
		recordReader.initInScriptMode(ifm);
		recordReader.readAllRecords();
		cat.setStandList(recordReader.getStandList());
		cat.calculateCarbon();
		CATSimulationResult simResults = cat.retrieveSimulationSummary();
		Estimate<Matrix, SymmetricMatrix, ?> estimate = simResults.getEvolutionMap().get(CompartmentInfo.Soil);
		Matrix evolSoil = estimate.getMean();
		Assert.assertEquals("Testing nb of entries", 36, evolSoil.m_iRows);
		Assert.assertEquals("Testing second entry", 81.63271051514383, evolSoil.getValueAt(1, 0), 1E-8);
		Assert.assertEquals("Testing first last", 125.30530543551049, evolSoil.getValueAt(35, 0), 1E-8);

		estimate = simResults.getEvolutionMap().get(CompartmentInfo.Humus);
		evolSoil = estimate.getMean();
		Assert.assertEquals("Testing nb of entries", 36, evolSoil.m_iRows);
		Assert.assertEquals("Testing second entry", 27.202404594823964, evolSoil.getValueAt(1, 0), 1E-8);
		Assert.assertEquals("Testing first last", 54.53071761069785, evolSoil.getValueAt(35, 0), 1E-8);

		estimate = simResults.getEvolutionMap().get(CompartmentInfo.MineralSoil);
		evolSoil = estimate.getMean();
		Assert.assertEquals("Testing nb of entries", 36, evolSoil.m_iRows);
		Assert.assertEquals("Testing second entry", 54.430305920319874, evolSoil.getValueAt(1, 0), 1E-8);
		Assert.assertEquals("Testing first last", 70.77458782481267, evolSoil.getValueAt(35, 0), 1E-8);
		
		estimate = simResults.getBudgetMap().get(CompartmentInfo.Soil);
		evolSoil = estimate.getMean();
		Assert.assertEquals("Testing nb of entries", 1, evolSoil.m_iRows);
		Assert.assertEquals("Testing entry", 100.87408913646546, evolSoil.getValueAt(0, 0), 1E-8);

		estimate = simResults.getBudgetMap().get(CompartmentInfo.Humus);
		evolSoil = estimate.getMean();
		Assert.assertEquals("Testing nb of entries", 1, evolSoil.m_iRows);
		Assert.assertEquals("Testing entry", 40.217945821160825, evolSoil.getValueAt(0, 0), 1E-8);

		estimate = simResults.getBudgetMap().get(CompartmentInfo.MineralSoil);
		evolSoil = estimate.getMean();
		Assert.assertEquals("Testing nb of entries", 1, evolSoil.m_iRows);
		Assert.assertEquals("Testing entry", 60.65614331530463, evolSoil.getValueAt(0, 0), 1E-8);
		
	}
	
}
