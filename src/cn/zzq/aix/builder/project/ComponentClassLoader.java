package cn.zzq.aix.builder.project;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.runtime.collect.Maps;
import com.google.appinventor.components.runtime.collect.Sets;

import cn.zzq.aix.builder.RuntimeEnvironment;
import cn.zzq.aix.builder.utils.Logger;
import cn.zzq.aix.builder.utils.Path;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

public class ComponentClassLoader {

	private static ClassPool activeClassPool;

	public static ClassPool getActiveClassPool() {
		return activeClassPool;
	}

	// 以包名为key，该包下组件类的CtClass对象存放所有组件
	private Map<String, Set<CtClass>> componentClassMap = Maps.newHashMap();

	private Set<Path> libraries = Sets.newHashSet();

	/**
	 * <pre>
	 * 根据编译后的字节码目录来构建组件类的加载器(不是Java的类加载器)，
	 * 以便于获取其所有的组件类的CtClass对象并按照包名划分组件
	 * </pre>
	 * 
	 * @param classesPath 编译后的字节码目录
	 */
	public ComponentClassLoader(Path classesPath, Path... libraries) {
		addLibrary(libraries);
		// 将所有字节码加载为CtClass对象集合
		Set<CtClass> ccs = loadComponentClasses(classesPath.listAllChildrenBySuffix(".class"));
		for (CtClass cc : ccs) {
			String packageName = cc.getPackageName();
			Set<CtClass> aixChildClasses = componentClassMap.get(packageName);

			// 如果第一次加载该包，那么就创建一个该包下的所有组件类的集合，并且加入map
			if (aixChildClasses == null) {
				aixChildClasses = Sets.newHashSet();
				componentClassMap.put(packageName, aixChildClasses);
			}

			// 此时map中packageName对应的CtClass集合创建好了，开始添加
			aixChildClasses.add(cc);
		}
	}

	public void addLibrary(Path... libraries) {
		this.libraries.addAll(Arrays.asList(libraries));
	}

	public Set<String> getPackageNames() {
		return componentClassMap.keySet();
	}

	public Set<CtClass> getAIXClasses(String packageName) {
		return componentClassMap.get(packageName);
	}

	private Set<CtClass> loadComponentClasses(Path... classFiles) {
		Logger.log("正在获取所有的组件类");
		int count = 0;
		Set<CtClass> result = Sets.newHashSet();
		ClassPool classPool = new ClassPool();
		try {
			for (Path runtimeLibs : RuntimeEnvironment.RUNTIME_LIBRARY_DIR.listAllChildrenBySuffix(".jar")) {
				classPool.appendClassPath(runtimeLibs.toString());
			}
			for (Path lib : libraries) {
				for (Path library : lib.listAllChildrenBySuffix(".jar")) {
					classPool.appendClassPath(library.toString());
				}
			}
		} catch (NotFoundException e1) {
			e1.printStackTrace();
		}

		// 添加classPath
		for (Path classFile : classFiles) {
			try {
				classPool.appendClassPath(classFile.toString());
			} catch (NotFoundException e) {
				e.printStackTrace();
			}
		}

		for (Path classFile : classFiles) {

			try {

				CtClass c = classPool.makeClass(classFile.buildInputStream());
				DesignerComponent designerComponent;
				try {
					designerComponent = (DesignerComponent) c.getAnnotation(DesignerComponent.class);
				} catch (ClassNotFoundException e) {
					designerComponent = null;
				}
				if (designerComponent != null) {
					Logger.log("获取到组件" + (++count) + ": " + c.getName());
					result.add(c);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Logger.log("获取组件类完毕，共有" + count + "个组件类");

		ComponentClassLoader.activeClassPool = classPool;
		return result;
	}

	@Override
	public String toString() {
		JSONObject jo = new JSONObject();
		for (String packageName : getPackageNames()) {
			JSONArray ja = new JSONArray();
			for (final CtClass cc : componentClassMap.get(packageName)) {
				ja.put(cc.getName());
			}
			jo.put(packageName, ja);
		}
		return jo.toString();
	}
	/*
	 * public static Set<CtClass> getComponentClasses(Set<CtClass> classes) {
	 * Logger.log("正在获取所有的组件类"); int count = 0; Set<CtClass> result =
	 * Sets.newHashSet(); for (CtClass cc : classes) { DesignerComponent
	 * designerComponent; try { designerComponent = (DesignerComponent) cc
	 * .getAnnotation(DesignerComponent.class); } catch (ClassNotFoundException e) {
	 * designerComponent = null; } if (designerComponent != null) {
	 * Logger.log("获取到组件" + (++count) + ": " + cc.getName()); result.add(cc); } }
	 * Logger.log("获取组件类完毕，共有" + count + "个组件类"); return result; }
	 * 
	 * public static Set<CtClass> getComponentClasses(Path path) { Set<Path>
	 * classFiles = getAllClassFiles(path); Set<CtClass> classes =
	 * loadClasses(classFiles); Set<CtClass> componentClasses =
	 * getComponentClasses(classes); return componentClasses; }
	 * 
	 * public static Set<Component> getComponents(Path path) { return
	 * getComponents(getComponentClasses(path)); }
	 * 
	 * public static Set<Component> getComponents(Set<CtClass> componentClasses) {
	 * Set<Component> components = Sets.newHashSet(); for (CtClass ctClass :
	 * componentClasses) { components.add(new Component(ctClass)); } return
	 * components; }
	 * 
	 * public static Set<ComponentBuildInfo> getComponentBuildInfos(Path path) {
	 * return getComponentBuildInfos(getComponentClasses(path)); }
	 * 
	 * public static Set<ComponentBuildInfo> getComponentBuildInfos( Set<CtClass>
	 * componentClasses) { Set<ComponentBuildInfo> components = Sets.newHashSet();
	 * for (CtClass ctClass : componentClasses) { components.add(new
	 * ComponentBuildInfo(ctClass)); } return components; }
	 */
}
