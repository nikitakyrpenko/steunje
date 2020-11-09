package com.negeso.framework.util;

import com.negeso.framework.exception.ValidationException;

public interface Validator {
	void validate() throws ValidationException;
}
