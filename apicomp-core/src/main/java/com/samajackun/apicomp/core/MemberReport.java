package com.samajackun.apicomp.core;

import java.util.HashSet;
import java.util.Set;

public class MemberReport
{
	public enum MemberChangeType {
		WAS_NOT_ABSTRACT, WAS_NOT_FINAL, WAS_STATIC, WAS_INTERFACE, WAS_NOT_INTERFACE, WAS_PUBLIC, WAS_PROTECTED, WAS_PACKAGE
	};

	private final Set<MemberChangeType> changes=new HashSet<>();

	public void addMemberChange(MemberChangeType change)
	{
		this.changes.add(change);
	}

	public Set<MemberChangeType> getMemberChanges()
	{
		return this.changes;
	}
}
