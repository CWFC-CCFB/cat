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
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;

import com.cedarsoftware.util.io.JsonWriter;

/**
 * The AffiliereJSONReader class produces a JSON file from a ProductionProcessorManager instance.
 * @author Mathieu Fortin - November 2023
 */
public class AffiliereJSONExportWriter {

	/**
	 * Write a JSON representation of a ProductionProcessorManager instance to file.
	 * @param manager a ProductionProcessorManager instance
	 * @param filename the filename
	 * @throws IOException if an error occurs while writing the file
	 */
	public AffiliereJSONExportWriter(LinkedHashMap<String, Object> managerRep, String filename) throws IOException {
		JsonWriter writer = null;
		try {
			FileOutputStream fos = new FileOutputStream(new File(filename));
			writer = new JsonWriter(fos);
			writer.write(managerRep);	
		} finally {
			if (writer != null) {
				writer.close();
			}
		}
	}
	
}
