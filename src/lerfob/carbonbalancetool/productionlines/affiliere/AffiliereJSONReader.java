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
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.cedarsoftware.util.io.JsonObject;
import com.cedarsoftware.util.io.JsonReader;

import lerfob.carbonbalancetool.productionlines.ProductionLineProcessor;

/**
 * The AffiliereJSONReader class reads a JSON file from AFFILIERE and 
 * converts it into a flux configuration file for CAT.
 * @author Mathieu Fortin - October 2023
 */
public class AffiliereJSONReader {

	private static class FutureLink {
		final ProductionLineProcessor fatherProcessor;
		final ProductionLineProcessor childProcessor;
		final double value;
		
		FutureLink(ProductionLineProcessor fatherProcessor, ProductionLineProcessor childProcessor, double value) {
			this.fatherProcessor = fatherProcessor;
			this.childProcessor = childProcessor;
			this.value = value;
		}
	}
		
	protected static int OFFSET = 150;
	
	protected final JsonObject<?,?> mappedJSON;
	protected final Map<String, ProductionLineProcessor> processors;
	protected final List<ProductionLineProcessor> endProductProcessors;
	
	/**
	 * Constructor.
	 * @param file the File instance to be read.
	 * @throws FileNotFoundException if the file cannot be found.
	 */
	public AffiliereJSONReader(File file) throws FileNotFoundException {
		this(new FileInputStream(file));
	}

	/**
	 * Constructor.
	 * @param url the url of the file to be read.
	 * @throws IOException if the url cannot produce an input stream.
	 */
	public AffiliereJSONReader(URL url) throws IOException {
		this(url.openStream());
	}
	
	@SuppressWarnings("unchecked")
	private AffiliereJSONReader(InputStream is) {
		JsonReader reader = new JsonReader(is);
		mappedJSON = (JsonObject<?,?>) reader.readObject();
		reader.close();
		JsonObject<?,?> processorJSONMap = (JsonObject<?,?>) mappedJSON.get("nodes");
		JsonObject<?,?> linkJSONMap = (JsonObject<?,?>) mappedJSON.get("links");
		processors = new HashMap<String, ProductionLineProcessor>();
		endProductProcessors = new ArrayList<ProductionLineProcessor>();
		for (Object o : processorJSONMap.values()) {
			JsonObject<String,?> oMap = (JsonObject<String,?>) o;
			String id = (String) oMap.get("idNode");
			String name = (String) oMap.get("name");
			int x = ((Number) ((JsonObject<?,?>) o).get("x")).intValue() + OFFSET;
			int y = ((Number) ((JsonObject<?,?>) o).get("y")).intValue();
			ProductionLineProcessor p = ProductionLineProcessor.createProductionLineProcessor(name, x, y);
			processors.put(id, p);
		}

		Map<ProductionLineProcessor, List<FutureLink>> linkMap = new HashMap<ProductionLineProcessor, List<FutureLink>>();
		
		for (String fatherProcessorName : processors.keySet()) {
			for (Object linkValue : linkJSONMap.values()) {
				JsonObject<?,?> linkProperties = (JsonObject<?,?>)  linkValue;
				if (linkProperties.containsKey("idSource") && linkProperties.get("idSource").equals(fatherProcessorName)) {
					ProductionLineProcessor fatherProcessor = processors.get(fatherProcessorName);
					if (linkProperties.containsKey("idTarget")) {
						String childProcessorName = (String) linkProperties.get("idTarget");
						ProductionLineProcessor childProcessor = processors.get(childProcessorName);
						if (childProcessor != null) {
							double value = ((Number) ((JsonObject<?,?>) linkProperties.get("value")).get("value")).doubleValue();
							if (!linkMap.containsKey(fatherProcessor)) {
								linkMap.put(fatherProcessor, new ArrayList<FutureLink>());
							}
							linkMap.get(fatherProcessor).add(new FutureLink(fatherProcessor, childProcessor, value));
						}
					}
				}
			}
		}
		
		for (List<FutureLink> futureLinks : linkMap.values()) {
			double sumValues = 0d;
			for (FutureLink fLink : futureLinks) {
				sumValues += fLink.value;
			}
			
			for (FutureLink fLink : futureLinks) {
				fLink.fatherProcessor.addSubProcessor(fLink.childProcessor);
				fLink.fatherProcessor.getSubProcessorIntakes().put(fLink.childProcessor, fLink.value / sumValues * 100);
			}
		}
	}

	/**
	 * Provide a map of the processors read by this AffiliereJSONReader instance.<p>
	 * Map keys are the names of the processors whereas the values are the ProductionLineProcessors
	 * instances.
	 * @return a Map
	 */
	public Map<String, ProductionLineProcessor> getProcessors() {
		return processors;
	}
}
