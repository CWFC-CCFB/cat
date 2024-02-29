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

import java.awt.Container;
import java.awt.Window;

import repicea.gui.permissions.REpiceaGUIPermission;
import repicea.simulation.processsystem.Processor;
import repicea.simulation.processsystem.ProcessorButton;
import repicea.simulation.processsystem.ProcessorInternalDialog;
import repicea.simulation.processsystem.SystemPanel;

/**
 * An extension of the original {@link repicea.simulation.processsystem.ProcessorButton} class for 
 * a better representation of CAT processors.
 */
@SuppressWarnings("serial")
public class AbstractProcessorButton extends ProcessorButton {

	/**
	 * Constructor.
	 * @param panel a SystemPanel instance
	 * @param process the Processor instance that owns this button
	 */
	protected AbstractProcessorButton(SystemPanel panel, Processor process) {
		super(panel, process);
	}
	
	/**
	 * Constructor.
	 * @param panel a SystemPanel instance
	 * @param process the Processor instance that owns this button
	 * @param permissions the REpiceaGUIPermission instance that enables or disables some controls
	 */
	protected AbstractProcessorButton(SystemPanel panel, Processor process, REpiceaGUIPermission permissions) {
		super(panel, process, permissions);
	}

	@Override
	public ProcessorInternalDialog getUI(Container parent) {
		if (guiInterface == null) {
			guiInterface = new EnhancedProcessorInternalDialog((Window) parent, this);
		}
		return guiInterface;
	}
	
	/*
	 * For extended visibility (non-Javadoc)
	 * @see repicea.simulation.processsystem.ProcessorButton#setChanged(boolean)
	 */
	@Override
	protected void setChanged(boolean hasChanged) {
		super.setChanged(hasChanged);
	}
	
}
