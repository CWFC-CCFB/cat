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
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.cedarsoftware.util.io.JsonReader;

import lerfob.carbonbalancetool.productionlines.AbstractProcessor;
import py4j.GatewayServer;
import repicea.simulation.processsystem.Processor;

/**
 * The AffiliereJSONReader class reads a JSON file from AFFILIERE and 
 * converts it into a flux configuration file for CAT.
 * @author Mathieu Fortin - October 2023
 */
public class AffiliereJSONImportReader {

	/**
	 * An interface to provide access to methods on Python's end.
	 */
	public interface SankeyProxy {
		
		/**
		 * Read the content of an excel file and convert it
		 * into Sankey objects on Python's end.<p>
		 * This method returns tuples in Python. The tuple is converted into string.
		 * @param filename the path to the Excel file.
		 * @return a String 
		 */
		public String readFromExcel(String filename);

		/**
		 * Write Sankey objects to an Excel file.
		 * @param filename the output filename
		 * @param mode either "a" (append) or "w" (write)
		 * @return null
		 */
		public String writeToExcel(String filename, String mode);
		
		public boolean clear();
		
		public String requestShutdown();

	}

	
	
	private static final List<String> NodeTypesToBeDiscarded = new ArrayList<String>();
	static {
		NodeTypesToBeDiscarded.add("echange");		// we do not want import / export processors MF2024-03-01
	}
	
	public enum AFFiliereUnit {
		VolumeM3("1000m3"), 
		DryBiomassMg("1000t");
	
		final String suffix;
		
		AFFiliereUnit(String suffix) {
			this.suffix = suffix;
		}
	}

	public enum AFFiliereStudy {
		AFFiliere("AF Fili\u00E8res"),
		Carbone4("Carbone 4"),
		BACCFIRE("BACCFIRE"),
		MFAB("MFAB");
		
		final String prefix;
		
		AFFiliereStudy(String prefix) {
			this.prefix = prefix;
		}
	}
	
	private static class FutureLink {
		final Processor fatherProcessor;
		final Processor childProcessor;
		final double value;
		final boolean isPercent;
		
		FutureLink(Processor fatherProcessor, Processor childProcessor, double value, boolean isPercent) {
			this.fatherProcessor = fatherProcessor;
			this.childProcessor = childProcessor;
			this.value = value;
			this.isPercent = isPercent;
		}
	}
		
	protected static int OFFSET = 150;
	
	protected final LinkedHashMap<?,?> mappedJSON;
	protected final Map<String, Processor> processors;
	protected final List<Processor> endProductProcessors;
	protected final List<Processor> ioProcessors;
	protected final AFFiliereStudy study;
	protected final AFFiliereUnit unit;
	protected final Map nodeTags;
	
