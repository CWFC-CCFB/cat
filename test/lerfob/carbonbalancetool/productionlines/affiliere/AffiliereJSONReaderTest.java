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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import lerfob.carbonbalancetool.productionlines.ProductionLineProcessor;
import lerfob.carbonbalancetool.productionlines.ProductionProcessorManager;
import repicea.serial.MemorizerPackage;
import repicea.util.ObjectUtility;

public class AffiliereJSONReaderTest {

	@Test
	public void testAffiliereReaderFromFile() throws FileNotFoundException {
		String filename = ObjectUtility.getPackagePath(AffiliereJSONReaderTest.class) + "EtudesAvecEtiquette4.json";
		AffiliereJSONReader reader = new AffiliereJSONReader(new File(filename));
		ProductionProcessorManager manager = new ProductionProcessorManager();
		for (ProductionLineProcessor p : reader.processors.values()) {
			manager.registerObject(p);
		}
//		manager.showUI(null);
		MemorizerPackage mp = manager.getMemorizerPackage();
		Assert.assertEquals("Testing nb of processors", 94, ((List) mp.get(1)).size());
	}
	
	@Ignore
	@Test
	public void testAffiliereReaderFromURL() throws IOException {
		URL url = new URL("https://open-sankey.fr/fm/userfiles/Fili%C3%A8res/ForetBois/EtudeCarbone4/sankey/Fili%C3%A8re%20bois%20-%20Exports%20Sankeys_v25_layout.json");
		AffiliereJSONReader reader = new AffiliereJSONReader(url);
		ProductionProcessorManager manager = new ProductionProcessorManager();
		for (ProductionLineProcessor p : reader.processors.values()) {
			manager.registerObject(p);
		}
//		manager.showUI(null);
		MemorizerPackage mp = manager.getMemorizerPackage();
		Assert.assertEquals("Testing nb of processors", 88, ((List) mp.get(1)).size());
	}
	
	
	public static void main(String[] args) throws FileNotFoundException {
		String filename = ObjectUtility.getPackagePath(AffiliereJSONReaderTest.class) + "EtudesAvecEtiquette4.json";
		AffiliereJSONReader reader = new AffiliereJSONReader(new File(filename));
		ProductionProcessorManager manager = new ProductionProcessorManager();
		for (ProductionLineProcessor p : reader.processors.values()) {
			manager.registerObject(p);
		}
		manager.showUI(null);
		
	}
	
}
