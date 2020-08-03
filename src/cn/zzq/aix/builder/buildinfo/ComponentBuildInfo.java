package cn.zzq.aix.builder.buildinfo;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.util.Set;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleBroadcastReceiver;
import com.google.appinventor.components.annotations.UsesActivities;
import com.google.appinventor.components.annotations.UsesAssets;
import com.google.appinventor.components.annotations.UsesBroadcastReceivers;
import com.google.appinventor.components.annotations.UsesLibraries;
import com.google.appinventor.components.annotations.UsesNativeLibraries;
import com.google.appinventor.components.annotations.UsesPermissions;
import com.google.appinventor.components.annotations.androidmanifest.ActionElement;
import com.google.appinventor.components.annotations.androidmanifest.ActivityElement;
import com.google.appinventor.components.annotations.androidmanifest.CategoryElement;
import com.google.appinventor.components.annotations.androidmanifest.DataElement;
import com.google.appinventor.components.annotations.androidmanifest.IntentFilterElement;
import com.google.appinventor.components.annotations.androidmanifest.MetaDataElement;
import com.google.appinventor.components.annotations.androidmanifest.ReceiverElement;

import cn.zzq.aix.builder.annotation.UsesXMLActivities;
import cn.zzq.aix.builder.exception.NotComponentClass;
import javassist.CtClass;

public class ComponentBuildInfo extends ComponentBuildInfoBean {

	// Must match buildserver.compiler.ARMEABI_V7A_SUFFIX
	public static final String ARMEABI_V7A_SUFFIX = "-v7a";
	// Must match buildserver.compiler.ARMEABI_V8A_SUFFIX
	public static final String ARM64_V8A_SUFFIX = "-v8a";
	// Must match buildserver.compiler.X86_64_SUFFIX
	public static final String X86_64_SUFFIX = "-x64";// "-x8a";
	// Must match buildserver.compiler.X86_SUFFIX
	public static final String X86_SUFFIX = "-x86";

