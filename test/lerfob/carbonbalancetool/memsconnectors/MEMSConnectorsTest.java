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
import lerfob.carbonbalancetool.io.CATGrowthSimulationCompositeStand;
import lerfob.carbonbalancetool.io.CATGrowthSimulationPlot;
import lerfob.carbonbalancetool.io.CATGrowthSimulationRecordReader;
import lerfob.carbonbalancetool.io.CATGrowthSimulationTreeWithDBH;
import lerfob.carbonbalancetool.memsconnectors.MEMSSite.SiteType;
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

		@Override
		protected CATGrowthSimulationCompositeStand createCompositeStand(String standIdentification, int dateYr, boolean scaleDependentInterventionResult) {
			return new CATGrowthSimulationCompositeStandHacked(dateYr, standIdentification, this, scaleDependentInterventionResult);
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
		public double getAnnualFoliarDetritusCarbonProductionMgYr() {
			return 0.15 * Math.pow(10, 1.18) * 0.001; // 10 cm2 of cross section growth is assumed for the test
		}

		/**
		 * This implementation is based on Finer et al. (2011).
		 * @see <a href=https://doi.org/10.1016/j.foreco.2011.08.042> Finer, L., M. Ohashi, K. Noguchi, and 
		 * Y. Hirano. 2011. Fine root production and turnover in forest ecosystems in relation to stand and 
		 * environmental characteristics. Forest Ecology and Management 262(11): 2008-2023</a>
		 */
		@Override
		public double getAnnualFineRootDetritusCarbonProductionMgYr() {
			return (1.55 * Math.log(getStemBasalAreaM2()) + 9.408) * .001;
		}

		@Override
		public double getAnnualBranchDetritusCarbonProductionMgYr() {
			return getAnnualFoliarDetritusCarbonProductionMgYr() * .5;
		}

		@Override
		public double getFoliarBiomassMg() {
			// Useless in this context
			return 0;
		}
	}

	static class CATGrowthSimulationCompositeStandHacked extends CATGrowthSimulationCompositeStand implements MEMSCompatibleStand {

		CATGrowthSimulationCompositeStandHacked(int dateYr, String standIdentification, CATGrowthSimulationRecordReader reader, boolean isInterventionResult) {
			super(dateYr, standIdentification, reader, isInterventionResult);
		}

		@Override
		public SiteType getSiteType() {return SiteType.Montmorency2;}

		@Override
		public double getMeanAnnualTemperatureCForThisYear(int year) {
			return 3.8; // between Jan 1 2013 to Dec 31st 2016 at Foret Montmorency 
		}

		@Override
		public double getAnnualTemperatureRangeForThisYear(int year) {
			double minTemp = -9.48;   // between Jan 1 2013 to Dec 31st 2016 at Foret Montmorency
			double maxTemp = 17.79;   // between Jan 1 2013 to Dec 31st 2016 at Foret Montmorency
			return maxTemp - minTemp;
		}

	}

	@Test
	public void testMEMSIntegration01() throws Exception {
		CATGrowthSimulationRecordReader.TestUnevenAgedInfiniteSequence = true;	// this way we get the application scale set to stand
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
		Assert.assertEquals("Testing first last", 121.54493372864661, evolSoil.getValueAt(35, 0), 1E-8);

		estimate = simResults.getEvolutionMap().get(CompartmentInfo.Humus);
		evolSoil = estimate.getMean();
		Assert.assertEquals("Testing nb of entries", 36, evolSoil.m_iRows);
		Assert.assertEquals("Testing second entry", 27.202404594823964, evolSoil.getValueAt(1, 0), 1E-8);
		Assert.assertEquals("Testing first last", 52.481212174299145, evolSoil.getValueAt(35, 0), 1E-8);

		estimate = simResults.getEvolutionMap().get(CompartmentInfo.MineralSoil);
		evolSoil = estimate.getMean();
		Assert.assertEquals("Testing nb of entries", 36, evolSoil.m_iRows);
		Assert.assertEquals("Testing second entry", 54.430305920319874, evolSoil.getValueAt(1, 0), 1E-8);
		Assert.assertEquals("Testing first last", 69.06372155434751, evolSoil.getValueAt(35, 0), 1E-8);
		
		estimate = simResults.getBudgetMap().get(CompartmentInfo.Soil);
		evolSoil = estimate.getMean();
		Assert.assertEquals("Testing nb of entries", 1, evolSoil.m_iRows);
		Assert.assertEquals("Testing entry", 97.9033534714863, evolSoil.getValueAt(0, 0), 1E-8);

		estimate = simResults.getBudgetMap().get(CompartmentInfo.Humus);
		evolSoil = estimate.getMean();
		Assert.assertEquals("Testing nb of entries", 1, evolSoil.m_iRows);
		Assert.assertEquals("Testing entry", 38.33797503396882, evolSoil.getValueAt(0, 0), 1E-8);

		estimate = simResults.getBudgetMap().get(CompartmentInfo.MineralSoil);
		evolSoil = estimate.getMean();
		Assert.assertEquals("Testing nb of entries", 1, evolSoil.m_iRows);
		Assert.assertEquals("Testing entry", 59.56537843751747, evolSoil.getValueAt(0, 0), 1E-8);
		CATGrowthSimulationRecordReader.TestUnevenAgedInfiniteSequence = false;	// set the static variable to its original value
	}
	
}
