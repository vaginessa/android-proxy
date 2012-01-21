package com.lechucksoftware.proxy.lib.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.util.Log;

public class ReflectionUtils
{
	public static final String TAG = "ReflectionUtils";

	static void describeClassOrInterface(Class className, String name)
	{
		displayModifiers(className.getModifiers());
		displayFields(className.getDeclaredFields());
		displayMethods(className.getDeclaredMethods());

		if (className.isInterface()) 
		{
			Log.d(TAG, "Interface: " + name);
		} 
		else 
		{
			Log.d(TAG, "Class: " + name);
			displayInterfaces(className.getInterfaces());
			displayConstructors(className.getDeclaredConstructors());
		}
	}

	static void displayModifiers(int m)
	{
		Log.d(TAG, "Modifiers: " + Modifier.toString(m));
	}

	static void displayInterfaces(Class[] interfaces)
	{
		if (interfaces.length > 0) 
		{
			Log.d(TAG, "Interfaces: ");
			for (int i = 0; i < interfaces.length; ++i)
				Log.d("", interfaces[i].getName());
		}
	}

	static void displayFields(Field[] fields)
	{
		if (fields.length > 0) 
		{
			Log.d(TAG, "Fields: ");
			for (int i = 0; i < fields.length; ++i)
				Log.d(TAG, fields[i].toString());
		}
	}

	static void displayConstructors(Constructor[] constructors)
	{
		if (constructors.length > 0) 
		{
			Log.d(TAG, "Constructors: ");
			for (int i = 0; i < constructors.length; ++i)
				Log.d(TAG, constructors[i].toString());
		}
	}

	static void displayMethods(Method[] methods)
	{
		if (methods.length > 0) 
		{
			Log.d(TAG, "Methods: ");
			for (int i = 0; i < methods.length; ++i)
				Log.d(TAG, methods[i].toString());
		}
	}

	public static Field[] getAllFields(Class klass)
	{
		List<Field> fields = new ArrayList<Field>();
		fields.addAll(Arrays.asList(klass.getDeclaredFields()));
		
		if (klass.getSuperclass() != null) 
		{
			fields.addAll(Arrays.asList(getAllFields(klass.getSuperclass())));
		}
		
		return fields.toArray(new Field[] {});
	}
}