	public ComponentBuildInfo(CtClass componentClass) throws NotComponentClass {
		DesignerComponent designerComponent = null;
		try {
			designerComponent = (DesignerComponent) componentClass.getAnnotation(DesignerComponent.class);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (designerComponent == null) {
			throw new NotComponentClass(componentClass, "不存在@DesignerComponent注解");
		}
		// 能执行到这里说明全部都是组件类
		type = componentClass.getName();
		try {
			androidMinSdk.add(Integer.toString(
					((DesignerComponent) componentClass.getAnnotation(DesignerComponent.class)).androidMinSdk()));

			generateAnnotationString(componentClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("deprecation")
	private void generateAnnotationString(CtClass componentClass) throws ClassNotFoundException {
		// Gather permissions.
		UsesPermissions usesPermissions = (UsesPermissions) componentClass.getAnnotation(UsesPermissions.class);
		if (usesPermissions != null) {
			for (String permission : usesPermissions.permissionNames().split(",")) {
				updateWithNonEmptyValue(permissions, permission);
			}
			for (String permission : usesPermissions.value()) {
				updateWithNonEmptyValue(permissions, permission);
			}
		}

		// Gather library names. UsesLibraries usesLibraries =
		UsesLibraries usesLibraries = (UsesLibraries) componentClass.getAnnotation(UsesLibraries.class);
		if (usesLibraries != null) {
			for (String library : usesLibraries.libraries().split(",")) {
				updateWithNonEmptyValue(libraries, library);
			}
			for (String library : usesLibraries.value()) {
				updateWithNonEmptyValue(libraries, library);
			}
		}

		// Gather native library names.
		UsesNativeLibraries usesNativeLibraries = (UsesNativeLibraries) componentClass
				.getAnnotation(UsesNativeLibraries.class);
		if (usesNativeLibraries != null) {
			if (!usesNativeLibraries.v7aLibraries().isEmpty()) {
				for (String v7aLibrary : usesNativeLibraries.v7aLibraries().split(",")) {
					updateWithNonEmptyValue(nativeLibraries, v7aLibrary.trim() + ARMEABI_V7A_SUFFIX);
				}
			}
			if (!usesNativeLibraries.v8aLibraries().isEmpty()) {
				for (String v8aLibrary : usesNativeLibraries.v8aLibraries().split(",")) {
					updateWithNonEmptyValue(nativeLibraries, v8aLibrary.trim() + ARM64_V8A_SUFFIX);
				}
			}
			if (!usesNativeLibraries.x86_64Libraries().isEmpty()) {
				for (String x8664Library : usesNativeLibraries.x86_64Libraries().split(",")) {
					updateWithNonEmptyValue(nativeLibraries, x8664Library.trim() + X86_64_SUFFIX);
				}
			}
			if (!usesNativeLibraries.x86Libraries().isEmpty()) {
				for (String x86Library : usesNativeLibraries.x86Libraries().split(",")) {
					updateWithNonEmptyValue(nativeLibraries, x86Library.trim() + X86_SUFFIX);
				}
			}

		}

		// Gather required files.
		UsesAssets usesAssets = (UsesAssets) componentClass.getAnnotation(UsesAssets.class);
		if (usesAssets != null) {
			for (String file : usesAssets.fileNames().split(",")) {
				updateWithNonEmptyValue(assets, file);
			}
		}

		// Gather the required activities and build their element strings.
		UsesActivities usesActivities = (UsesActivities) componentClass.getAnnotation(UsesActivities.class);
		if (usesActivities != null) {

			for (ActivityElement ae : usesActivities.activities()) {
				try {
					updateWithNonEmptyValue(activities, activityElementToString(ae));
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		// Gather the required broadcast receivers and build their element
		// strings.
		UsesBroadcastReceivers usesBroadcastReceivers = (UsesBroadcastReceivers) componentClass
				.getAnnotation(UsesBroadcastReceivers.class);
		if (usesBroadcastReceivers != null) {

			for (ReceiverElement re : usesBroadcastReceivers.receivers()) {
				try {
					updateWithNonEmptyValue(broadcastReceivers, receiverElementToString(re));
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		// TODO(Will): Remove the following legacy code once the deprecated
		// @SimpleBroadcastReceiver annotation is removed. It should
		// should remain for the time being because otherwise we'll break
		// extensions currently using @SimpleBroadcastReceiver.
		//
		// Gather required actions for legacy Broadcast Receivers. The
		// annotation
		// has a Class Name and zero or more Filter Actions. In the
		// resulting String, Class name will go first, and each Action
		// will be added, separated by a comma.

		@SuppressWarnings({ "deprecation" })
		SimpleBroadcastReceiver simpleBroadcastReceiver = (SimpleBroadcastReceiver) componentClass
				.getAnnotation(SimpleBroadcastReceiver.class);
		if (simpleBroadcastReceiver != null) {
			for (String className : simpleBroadcastReceiver.className().split(",")) {
				StringBuffer nameAndActions = new StringBuffer();
				nameAndActions.append(className.trim());
				for (String action : simpleBroadcastReceiver.actions().split(",")) {
					nameAndActions.append("," + action.trim());
				}
				classNameAndActionsBR.add(nameAndActions.toString());
				break; // We only need one class name; If more than one is
						// passed, ignore all but
						// first.
			}
		}

		// 以下为ACC编译器特有的使用XML文件注解
		UsesXMLActivities usesXMLActivities = (UsesXMLActivities) componentClass.getAnnotation(UsesXMLActivities.class);
		if (usesXMLActivities != null) {
			for (String xmlFileName : usesXMLActivities.fileNames().trim().split(",")) {
				// String xmlText=FileUtils.readTextFile();
				// TODO
			}
		}
	}

	/**
	 * 清空多余的空白字符，并存放到指定的Set集合中
	 * 
	 * @param collection Set集合
	 * @param value      字符串
	 */
	private void updateWithNonEmptyValue(Set<String> collection, String value) {
		// 刪除空白的空格字符
		String trimmedValue = value.trim();
		// 如果刪除完还是不为空，那么就添加进指定的Set集合中
		if (!trimmedValue.isEmpty()) {
			collection.add(trimmedValue);
		}
	}

	// Transform an @ActivityElement into an XML element String for use later
	// in creating AndroidManifest.xml.
	private static String activityElementToString(ActivityElement element)
			throws IllegalAccessException, InvocationTargetException {
		// First, we build the <activity> element's opening tag including any
		// receiver element attributes.
		StringBuilder elementString = new StringBuilder("    <activity ");
		elementString.append(elementAttributesToString(element));
		elementString.append(">\n");

		// Now, we collect any <activity> subelements.
		elementString.append(subelementsToString(element.metaDataElements()));
		elementString.append(subelementsToString(element.intentFilters()));

		// Finally, we close the <activity> element and create its String.
		return elementString.append("    </activity>\n").toString();
	}

	// Transform a @ReceiverElement into an XML element String for use later
	// in creating AndroidManifest.xml.
	private static String receiverElementToString(ReceiverElement element)
			throws IllegalAccessException, InvocationTargetException {
		// First, we build the <receiver> element's opening tag including any
		// receiver element attributes.
		StringBuilder elementString = new StringBuilder("    <receiver ");
		elementString.append(elementAttributesToString(element));
		elementString.append(">\n");

		// Now, we collect any <receiver> subelements.
		elementString.append(subelementsToString(element.metaDataElements()));
		elementString.append(subelementsToString(element.intentFilters()));

		// Finally, we close the <receiver> element and create its String.
		return elementString.append("    </receiver>\n").toString();
	}

	// Transform a @MetaDataElement into an XML element String for use later
	// in creating AndroidManifest.xml.
	private static String metaDataElementToString(MetaDataElement element)
			throws IllegalAccessException, InvocationTargetException {
		// First, we build the <meta-data> element's opening tag including any
		// receiver element attributes.
		StringBuilder elementString = new StringBuilder("      <meta-data ");
		elementString.append(elementAttributesToString(element));
		// Finally, we close the <meta-data> element and create its String.
		return elementString.append("/>\n").toString();
	}

	// Transform an @IntentFilterElement into an XML element String for use
	// later
	// in creating AndroidManifest.xml.
	private static String intentFilterElementToString(IntentFilterElement element)
			throws IllegalAccessException, InvocationTargetException {
		// First, we build the <intent-filter> element's opening tag including
		// any
		// receiver element attributes.
		StringBuilder elementString = new StringBuilder("      <intent-filter ");
		elementString.append(elementAttributesToString(element));
		elementString.append(">\n");

		// Now, we collect any <intent-filter> subelements.
		elementString.append(subelementsToString(element.actionElements()));
		elementString.append(subelementsToString(element.categoryElements()));
		elementString.append(subelementsToString(element.dataElements()));

		// Finally, we close the <intent-filter> element and create its String.
		return elementString.append("    </intent-filter>\n").toString();
	}

	// Transform an @ActionElement into an XML element String for use later
	// in creating AndroidManifest.xml.
	private static String actionElementToString(ActionElement element)
			throws IllegalAccessException, InvocationTargetException {
		// First, we build the <action> element's opening tag including any
		// receiver element attributes.
		StringBuilder elementString = new StringBuilder("        <action ");
		elementString.append(elementAttributesToString(element));
		// Finally, we close the <action> element and create its String.
		return elementString.append("/>\n").toString();
	}

	// Transform an @CategoryElement into an XML element String for use later
	// in creating AndroidManifest.xml.
	private static String categoryElementToString(CategoryElement element)
			throws IllegalAccessException, InvocationTargetException {
		// First, we build the <category> element's opening tag including any
		// receiver element attributes.
		StringBuilder elementString = new StringBuilder("        <category ");
		elementString.append(elementAttributesToString(element));
		// Finally, we close the <category> element and create its String.
		return elementString.append("/>\n").toString();
	}

	// Transform an @DataElement into an XML element String for use later
	// in creating AndroidManifest.xml.
	private static String dataElementToString(DataElement element)
			throws IllegalAccessException, InvocationTargetException {
		// First, we build the <data> element's opening tag including any
		// receiver element attributes.
		StringBuilder elementString = new StringBuilder("        <data ");
		elementString.append(elementAttributesToString(element));
		// Finally, we close the <data> element and create its String.
		return elementString.append("/>\n").toString();
	}

	// Build the attribute String for a given XML element modeled by an
	// annotation.
	//
	// Note that we use the fully qualified names for certain classes in the
	// "java.lang.reflect" package to avoid namespace collisions.
	private static String elementAttributesToString(Annotation element)
			throws IllegalAccessException, InvocationTargetException {
		StringBuilder attributeString = new StringBuilder("");
		Class<? extends Annotation> clazz = element.annotationType();
		java.lang.reflect.Method[] methods = clazz.getDeclaredMethods();
		String attributeSeparator = "";
		for (java.lang.reflect.Method method : methods) {
			int modCode = method.getModifiers();
			if (java.lang.reflect.Modifier.isPublic(modCode) && !java.lang.reflect.Modifier.isStatic(modCode)) {
				if (method.getReturnType().getSimpleName().equals("String")) {
					// It is an XML element attribute.
					String attributeValue = (String) method.invoke(clazz.cast(element));
					if (!attributeValue.equals("")) {
						attributeString.append(attributeSeparator);
						attributeString.append("android:");
						attributeString.append(method.getName());
						attributeString.append("=\"");
						attributeString.append(attributeValue);
						attributeString.append("\"");
						attributeSeparator = " ";
					}
				}
			}
		}
		return attributeString.toString();
	}

	// Build the subelement String for a given array of XML elements modeled by
	// corresponding annotations.
	private static String subelementsToString(Annotation[] subelements)
			throws IllegalAccessException, InvocationTargetException {
		StringBuilder subelementString = new StringBuilder("");
		for (Annotation subelement : subelements) {
			if (subelement instanceof MetaDataElement) {
				subelementString.append(metaDataElementToString((MetaDataElement) subelement));
			} else if (subelement instanceof IntentFilterElement) {
				subelementString.append(intentFilterElementToString((IntentFilterElement) subelement));
			} else if (subelement instanceof ActionElement) {
				subelementString.append(actionElementToString((ActionElement) subelement));
			} else if (subelement instanceof CategoryElement) {
				subelementString.append(categoryElementToString((CategoryElement) subelement));
			} else if (subelement instanceof DataElement) {
				subelementString.append(dataElementToString((DataElement) subelement));
			}
		}
		return subelementString.toString();
	}

}
