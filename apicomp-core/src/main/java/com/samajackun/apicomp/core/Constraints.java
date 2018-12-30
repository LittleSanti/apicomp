package com.samajackun.apicomp.core;

public class Constraints
{
	private boolean mySearchRepackagedClasses;

	public void setSearchRepackagedClasses(boolean mySearchRepackagedClasses)
	{
		this.mySearchRepackagedClasses=mySearchRepackagedClasses;
	}

	public boolean mustSearchRepackagedClasses()
	{
		return this.mySearchRepackagedClasses;
	}
}
