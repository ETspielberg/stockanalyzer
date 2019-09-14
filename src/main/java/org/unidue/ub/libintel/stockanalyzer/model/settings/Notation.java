package org.unidue.ub.libintel.stockanalyzer.model.settings;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Plain old java object holding a notation and the corresponding description.
 * 
 * @author Eike Spielberg
 * @version 1
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Notation {

	private String notation;

	private String description;

	/**
	 * returns the description
	 * 
	 * @return description the description of the notation
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * sets the description
	 * 
	 * @param description the description of the notation
	 * @return Notation the updated <code>Notation</code>-object
	 */
	public Notation setDescription(String description) {
		this.description = description;
		return this;
	}

	/**
	 * returns the notation
	 * 
	 * @return description the notation
	 */
	public String getNotation() {
		return notation;
	}

	/**
	 * sets the notation
	 * 
	 * @param notation the notation
	 * @return Notation the updated <code>Notation</code>-object
	 */
	public Notation setNotation(String notation) {
		this.notation = notation;
		return this;
	}
	

}
