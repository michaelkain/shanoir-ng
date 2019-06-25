/**
 * Shanoir NG - Import, manage and share neuroimaging data
 * Copyright (C) 2009-2019 Inria - https://www.inria.fr/
 * Contact us on https://project.inria.fr/shanoir/
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see https://www.gnu.org/licenses/gpl-3.0.html
 */

package org.shanoir.ng.dataset.modality;

/**
 * Spectroscopy dataset nature.
 * 
 * @author msimon
 *
 */
public enum SpectDatasetNature {

	// Nuclear medicine tomo dataset
	NUCLEAR_MEDICINE_TOMO_DATASET(1),

	// Nuclear medicine projection dataset
	NUCLEAR_MEDICINE_PROJECTION_DATASET(2);

	private int id;

	/**
	 * Constructor.
	 * 
	 * @param id
	 *            id
	 */
	private SpectDatasetNature(final int id) {
		this.id = id;
	}

	/**
	 * Get a spectroscopy dataset nature by its id.
	 * 
	 * @param id
	 *            nature id.
	 * @return spectroscopy dataset nature.
	 */
	public static SpectDatasetNature getNature(final Integer id) {
		if (id == null) {
			return null;
		}
		for (SpectDatasetNature nature : SpectDatasetNature.values()) {
			if (id.equals(nature.getId())) {
				return nature;
			}
		}
		throw new IllegalArgumentException("No matching spectroscopy dataset nature for id " + id);
	}

	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

}
