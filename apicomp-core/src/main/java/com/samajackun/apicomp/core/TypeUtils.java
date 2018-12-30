package com.samajackun.apicomp.core;

import static com.samajackun.apicomp.core.MemberReport.MemberChangeType.WAS_INTERFACE;
import static com.samajackun.apicomp.core.MemberReport.MemberChangeType.WAS_NOT_ABSTRACT;
import static com.samajackun.apicomp.core.MemberReport.MemberChangeType.WAS_NOT_FINAL;
import static com.samajackun.apicomp.core.MemberReport.MemberChangeType.WAS_NOT_INTERFACE;
import static com.samajackun.apicomp.core.MemberReport.MemberChangeType.WAS_PACKAGE;
import static com.samajackun.apicomp.core.MemberReport.MemberChangeType.WAS_PROTECTED;
import static com.samajackun.apicomp.core.MemberReport.MemberChangeType.WAS_PUBLIC;
import static com.samajackun.apicomp.core.MemberReport.MemberChangeType.WAS_STATIC;

import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.samajackun.apicomp.core.Report.CompatibilityLevel;

final class TypeUtils
{
	private TypeUtils()
	{
	}

	public static void compareParameters(Class<?>[] baseParameterTypes, Class<?>[] comparedParameterTypes, ExecutableReport report)
	{
		for (int i=0; i < baseParameterTypes.length; i++)
		{
			Class<?> baseParameter=baseParameterTypes[i];
			Class<?> comparedParameter=comparedParameterTypes[i];
			CompatibilityLevel level=compareTypes(comparedParameter, baseParameter);
			if (level != CompatibilityLevel.BINARY_COMPATIBLE)
			{
				report.addChangedParameter(i, level, comparedParameter);
			}
		}

	}

	public static CompatibilityLevel compareTypes(Class<?> superType, Class<?> subType)
	{
		CompatibilityLevel level;
		if (superType.equals(subType))
		{
			level=CompatibilityLevel.BINARY_COMPATIBLE;
		}
		else
		{
			if (superType.isAssignableFrom(subType))
			{
				level=CompatibilityLevel.SOURCE_COMPATIBLE;
			}
			else
			{
				level=CompatibilityLevel.INCOMPATIBLE;
			}
		}
		return level;
	}

	public static void compareExceptions(Class<?>[] baseExceptionsArray, Class<?>[] comparedExceptionsArray, ExecutableReport report)
	{
		Set<Class<?>> baseExceptions=Stream.of(baseExceptionsArray).filter(t -> !RuntimeException.class.isAssignableFrom(t)).collect(Collectors.toSet());
		Set<Class<?>> comparedExceptions=Stream.of(comparedExceptionsArray).filter(t -> !RuntimeException.class.isAssignableFrom(t)).collect(Collectors.toSet());
		Set<Class<?>> remainingComparedExceptions=new HashSet<>(comparedExceptions);
		for (Class<?> baseException : baseExceptions)
		{
			compareException(baseException, comparedExceptions, report);
			remainingComparedExceptions.remove(baseException);
		}
		for (Class<?> remainingException : remainingComparedExceptions)
		{
			report.addNewException(remainingException);
		}
	}

	public static void compareException(Class<?> baseException, Set<Class<?>> comparedExceptions, ExecutableReport report)
	{
		if (comparedExceptions.contains(baseException))
		{
			// OK. Compatibilidad binaria.
		}
		else
		{
			// Ha desaparecido una excepción:
			// Comprobar si alguna de las excepciones nuevas es superclase de la desaparecida:
			Optional<Class<?>> mySuper=comparedExceptions.stream().filter(t -> t.isAssignableFrom(baseException)).findFirst();
			if (mySuper.isPresent())
			{
				report.addSuperException(baseException, mySuper);
			}
			else
			{
				report.addDroppedException(baseException);
			}
		}
	}

	public static void compareModifiers(int baseModifiers, int comparedModifiers, MemberReport report)
	{
		if (!Modifier.isAbstract(baseModifiers))
		{
			if (Modifier.isAbstract(comparedModifiers))
			{
				report.addMemberChange(WAS_NOT_ABSTRACT);
			}
		}
		if (!Modifier.isFinal(baseModifiers))
		{
			if (Modifier.isFinal(comparedModifiers))
			{
				report.addMemberChange(WAS_NOT_FINAL);
			}
		}
		if (Modifier.isStatic(baseModifiers))
		{
			if (!Modifier.isStatic(comparedModifiers))
			{
				report.addMemberChange(WAS_STATIC);
			}
		}
		if (Modifier.isInterface(baseModifiers))
		{
			if (!Modifier.isInterface(comparedModifiers))
			{
				report.addMemberChange(WAS_INTERFACE);
			}
		}
		else
		{
			if (Modifier.isInterface(comparedModifiers))
			{
				report.addMemberChange(WAS_NOT_INTERFACE);
			}
		}
		if (Modifier.isPublic(baseModifiers))
		{
			if (!Modifier.isPublic(comparedModifiers))
			{
				report.addMemberChange(WAS_PUBLIC);
			}
		}
		else if (Modifier.isProtected(baseModifiers))
		{
			if (!Modifier.isPublic(comparedModifiers))
			{
				report.addMemberChange(WAS_PROTECTED);
			}
		}
		else if (Modifier.isPrivate(baseModifiers))
		{
		}
		else // El base era Package:
		{
			if (Modifier.isPrivate(comparedModifiers))
			{
				report.addMemberChange(WAS_PACKAGE);
			}
		}
	}
}
