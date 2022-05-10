package com.indulgetech.validators.date;


import com.indulgetech.utils.CustomDateUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomDateValidator implements
		ConstraintValidator<CustomDate, String> {

	private String format;
	private boolean required;

	@Override
	public void initialize(CustomDate customDate) {
		format = customDate.format();
		required = customDate.required();
	}

	@Override
	public boolean isValid(String date, ConstraintValidatorContext context) {

		if (date == null || date.isEmpty()) {
			return !required;
		}

		return CustomDateUtils.isValidFormat(format,date);

	}


}
