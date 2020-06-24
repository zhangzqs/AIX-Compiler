package cn.zzq.aix.builder.project;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.appinventor.components.runtime.collect.Maps;
import com.google.appinventor.components.runtime.collect.Sets;
import com.google.gson.Gson;

import cn.zzq.aix.builder.DescriptorManager;
import cn.zzq.aix.builder.blocks.Component;
import cn.zzq.aix.builder.buildinfo.ComponentBuildInfo;
import cn.zzq.aix.builder.doc.ComponentXMLGenerator;
import cn.zzq.aix.builder.doc.gitbook.GitbookGenerator;
import cn.zzq.aix.builder.exception.HasDifferentPackage;
import cn.zzq.aix.builder.exception.NotComponentClass;
import cn.zzq.aix.builder.utils.Path;
import javassist.CtClass;

/**
 * @author Zhiqiang
 * 
 *         <pre>
 *  该类表示一个AIX项目的子工程，具体表现为，一个项目中的源码含有多个组件源码时，
 *  同一个包下的所有组件类为一个aix项目的子工程， 会打包出一个独立的aix扩展包
 *  例如：包cn.a下有组件类Test1，Test2
 *  	包cn.b下有组件类Test3，Test4
 *  	那么Test1,Test2将会被打包为一个aix扩展包，输出文件名为cn.a.aix
 *  	   Test3，Test4将会被打包为一个aix扩展包,输出文件名为cn.b.aix
 *         </pre>
 * 
 */
public class AIXChildProject {

	private String packageName = null;
	private Set<Component> components = Sets.newHashSet();
	private Set<ComponentBuildInfo> componentInfos = Sets.newHashSet();

	// aix子工程将要打包的所有资源
	private Set<String> assetNames = Sets.newHashSet();
	// aix子工程将要打包的所有native库
	private Set<String> nativeLibraryNames = Sets.newHashSet();
	// aix子工程将要依赖的所有jar文件
	private Set<String> libraryNames = Sets.newHashSet();
	// aix子工程将要依赖的所有aiwebres文件
	private Set<String> aiwebresNames = Sets.newHashSet();

	private void addComponentClass(CtClass componentClass) throws NotComponentClass {

		// 能执行到这里说明是正常的组件类

		// 现在需要将所有的组件类解析成Component对象和ComponentBuildInfo对象
		Component component = new Component(componentClass);
		//替换xml注释
		DescriptorManager.getDescriptorManager().process(component);
		ComponentBuildInfo componentInfo = new ComponentBuildInfo(componentClass);
		components.add(component);
		componentInfos.add(componentInfo);
		// 开始分析组件依赖的各类资源
		assetNames.addAll(componentInfo.assets);
		nativeLibraryNames.addAll(componentInfo.nativeLibraries);
		libraryNames.addAll(componentInfo.libraries);

		String iconName = component.iconName;
		if (iconName.startsWith("aiwebres/")) {
			aiwebresNames.add(iconName.substring(9));
		}
	}

	public AIXChildProject(CtClass... classes) throws HasDifferentPackage, NotComponentClass {
		for (CtClass componentClass : classes) {
			this.packageName = classes[0].getPackageName();
			for (int i = 1; i < classes.length; i++) {
				if (!this.packageName.equals(classes[i].getPackageName())) {
					throw new HasDifferentPackage(this.packageName, classes[i].getPackageName());
				}
			}

			// 能执行到这里的说明所有组件类包名全部相同
			addComponentClass(componentClass);
			// 解析完毕
		}

	}

	public String generateComponentsInfo() {
		return new Gson().toJson(components);
	}

	public String generateComponentsBuildInfo() {
		return new Gson().toJson(componentInfos);
	}

	/**
	 * 生成组件的Markdown文档，返回一个HashMap，其中key为组件类全名，value为生成的markdown文档
	 * 
	 * @return 返回一个HashMap
	 */
	public HashMap<String, String> generateMarkdowns() {
		HashMap<String, String> mdMap = Maps.newHashMap();
		for (Component component : components) {
			GitbookGenerator generator = new GitbookGenerator(component);
			String markdown = generator.generatorMarkdown();
			mdMap.put(component.type + ".md", markdown);
		}
		return mdMap;
	}
	/**
	 * 生成组件的xml文档描述，返回一个HashMap，其中key为组件类全名，value为生成的xml文档描述
	 * 
	 * @return 返回一个HashMap
	 */
	public HashMap<String, String> generateXmls() {
		HashMap<String, String> mdMap = Maps.newHashMap();
		for (Component component : components) {
			ComponentXMLGenerator generator=new ComponentXMLGenerator(component);
			String xmlText=generator.generateXMLDescriptor();
			mdMap.put(component.type + ".xml", xmlText);
		}
		return mdMap;
	}


