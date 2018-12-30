package com.samajackun.apicomp.core;

import java.lang.reflect.Method;

import com.samajackun.apicomp.core.Report.CompatibilityLevel;

public class MethodReport extends ExecutableReport
{
	private final Method baseMethod;

	public MethodReport(Method baseMethod)
	{
		super();
		this.baseMethod=baseMethod;
	}

	public void setReturnChange(CompatibilityLevel returnLevel, Class<?> comparedReturn)
	{
		// TODO Auto-generated method stub

	}

	public Method getBaseMethod()
	{
		return this.baseMethod;
	}

}
