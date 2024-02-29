/*
 * This file is part of the CAT library.
 *
 * Copyright (C) 2024 His Majesty the King in right of Canada
 * Author: Mathieu Fortin, Canadian Forest Service 
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
package repicea.simulation.processsystem;

/**
 * Ensure a UI can be release from the owning instance.<p>
 * For instance, the Processor instance can release the ProcessorInternalDialog instance. The interface
 * is mainly used to ensure an outdated dialog is release so that a new one will be produced.
 * @author Mathieu Fortin - February 2024
 */
public interface ResourceReleasable {
	
	/**
	 * Release the UI associated to a particular instance so that a new one will be produced.
	 */
	public void releaseResources();

}
