package com.samajackun.apicomp.core;

import static com.samajackun.apicomp.core.ClassReport.LocationChangeType.MISSING_CLASS;
import static com.samajackun.apicomp.core.ClassReport.LocationChangeType.REPACKAGED_CLASS;
import static com.samajackun.apicomp.core.FieldReport.ValueChangeType.CHANGED_VALUE;
import static com.samajackun.apicomp.core.FieldReport.ValueChangeType.WAS_CONSTANT;
import static com.samajackun.apicomp.core.FieldReport.ValueChangeType.WAS_NOT_CONSTANT;
import static com.samajackun.apicomp.core.TypeUtils.compareExceptions;
import static com.samajackun.apicomp.core.TypeUtils.compareModifiers;
import static com.samajackun.apicomp.core.TypeUtils.compareParameters;
import static com.samajackun.apicomp.core.TypeUtils.compareTypes;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import com.samajackun.apicomp.core.Report.CompatibilityLevel;

public class Checker
{
	private final ClassLoader baseLoader;

	private final ClassLoader comparedLoader;

	private final Constraints constraints;

	public Checker(ClassLoader baseLoader, ClassLoader comparedLoader, Constraints constraints)
	{
		super();
		this.baseLoader=baseLoader;
		this.comparedLoader=comparedLoader;
		this.constraints=constraints;
	}

	public void checkClassOrInterface(String className, ClassReport report)
		throws ClassNotFoundException
	{
		Class<?> baseClass=this.baseLoader.loadClass(className);
		try
		{
			Class<?> comparedClass=this.comparedLoader.loadClass(className);
			compareClassOrInterface(baseClass, comparedClass, report);
		}
		catch (ClassNotFoundException e)
		{
			Class<?> comparedClass;
			if (this.constraints.mustSearchRepackagedClasses())
			{
				comparedClass=searchRepackagedClass(toSimpleName(className));
			}
			else
			{
				comparedClass=null;
			}
			if (comparedClass != null)
			{
				report.setLocationChange(REPACKAGED_CLASS);
				compareClassOrInterface(baseClass, comparedClass, report);
			}
			else
			{
				report.setLocationChange(MISSING_CLASS);
			}
		}
	}

	private Class<?> searchRepackagedClass(String simpleName)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private String toSimpleName(String className)
	{
		int p=className.lastIndexOf('.');
		return p < 0
			? className
			: className.substring(1 + p);
	}

	private void compareClassOrInterface(Class<?> baseClass, Class<?> comparedClass, ClassReport report)
	{
		if (baseClass.isInterface())
		{
			if (!comparedClass.isInterface())
			{
				report.setNatureChange(ClassReport.NatureChangeType.WAS_INTERFACE);
			}
		}
		else
		{
			if (comparedClass.isInterface())
			{
				report.setNatureChange(ClassReport.NatureChangeType.WAS_CLASS);
			}
			checkConstructors(baseClass.getConstructors(), comparedClass, report);
		}
		compareModifiers(baseClass.getModifiers(), comparedClass.getModifiers(), report);
		checkVariables(baseClass.getFields(), comparedClass, report);
		checkMethods(baseClass.getMethods(), comparedClass, report);
		checkInnerClasses(baseClass.getClasses(), comparedClass, report);
	}

	private void checkMethods(Method[] baseMethods, Class<?> comparedClass, ClassReport classReport)
	{
		for (Method baseMethod : baseMethods)
		{
			if ((baseMethod.getModifiers() & Modifier.PRIVATE) == 0)
			{
				MethodReport methodReport=classReport.createMethodReport(baseMethod);
				checkMethod(baseMethod, comparedClass, methodReport);
			}
		}
	}

	private void checkMethod(Method baseMethod, Class<?> comparedClass, MethodReport report)
	{
		Method comparedMethod;
		try
		{
			comparedMethod=comparedClass.getMethod(baseMethod.getName(), baseMethod.getParameterTypes());
			// Hay compatibilidad binaria.
		}
		catch (NoSuchMethodException e)
		{
			// No hay compatibilidad binaria. Probar a buscar la sobrecarga que mejor se adapte:
			comparedMethod=getBestOverride(baseMethod.getName(), comparedClass.getMethods());
		}
		compareModifiers(baseMethod.getModifiers(), comparedMethod.getModifiers(), report);
		Class<?> baseReturn=baseMethod.getReturnType();
		Class<?> comparedReturn=comparedMethod.getReturnType();

		CompatibilityLevel returnLevel=compareTypes(baseReturn, comparedReturn);
		if (returnLevel != CompatibilityLevel.BINARY_COMPATIBLE && returnLevel != CompatibilityLevel.UNCHANGED)
		{
			report.setReturnChange(returnLevel, comparedReturn);
		}
		compareParameters(baseMethod.getParameterTypes(), comparedMethod.getParameterTypes(), report);
		compareExceptions(baseMethod.getExceptionTypes(), comparedMethod.getExceptionTypes(), report);
	}

