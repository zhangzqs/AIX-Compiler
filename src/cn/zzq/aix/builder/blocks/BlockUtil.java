package cn.zzq.aix.builder.blocks;

import java.lang.reflect.Array;
import java.util.List;
import java.util.Set;

import javax.lang.model.type.TypeMirror;

import com.google.appinventor.components.runtime.collect.Lists;
import com.google.appinventor.components.runtime.collect.Sets;

import cn.zzq.aix.builder.blocks.beans.EventBean;
import cn.zzq.aix.builder.blocks.beans.MethodBean;
import cn.zzq.aix.builder.blocks.beans.ParameterBean;
import javassist.CtClass;
import javassist.NotFoundException;

public class BlockUtil {
	public static String[] getParameterNames(Object parameters) {
		List<String> parameterNames = Lists.newArrayList();

		for (int i = 0; i < Array.getLength(parameters); i++) {
			String parameterName = ((ParameterBean) Array.get(parameters, i)).name;
			parameterNames.add(parameterName);
		}
		return parameterNames.toArray(new String[parameterNames.size()]);
	}

	public static Set<String> getMethodNames(Object methods) {
		Set<String> methodNames = Sets.newHashSet();
		for (int i = 0; i < Array.getLength(methods); i++) {

			String methodName = ((MethodBean) Array.get(methods, i)).name;
			methodNames.add(methodName);
		}
		return methodNames;
	}

	public static Set<String> getEventNames(Object events) {
		Set<String> eventNames = Sets.newHashSet();
		for (int i = 0; i < Array.getLength(events); i++) {

			String eventName = ((EventBean) Array.get(events, i)).name;
			eventNames.add(eventName);
		}
		return eventNames;
	}

	/**
	 * Returns the appropriate Yail type (e.g., "number" or "text") for a given Java
	 * type (e.g., "float" or "java.lang.String"). All component names are converted
	 * to "component".
	 * 
	 * @param type a type name, as returned by {@link TypeMirror#toString()}
	 * @return one of "boolean", "text", "number", "list", or "component".
	 * @throws RuntimeException if the parameter cannot be mapped to any of the
	 *                          legal return values
	 */
	public static String javaaTypeToYailType(String type) {
		// boolean -> boolean
		if (type.equals("boolean")) {
			return type;
		}
		// String -> text
		if (type.equals("java.lang.String")) {
			return "text";
		}
		// {float, double, int, short, long, byte} -> number
		if (type.equals("float") || type.equals("double") || type.equals("int") || type.equals("short")
				|| type.equals("long") || type.equals("byte")) {
			return "number";
		}
		// YailList -> list
		if (type.equals("com.google.appinventor.components.runtime.util.YailList")) {
			return "list";
		}
		// List<?> -> list
		if (type.startsWith("java.util.List")) {
			return "list";
		}

		// YailProcedure -> procedure
		if (type.equals("com.google.appinventor.components.runtime.util.YailProcedure")) {
			return "procedure";
		}
		if (type.equals("com.google.appinventor.components.runtime.util.YailDictionary")) {
			return "dictionary";
		}
		if (type.equals("com.google.appinventor.components.runtime.util.YailObject")) {
			return "yailobject";
		}

		// Calendar -> InstantInTime
		if (type.equals("java.util.Calendar")) {
			return "InstantInTime";
		}

		if (type.equals("com.google.appinventor.components.runtime.Component")) {
			return "component";
		}
		return "any";
	}

	public static String javaTypeToYailType(CtClass type) {

		//System.out.println(type);
		if (isInterfaceImpl("com.google.appinventor.components.runtime.Component", type)) {
			return "component";
		}

		return javaaTypeToYailType(type.getName());
	}

	public static List<CtClass> getExtendsList(CtClass cc) {
		List<CtClass> lc = Lists.newArrayList();
		for (CtClass parent = cc; //
				parent != null; //
				parent = getSuperClass(parent)) {
			lc.add(parent);
		}
		return lc;
	}

	public static CtClass getSuperClass(CtClass cc) {
		try {
			return cc.getSuperclass();
		} catch (NotFoundException e) {
			return null;
		}
	}

	public static Set<CtClass> getImplInterfaces(CtClass cc) {
		Set<CtClass> li = Sets.newHashSet();
		getExtendsList(cc).forEach(c -> {
			try {
				for (CtClass inf : c.getInterfaces()) {
					li.add(inf);
				}
			} catch (NotFoundException e) {
			}
		});
		return li;
	}

	/**
	 * 判断子类与父类关系是否合法
	 * 
	 * @param parent 父类
	 * @param child  子类
	 * @return 判定结果
	 */
	public static boolean isInterfaceImpl(String interfaceName, CtClass child) {

		for (CtClass inf : getImplInterfaces(child)) {
			if (inf.getName().equals(interfaceName)) {
				return true;
			}
		}
		return false;
	}

}
