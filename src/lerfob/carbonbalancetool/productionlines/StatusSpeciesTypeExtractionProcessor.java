/*
 * This file is part of the CAT library.
 *
 * Copyright (C) 2023 His Majesty the King in Right of Canada
 * Author: Mathieu Fortin for Canadian Forest Service
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
package lerfob.carbonbalancetool.productionlines;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import repicea.gui.REpiceaPanel;
import repicea.gui.UIControlManager;
import repicea.simulation.covariateproviders.treelevel.SpeciesTypeProvider;
import repicea.simulation.covariateproviders.treelevel.SpeciesTypeProvider.SpeciesType;
import repicea.simulation.covariateproviders.treelevel.TreeStatusProvider;
import repicea.simulation.covariateproviders.treelevel.TreeStatusProvider.StatusClass;
import repicea.simulation.processsystem.ProcessUnit;
import repicea.util.REpiceaTranslator;
import repicea.util.REpiceaTranslator.TextableEnum;

/**
 * A special processor for extracting wood pieces from broadleaved species.
 * @author Mathieu Fortin - December 2023
 */
@SuppressWarnings({ "serial", "rawtypes" })
public class StatusSpeciesTypeExtractionProcessor extends AbstractExtractionProcessor {

	private enum MessageID implements TextableEnum {

		ExtractStatusSpeciesType("Sorting by species type and status ", "Tri par \u00E9tat et type d'esp\u00E8ces"), 
		None("No sorting", "Aucun tri");

		MessageID(String englishText, String frenchText) {
			setText(englishText, frenchText);
		}
		
		@Override
		public void setText(String englishText, String frenchText) {
			REpiceaTranslator.setString(this, englishText, frenchText);
		}

		@Override
		public String toString() {return REpiceaTranslator.getString(this);}
	}
	
	
	private static class StatusSpeciesTypeExtractionProcessorPanel extends REpiceaPanel implements ItemListener {

		private enum MessageID implements TextableEnum {

			ExtractionBasedOn("Sorting by:", "Tri par: "),
			SpeciesTypeLabel("Species type:", "Type d'esp\u00E8ces: "),
			StatusClassLavel("Tree status:", "Etat :"); 
			
			MessageID(String englishText, String frenchText) {
				setText(englishText, frenchText);
			}
			
			@Override
			public void setText(String englishText, String frenchText) {
				REpiceaTranslator.setString(this, englishText, frenchText);
			}

			@Override
			public String toString() {return REpiceaTranslator.getString(this);}
		}

		final StatusSpeciesTypeExtractionProcessor caller;
		
		
		final JComboBox<Enum> statusClassComboBox;
		final List<Enum> statusClassSelection;
		final JComboBox<Enum> speciesTypeComboBox;
		final List<Enum> speciesTypeSelection;
		
		
		protected StatusSpeciesTypeExtractionProcessorPanel(StatusSpeciesTypeExtractionProcessor caller) {
			super();
			this.caller = caller;
			statusClassSelection = new ArrayList<Enum>();
			statusClassSelection.add(StatusSpeciesTypeExtractionProcessor.MessageID.None);
			for (StatusClass sc : StatusClass.values()) {
				if (sc != StatusClass.alive) {
					statusClassSelection.add(sc);	
				}
			}
			statusClassComboBox = new JComboBox<Enum>(statusClassSelection.toArray(new Enum[] {}));
			
			speciesTypeSelection = new ArrayList<Enum>();
			speciesTypeSelection.add(StatusSpeciesTypeExtractionProcessor.MessageID.None);
			for (SpeciesType st : SpeciesType.values()) {
				speciesTypeSelection.add(st);	
			}
			speciesTypeComboBox = new JComboBox<Enum>(speciesTypeSelection.toArray(new Enum[] {}));
			speciesTypeComboBox.setSelectedItem(caller.getSpeciesType());
			statusClassComboBox.setSelectedItem(caller.getStatusClass());
			checkIfNoneShouldBeRemoved();
			createUI();
		}

		protected void createUI() {
			setLayout(new BorderLayout());
			setBorder(UIControlManager.getTitledBorder(MessageID.ExtractionBasedOn.toString()));
			JPanel setupPanel = new JPanel(new BorderLayout());
			add(setupPanel, BorderLayout.CENTER);
			JPanel mainPanel = new JPanel();
			setupPanel.add(mainPanel, BorderLayout.NORTH);
			mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
			
			mainPanel.add(Box.createVerticalStrut(5));
			mainPanel.add(UIControlManager.createSimpleHorizontalPanel(MessageID.SpeciesTypeLabel, speciesTypeComboBox, 5, true));
			mainPanel.add(Box.createVerticalStrut(5));
			mainPanel.add(UIControlManager.createSimpleHorizontalPanel(MessageID.StatusClassLavel, statusClassComboBox, 5, true));
			mainPanel.add(Box.createVerticalStrut(5));
		}


