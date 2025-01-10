/*
 * This file is part of the CAT library.
 *
 * Copyright (C) 2023 His Majesty the King in Right of Canada
 * Author: Mathieu Fortin, Canadian Forest Service, Canadian Wood Fibre Centre
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
package lerfob.carbonbalancetool.productionlines.affiliere;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import lerfob.carbonbalancetool.productionlines.ProductionProcessorManager;
import lerfob.carbonbalancetool.productionlines.ProductionProcessorManager.ExportFormat;
import lerfob.carbonbalancetool.productionlines.ProductionProcessorManager.ImportFormat;
import repicea.serial.MemorizerPackage;
import repicea.util.ObjectUtility;

public class AffiliereJSONImportExportTest {

	@SuppressWarnings("rawtypes")
	@Ignore
	@Test
	public void testAffiliereReaderFromFile() throws IOException {
		String filename = ObjectUtility.getPackagePath(AffiliereJSONImportExportTest.class) + "BACCFIRE V7.2_desagrege_layout.json";
		ProductionProcessorManager manager = new ProductionProcessorManager();
		manager.importFrom(filename, ImportFormat.AFFILIERE);
//		manager.showUI(null);
		MemorizerPackage mp = manager.getMemorizerPackage();
		Assert.assertEquals("Testing nb of processors", 108, ((List) mp.get(1)).size());
	}

	@Ignore
	@Test
	public void testAffiliereExportToFile() throws IOException {
		String filename = ObjectUtility.getPackagePath(AffiliereJSONImportExportTest.class) + "hardwood_recycling_en.prl";
		ProductionProcessorManager manager = new ProductionProcessorManager();
		manager.load(filename);
//		manager.showUI(null);
		String exportFilename = ObjectUtility.getPackagePath(AffiliereJSONImportExportTest.class) + "CATTestExportToAffiliere.json"; 
		File f = new File(exportFilename);
		if (f.exists()) {
			f.delete();
		}
		manager.exportTo(exportFilename, ExportFormat.AFFILIERE);
		Assert.assertTrue("Testing if file exists", f.exists());
	}
	
	
//	@SuppressWarnings("rawtypes")
//	@Ignore
//	@Test
//	public void testAffiliereReaderFromURL() throws IOException {
//		URL url = new URL("https://open-sankey.fr/fm/userfiles/Fili%C3%A8res/ForetBois/EtudeCarbone4/sankey/Fili%C3%A8re%20bois%20-%20Exports%20Sankeys_v25_layout.json");
//		AffiliereJSONImportReader reader = new AffiliereJSONImportReader(url);
//		ProductionProcessorManager manager = new ProductionProcessorManager();
//		for (Processor p : reader.processors.values()) {
//			manager.registerObject(p);
//		}
////		manager.showUI(null);
//		MemorizerPackage mp = manager.getMemorizerPackage();
//		Assert.assertEquals("Testing nb of processors", 88, ((List) mp.get(1)).size());
//	}
	
	
	public static void main(String[] args) throws IOException {
		String filename = ObjectUtility.getPackagePath(AffiliereJSONImportExportTest.class) + "EtudesAvecEtiquette4.json";
		ProductionProcessorManager manager = new ProductionProcessorManager();
		manager.importFrom(filename, ImportFormat.AFFILIERE);
		manager.showUI(null);
		
	}
	
}
