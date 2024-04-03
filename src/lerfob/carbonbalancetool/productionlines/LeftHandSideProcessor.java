/*
 * This file is part of the CAT library.
 *
 * Copyright (C) 2010-2015 Mathieu Fortin for LERFOB AgroParisTech/INRA, 
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
package lerfob.carbonbalancetool.productionlines;

import java.awt.Color;
import java.awt.Graphics;

import repicea.gui.permissions.REpiceaGUIPermission;
import repicea.simulation.processsystem.Processor;
import repicea.simulation.processsystem.SystemPanel;

/**
 * Processors of this class are the entry points of the flux configuration.<p>
 * The dead wood and the harvested trees are coming into the flux configuration through these processors.
 * @author Mathieu Fortin - 2015 
 */
@SuppressWarnings("serial")
public abstract class LeftHandSideProcessor extends AbstractProcessor {

	
	protected static class CustomizedREpiceaGUIPermission implements REpiceaGUIPermission {

		@Override
		public boolean isDragGranted() { // but can link them to other processors
			return true;
		}

		@Override
		public boolean isDropGranted() {  // cannot be drag and drop
			return false;
		}

		@Override
		public boolean isSelectionGranted() { // cannot be selected
			return false;
		}

		/**
		 * Enabling is mainly set in the repicea.simulation.processsystem.SystemComponentMouseAdapter class.<p>
		 * See the ComponentsToBeEnabledDisabled static member of that class for the list of components.
		 */
		@Override
		public boolean isEnablingGranted() { //cannot be modified
			return false;
		}
		
	}
	
	
	
	public static class LeftHandSideProcessorButton extends AbstractProcessorButton {
				
		protected LeftHandSideProcessorButton(SystemPanel panel, Processor process) {
			super(panel, process, new CustomizedREpiceaGUIPermission());
		}

		@SuppressWarnings("rawtypes")
		protected void setDragMode(Enum mode) {
			super.setDragMode(mode);
			buttonMoveRecognizer.setEnabled(false);		// no matter the selection, this button does not move
		}

		@Override
		public void setSelected(boolean bool) {}

		@Override
		public void paint(Graphics g) {
			if (!getOwner().hasSubProcessors()) {
				setBorderColor(Color.RED);
				setBorderWidth(2);
			} else {
				setBorderColor(Color.BLACK);
				setBorderWidth(1);
			}
			super.paint(g);
		}
	}
	
	
	protected LeftHandSideProcessor() {}
	
}