	/**
	 * 给定一个资源池（由若干个包含资源文件的文件夹所组成），去查找aix子工程所依赖的资源文件并返回
	 * 
	 * @param assetPools 资源池
	 * @return aix依赖的资源
	 */
	public Set<Path> getAssets(Set<Path> assetPools) {
		return getFileInFilePools(assetNames.toArray(new String[assetNames.size()]), assetPools);
	}

	/**
	 * 给定一个jar库池（由若干个包含jar文件的文件夹所组成），去查找aix子工程所依赖的jar文件并返回
	 * 
	 * @param libraryPools jar库池
	 * @return 依赖的jar包
	 */
	public Set<Path> getLibraries(Set<Path> libraryPools) {
		Set<Path> libPaths = getFileInFilePools(libraryNames.toArray(new String[libraryNames.size()]), libraryPools);
		return libPaths;
	}

	public Set<Path> getAiWebreses(Set<Path> webresPools) {
		return getFileInFilePools(aiwebresNames.toArray(new String[aiwebresNames.size()]), webresPools);

	}

	public Set<NativeLibrary> getNativeLibraries(Set<Path> jniDirPools) {
		HashMap<String, NativeLibrary> result = Maps.newHashMap();
		for (String nativeLibraryName : nativeLibraryNames) {
			for (Path jniDir : jniDirPools) {
				NativeLibrary jni = NativeLibrary.getNativeLibrary(jniDir, nativeLibraryName);
				if (jni != null) {
					// 找到了当前架构对应的so文件了，应当加入到结果并不再查找当前架构的so了
					result.put(nativeLibraryName, jni);
					break;
				}
			}
		}

		// 核验nativeLibrary是否完全添加完毕
		for (String nativeLibraryName : nativeLibraryNames) {
			if (!result.containsKey(nativeLibraryName)) {
				throw new RuntimeException("找不到so库" + nativeLibraryName + "在jni池" + jniDirPools);
			}
		}

		return new HashSet<NativeLibrary>(result.values());
	}

	/**
	 * 在一个文件池中按照给的文件名获取所有的Path对象
	 * 
	 * @param fileNames 文件名数组
	 * @param filePools 文件池数组
	 * @return
	 */
	private Set<Path> getFileInFilePools(String[] fileNames, Set<Path> filePools) {
		Map<String, Path> fileInFilesPool = Maps.newHashMap();
		for (Path filePool : filePools) {
			// 首先遍历所有的文件池
			if (filePool.isDirectory()) {
				for (Path fileInPool : filePool.listChildren()) {
					// 获得到了每一个资源池中的每一个资源
					String fileName = fileInPool.getName();
					fileInFilesPool.put(fileName, fileInPool);
				}
			} else {
				fileInFilesPool.put(filePool.getName(), filePool);
			}
		}

		// 现在所有的文件都集中在了fileInFilePool中，并且key为文件名

		// 造一个结果用于存放所需的资源
		Set<Path> files = Sets.newHashSet();
		for (String fileName : fileNames) {
			Path asset = fileInFilesPool.get(fileName);
			if (asset == null) {
				// 说明找不到资源，需要抛出异常
				throw new RuntimeException("找不到文件" + fileName + "在文件池" + filePools);
			}
			files.add(asset);
		}
		return files;
	}

	public String getPackageName() {
		return packageName;
	}

	public Set<Component> getComponents() {
		return components;
	}

	public Set<ComponentBuildInfo> getComponentInfos() {
		return componentInfos;
	}

	public Set<String> getAssetNames() {
		return assetNames;
	}

	public Set<String> getNativeLibraryNames() {
		return nativeLibraryNames;
	}

	public Set<String> getLibraryNames() {
		return libraryNames;
	}

	public Set<String> getAiwebresNames() {
		return aiwebresNames;
	}
}
