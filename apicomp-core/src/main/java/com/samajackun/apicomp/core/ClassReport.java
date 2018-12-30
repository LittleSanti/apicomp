package com.samajackun.apicomp.core;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ClassReport extends MemberReport
{
	public enum LocationChangeType {
		REPACKAGED_CLASS, MISSING_CLASS
	}

	public static enum NatureChangeType {
		WAS_CLASS, WAS_INTERFACE
	};

	private final Class<?> myClass;

	private LocationChangeType locationChange;

	private NatureChangeType natureChange;

	public ClassReport(Class<?> myClass)
	{
		super();
		this.myClass=myClass;
	}

	public void setLocationChange(LocationChangeType locationChange)
	{
		this.locationChange=locationChange;
	}

	public LocationChangeType getLocationChange()
	{
		return this.locationChange;
	}

	public FieldReport createFieldReport(Field baseField)
	{
		return new FieldReport(baseField);
	};

	public MethodReport createMethodReport(Method baseMethod)
	{
		return new MethodReport(baseMethod);
	};

	public ConstructorReport createConstructorReport(Constructor<?> baseConstructor)
	{
		return new ConstructorReport(baseConstructor);
	};

	public ClassReport createClassReport(Class<?> baseClass)
	{
		return new ClassReport(baseClass);
	}

	public Class<?> getMyClass()
	{
		return this.myClass;
	}

	public NatureChangeType getNatureChange()
	{
		return this.natureChange;
	}

	public void setNatureChange(NatureChangeType natureChange)
	{
		this.natureChange=natureChange;
	};
}