	private Method getBestOverride(String name, Method[] methods)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private void checkConstructors(Constructor<?>[] baseConstructors, Class<?> comparedClass, ClassReport classReport)
	{
		for (Constructor<?> baseConstructor : baseConstructors)
		{
			if ((baseConstructor.getModifiers() & Modifier.PRIVATE) == 0)
			{
				ConstructorReport constructorReport=classReport.createConstructorReport(baseConstructor);
				checkConstructor(baseConstructor, comparedClass, constructorReport);
			}
		}
	}

	private void checkConstructor(Constructor<?> baseConstructor, Class<?> comparedClass, ConstructorReport report)
	{
		Constructor<?> comparedConstructor;
		try
		{
			comparedConstructor=comparedClass.getConstructor(baseConstructor.getParameterTypes());
			// Hay compatibilidad binaria.
		}
		catch (NoSuchMethodException e)
		{
			// No hay compatibilidad binaria. Probar a buscar la sobrecarga que mejor se adapte:
			comparedConstructor=getBestConstructor(comparedClass.getConstructors());
		}
		compareModifiers(baseConstructor.getModifiers(), comparedConstructor.getModifiers(), report);
		compareParameters(baseConstructor.getParameterTypes(), comparedConstructor.getParameterTypes(), report);
		compareExceptions(baseConstructor.getExceptionTypes(), comparedConstructor.getExceptionTypes(), report);
	}

	private void checkVariables(Field[] baseFields, Class<?> comparedClass, ClassReport classReport)
	{
		for (Field baseField : baseFields)
		{
			if ((baseField.getModifiers() & Modifier.PRIVATE) == 0)
			{
				FieldReport fieldReport=classReport.createFieldReport(baseField);
				checkField(baseField, comparedClass, fieldReport);
			}
		}
	}

	private Constructor<?> getBestConstructor(Constructor<?>[] constructors)
	{
		// TODO Auto-generated method stub
		return null;
	}

	private void checkField(Field baseField, Class<?> comparedClass, FieldReport fieldReport)
	{
		CompatibilityLevel level;
		Class<?> comparedType;
		try
		{
			Field comparedField=comparedClass.getField(baseField.getName());
			compareFieldValues(baseField, comparedField, fieldReport);
			Class<?> baseType=baseField.getType();
			comparedType=comparedField.getType();
			TypeUtils.compareModifiers(baseField.getModifiers(), comparedField.getModifiers(), fieldReport);
			level=compareTypes(baseType, comparedType);
		}
		catch (NoSuchFieldException e)
		{
			// No hay compatibilidad binaria.
			level=CompatibilityLevel.INCOMPATIBLE;
			comparedType=null;
		}
		if (level != CompatibilityLevel.UNCHANGED && level != CompatibilityLevel.BINARY_COMPATIBLE)
		{
			fieldReport.setTypeChange(level, comparedType);
		}
	}

	private void compareFieldValues(Field baseField, Field comparedField, FieldReport fieldReport)
	{
		try
		{
			baseField.setAccessible(true);
			Object baseValue=baseField.get(null);
			// Es una constante.
			try
			{
				comparedField.setAccessible(true);
				Object comparedValue=comparedField.get(null);
				if (baseValue == null)
				{
					if (comparedValue == null)
					{
						// Valores iguales.
					}
					else
					{
						fieldReport.setValueChange(CHANGED_VALUE);
					}
				}
				else
				{
					if (comparedValue == null)
					{
						fieldReport.setValueChange(CHANGED_VALUE);
					}
					else
					{
						if (!baseValue.equals(comparedValue))
						{
							fieldReport.setValueChange(CHANGED_VALUE);
						}
					}
				}
			}
			catch (NullPointerException e2)
			{
				fieldReport.setValueChange(WAS_CONSTANT);
			}
		}
		catch (NullPointerException e)
		{
			// No es una constante.
			try
			{
				comparedField.get(null);
				fieldReport.setValueChange(WAS_NOT_CONSTANT);
			}
			catch (NullPointerException e2)
			{
			}
			catch (IllegalAccessException e2)
			{
				throw new java.lang.RuntimeException(e);
			}
		}
		catch (IllegalAccessException e)
		{
			throw new java.lang.RuntimeException(e);
		}
	}

	private void checkInnerClasses(Class<?>[] baseInnerClasses, Class<?> comparedClass, ClassReport classReport)
	{
		for (Class<?> baseInnerClass : baseInnerClasses)
		{
			if ((baseInnerClass.getModifiers() & Modifier.PRIVATE) == 0)
			{
				ClassReport innerClassReport=classReport.createClassReport(baseInnerClass);
				compareClassOrInterface(baseInnerClass, comparedClass, innerClassReport);
			}
		}
	}
}