		private void checkIfNoneShouldBeRemoved() {
			if (caller.getSpeciesType() == StatusSpeciesTypeExtractionProcessor.MessageID.None) {
				statusClassSelection.remove(StatusSpeciesTypeExtractionProcessor.MessageID.None);
			} else {
				if (!statusClassSelection.contains(StatusSpeciesTypeExtractionProcessor.MessageID.None)) {
					statusClassSelection.add(0, StatusSpeciesTypeExtractionProcessor.MessageID.None);
				}
			}
			ComboBoxModel<Enum> statusClassModel = new DefaultComboBoxModel<Enum>(statusClassSelection.toArray(new Enum[] {}));
			statusClassComboBox.setModel(statusClassModel);
			statusClassComboBox.setSelectedItem(caller.getStatusClass());
			
			if (caller.getStatusClass() == StatusSpeciesTypeExtractionProcessor.MessageID.None) {
				speciesTypeSelection.remove(StatusSpeciesTypeExtractionProcessor.MessageID.None);
			} else {
				if (!speciesTypeSelection.contains(StatusSpeciesTypeExtractionProcessor.MessageID.None)) {
					speciesTypeSelection.add(0, StatusSpeciesTypeExtractionProcessor.MessageID.None);
				}
			}
			ComboBoxModel<Enum> speciesTypeModel = new DefaultComboBoxModel<Enum>(speciesTypeSelection.toArray(new Enum[] {}));
			speciesTypeComboBox.setModel(speciesTypeModel);
			speciesTypeComboBox.setSelectedItem(caller.getSpeciesType());
		}			
		
		@Override
		public void refreshInterface() {
			doNotListenToAnymore();
			checkIfNoneShouldBeRemoved();
			listenTo();
		}

		@Override
		public void listenTo() {
			speciesTypeComboBox.addItemListener(this);
			statusClassComboBox.addItemListener(this);
		}

		@Override
		public void doNotListenToAnymore() {
			speciesTypeComboBox.removeItemListener(this);
			statusClassComboBox.removeItemListener(this);
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			if (e.getStateChange() == ItemEvent.SELECTED) {
				if (e.getSource().equals(speciesTypeComboBox)) {
					caller.speciesType = (Enum) speciesTypeComboBox.getSelectedItem();
					refreshInterface();
				} else if (e.getSource().equals(statusClassComboBox)) {
					caller.statusClass = (Enum) statusClassComboBox.getSelectedItem();
					refreshInterface();
				}
			}
		}
	}
	
	
	private Enum statusClass;
	private Enum speciesType;
	
	protected StatusSpeciesTypeExtractionProcessor() {
		super();
		setName(MessageID.ExtractStatusSpeciesType.toString());
	}

	Enum getSpeciesType() {
		if (speciesType == null) {
			speciesType = SpeciesType.BroadleavedSpecies; //default value in case of deserialization of a former version
		}
		return speciesType;
	}
	
	Enum getStatusClass() {
		if (statusClass == null) {
			statusClass = MessageID.None;
		} 
		return statusClass;
	}

	@Override
	protected List<ProcessUnit> extract(List<ProcessUnit> processUnits) {
		List<ProcessUnit> extractedUnits = new ArrayList<ProcessUnit>();
		List<ProcessUnit> copyList = new ArrayList<ProcessUnit>();
		copyList.addAll(processUnits);
		for (ProcessUnit p : copyList) {
//			if (p instanceof SpeciesTypeProvider) {
//				SpeciesType speciesType = ((SpeciesTypeProvider) p).getSpeciesType();
//				if (speciesType == SpeciesType.BroadleavedSpecies) {
//					extractedUnits.add(p);
//					processUnits.remove(p);
//				}
//			}
			if (canProcessUnitBeExtracted(p)) {
				extractedUnits.add(p);
				processUnits.remove(p);
			}
		}
		return extractedUnits;
	}

	private boolean canProcessUnitBeExtracted(ProcessUnit pu) {
		boolean canBeExtracted = false;
		Enum targetedSpeciesType = getSpeciesType();
		if (targetedSpeciesType != MessageID.None) {
			if (pu instanceof SpeciesTypeProvider) {
				canBeExtracted = targetedSpeciesType == ((SpeciesTypeProvider) pu).getSpeciesType();
				if (!canBeExtracted) {
					return false;	// it does not matter whether there is a status class criterion or not the species type does not fit
				}
			}
		}
		Enum targetedStatusClass = getStatusClass();
		if (targetedStatusClass != MessageID.None) {
			if (pu instanceof TreeStatusProvider) {
				canBeExtracted = targetedStatusClass == ((TreeStatusProvider) pu).getStatusClass();
			}
		}
		return canBeExtracted;
	}
	
	
	@Override
	protected REpiceaPanel getProcessFeaturesPanel() {
		return new StatusSpeciesTypeExtractionProcessorPanel(this);
	}

	
}