	private static GatewayServer Server;
	
	
	private boolean matchesTags(Map<String, Object> tagMapFromNode) {
		for (String tag : tagMapFromNode.keySet()) {
			if (!nodeTags.containsKey(tag)) {
				return false; // Should this throw an Exception MF20241009
			} else {
				if (!tag.equals("Diagramme")) {
					Map<String, Object> innerTagMap = (Map) ((Map) nodeTags.get(tag));
					Object[] array  = (Object[]) tagMapFromNode.get(tag);
					if (tag.equals(AffiliereJSONFormat.NODE_TAGS_NODETYPE_PROPERTY) && Arrays.asList(array).contains("echange")) { // we remove Import/Export nodes
						return false;
					}
					if (!isThereAtLeastOneTagEnabled(array, innerTagMap)) {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	private boolean isTrue(Object o) {
		return o != null && o instanceof Boolean && (Boolean) o;
	}
	
	
	private boolean isThereAtLeastOneTagEnabled(Object[] tagArrayFromNode, Map<String, Object> innerTagMap) {
//		if (isTrue(innerTagMap.get(AffiliereJSONFormat.ACTIVATED))) {
			for (Object tagVal : tagArrayFromNode) {
				String tagValue = (String) tagVal;
				Map inner2TagMap = (Map) innerTagMap.get(AffiliereJSONFormat.NODE_TAGS_PROPERTY);
				if (inner2TagMap.containsKey(tagValue)) {
					if (isTrue(((Map) inner2TagMap.get(tagValue)).get(AffiliereJSONFormat.SELECTED))) {
						return true;
					}
				}
			}
			return false;  // activated but no selection
//		} else { // not activated
//			return true;
//		}
	}

	@SuppressWarnings("unchecked")
	private void screenNodeMap(LinkedHashMap<?,?> nodeMap) {
		for (Object o : nodeMap.values()) {
			LinkedHashMap<String, Object> oMap = (LinkedHashMap<String, Object>) o;
			LinkedHashMap<String, Object> tagMap = (LinkedHashMap<String, Object>) oMap.get(AffiliereJSONFormat.NODE_TAGS_PROPERTY);
			boolean isSelected = matchesTags(tagMap);
			if (isSelected) {
				String id = oMap.get(AffiliereJSONFormat.NODE_IDNODE_PROPERTY).toString();
				Processor p = AbstractProcessor.createProcessor(oMap);
				processors.put(id, p);
			}
		}
	}

	@SuppressWarnings("unchecked")
	private LinkedHashMap<String, LinkedHashMap<String, Object>> screenLinkMap(LinkedHashMap<?,?> linkMap) {
		LinkedHashMap<String , LinkedHashMap<String, Object>> screenedMap = new LinkedHashMap<String, LinkedHashMap<String, Object>>();
		for (Object k : linkMap.keySet()) {
			String key = (String) k;
			LinkedHashMap<String, Object> innerMap = (LinkedHashMap<String, Object>) linkMap.get(key);
			String idSource = (String) innerMap.get(AffiliereJSONFormat.LINK_IDSOURCE_PROPERTY);
			String idTarget = (String) innerMap.get(AffiliereJSONFormat.LINK_IDTARGET_PROPERTY);
			if (processors.containsKey(idSource) && processors.containsKey(idTarget)) {
				screenedMap.put(key, innerMap);
			}
		}
		return screenedMap;
	}
	
	
	/**
	 * Constructor.
	 * @param file the File instance to be read.
	 * @param study an AFFiliereStudy enum
	 * @param unit an AFFiliereUnit enum
	 * @throws FileNotFoundException if the file cannot be found.
	 */
	@SuppressWarnings("unchecked")
	public AffiliereJSONImportReader(File file, AFFiliereStudy study, AFFiliereUnit unit) throws FileNotFoundException {
		this.study = study == null ? AFFiliereStudy.AFFiliere : study;
		this.unit = unit == null ? AFFiliereUnit.DryBiomassMg : unit;
		mappedJSON = AffiliereJSONImportReader.getJSONRepresentationThroughoutOpenSankey(file);
		nodeTags = (LinkedHashMap<?,?>) mappedJSON.get(AffiliereJSONFormat.L1_NODETAGS_PROPERTY);
		((Map) nodeTags.get("Diagramme")).put("activated", false);	// disabled diagram selection
		processors = new HashMap<String, Processor>();
		endProductProcessors = new ArrayList<Processor>();
		ioProcessors = new ArrayList<Processor>();
		screenNodeMap((LinkedHashMap<?,?>) mappedJSON.get(AffiliereJSONFormat.L1_NODES_PROPERTY));
		LinkedHashMap<String, LinkedHashMap<String, Object>> screenedLinkJSONMap = screenLinkMap((LinkedHashMap<?,?>) mappedJSON.get(AffiliereJSONFormat.L1_LINKS_PROPERTY));

		
//		String studyUnitAttribute = this.study.prefix + " " + this.unit.suffix;

		Map<Processor, List<FutureLink>> linkMap = new HashMap<Processor, List<FutureLink>>();
		for (LinkedHashMap<String, Object> innerLinkMap : screenedLinkJSONMap.values()) {
			String idSource = (String) innerLinkMap.get(AffiliereJSONFormat.LINK_IDSOURCE_PROPERTY);
			Processor fatherProcessor = processors.get(idSource);
			String idTarget = (String) innerLinkMap.get(AffiliereJSONFormat.LINK_IDTARGET_PROPERTY);
			Processor childProcessor = processors.get(idTarget);
			LinkedHashMap<?, ?> valueMap = (LinkedHashMap<?,?>) innerLinkMap.get(AffiliereJSONFormat.LINK_VALUE_PROPERTY); 
			boolean isPercent = false;
			double value;
			if (valueMap.containsKey(AffiliereJSONFormat.LINK_VALUE_PERCENT_PROPERTY)) {
				value = ((Number) valueMap.get(AffiliereJSONFormat.LINK_VALUE_PERCENT_PROPERTY)).doubleValue();
				isPercent = true;
			} else {
				Object o = valueMap.get(AffiliereJSONFormat.LINK_VALUE_PROPERTY);
				if (o instanceof String && ((String) o).isEmpty()) {
					value = 0d;
				} else {
					value = ((Number) valueMap.get(AffiliereJSONFormat.LINK_VALUE_PROPERTY)).doubleValue(); 
				}
			}
			if (!linkMap.containsKey(fatherProcessor)) {
				linkMap.put(fatherProcessor, new ArrayList<FutureLink>());
			}
			linkMap.get(fatherProcessor).add(new FutureLink(fatherProcessor, childProcessor, value, isPercent));
		}
		
		
		
		for (List<FutureLink> futureLinks : linkMap.values()) {
			double sumValues = 0d;
			Boolean lastIsPercent = null;
			for (FutureLink fLink : futureLinks) {
				if (lastIsPercent == null) {
					lastIsPercent = fLink.isPercent;
				} else {
					if (!lastIsPercent.equals(fLink.isPercent)) { // check if the isPercent member is consistent across the links
						throw new InvalidParameterException("It seems the values of some links are mix of percentage and absolute values!");
					}
				}
				sumValues += fLink.value;
			}
			
			if (!lastIsPercent) {
				for (FutureLink fLink : futureLinks) {
					fLink.fatherProcessor.addSubProcessor(fLink.childProcessor);
					fLink.fatherProcessor.getSubProcessorIntakes().put(fLink.childProcessor, fLink.value / sumValues * 100);
				}
			}
		}
	}

	private static String getProperFilenameForPython(String originalFilename) {
		return originalFilename.startsWith("/") ? originalFilename.substring(1) : originalFilename;
	}

	
	private synchronized static LinkedHashMap<?,?> getJSONRepresentationThroughoutOpenSankey(File f) {
		if (Server == null) {
			GatewayServer.turnLoggingOff();
			Server = new GatewayServer();
			Server.start();
		}
		SankeyProxy sankeyProxy = (SankeyProxy) Server.getPythonServerEntryPoint(new Class[] { SankeyProxy.class });
		String path = getProperFilenameForPython(f.getAbsolutePath());
		long initialTime = System.currentTimeMillis();
		String message = sankeyProxy.readFromExcel(path);
		System.out.println("Reading file took " + (System.currentTimeMillis() - initialTime));
		LinkedHashMap<?,?> oMap = (LinkedHashMap<?,?>) JsonReader.jsonToMaps(message);
		sankeyProxy.clear();
		return oMap;
	}
	
//	static LinkedHashMap<?,?> readJSONMap(InputStream is) {
//		JsonReader reader = new JsonReader(is);
//		LinkedHashMap<?,?> mappedJSON = (LinkedHashMap<?,?>) reader.readObject();
//		reader.close();
//		return mappedJSON;
//	}
	
	private void addToListIfRelevant(Processor p, LinkedHashMap<String, Object> oMap, String key, String expression, List<Processor> outputList) {
		if (oMap.containsKey(key) ) {
			Object o = oMap.get(key);
			String str = ((Object[]) o)[0].toString();
			if (str.startsWith(expression)) {
				outputList.add(p);
			}
		}
	}
	
	private static boolean containsUndesiredNodeTypeTag(Object[] tags) {
		for (Object o : tags) {
			if (NodeTypesToBeDiscarded.contains(o.toString()))
				return true;
		}
		return false;
	}


	/**
	 * Provide a map of the processors read by this AffiliereJSONReader instance.<p>
	 * Map keys are the names of the processors whereas the values are the ProductionLineProcessors
	 * instances.
	 * @return a Map
	 */
	public Map<String, Processor> getProcessors() {
		return processors;
	}
}
