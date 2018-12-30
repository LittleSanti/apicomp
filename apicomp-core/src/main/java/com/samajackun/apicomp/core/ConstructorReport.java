package com.samajackun.apicomp.core;

import java.lang.reflect.Constructor;

public class ConstructorReport extends ExecutableReport
{
	private final Constructor<?> constructor;

	public ConstructorReport(Constructor<?> constructor)
	{
		super();
		this.constructor=constructor;
	}

	public Constructor<?> getConstructor()
	{
		return this.constructor;
	}

}
