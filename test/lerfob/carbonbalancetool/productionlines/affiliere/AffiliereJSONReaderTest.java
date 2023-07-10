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

import java.io.FileNotFoundException;

import lerfob.carbonbalancetool.productionlines.ProductionLineProcessor;
import lerfob.carbonbalancetool.productionlines.ProductionProcessorManager;
import repicea.util.ObjectUtility;

public class AffiliereJSONReaderTest {

	public static void main(String[] args) throws FileNotFoundException {
		String filename = ObjectUtility.getPackagePath(AffiliereJSONReaderTest.class) + "Filière bois - Exports Sankeys_v23_layout - Copie.json";
		AffiliereJSONReader reader = new AffiliereJSONReader(filename);
		ProductionProcessorManager manager = new ProductionProcessorManager();
		for (ProductionLineProcessor p : reader.processors.values()) {
			manager.registerObject(p);
		}
		manager.showUI(null);
	}
	
}
