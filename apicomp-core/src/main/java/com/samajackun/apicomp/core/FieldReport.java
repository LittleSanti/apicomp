package com.samajackun.apicomp.core;

import java.lang.reflect.Field;

import com.samajackun.apicomp.core.Report.CompatibilityLevel;

public class FieldReport extends MemberReport
{
	public enum ValueChangeType {
		WAS_CONSTANT, CHANGED_VALUE, WAS_NOT_CONSTANT
	};

	private final Field field;

	private ValueChangeType valueChange;

	public FieldReport(Field field)
	{
		super();
		this.field=field;
	}

	public Field getField()
	{
		return this.field;
	}

	public void setTypeChange(CompatibilityLevel level, Class<?> comparedType)
	{

	}

	public ValueChangeType getValueChange()
	{
		return this.valueChange;
	}

	public void setValueChange(ValueChangeType valueChange)
	{
		this.valueChange=valueChange;
	}
}
