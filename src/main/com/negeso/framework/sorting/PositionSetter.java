package com.negeso.framework.sorting;

public interface PositionSetter {
	/**
	 * Assigns desired_position to the row with rowId
	 * and returns error (or empty string), must be declared as follows:
	 * @param recordId id of the record to be assigned new positioning value
	 * @param newPosition position desired
	 * @return true (success) or false
	 */
	boolean setPosition(int recordId, int newPosition);
}
