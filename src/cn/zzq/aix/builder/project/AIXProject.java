package cn.zzq.aix.builder.project;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.appinventor.components.runtime.collect.Sets;

import cn.zzq.aix.builder.DescriptorManager;
import cn.zzq.aix.builder.RuntimeEnvironment;
import cn.zzq.aix.builder.compiler.ECJCompiler;
//import cn.zzq.aix.builder.compiler.KotlinCompiler;
import cn.zzq.aix.builder.exception.HasDifferentPackage;
import cn.zzq.aix.builder.exception.NotComponentClass;
import cn.zzq.aix.builder.utils.Logger;
import cn.zzq.aix.builder.utils.Path;
import cn.zzq.aix.builder.utils.Path.PathFilter;
import cn.zzq.aix.builder.utils.ZipUtils;
import javassist.CtClass;
import net.lingala.zip4j.exception.ZipException;

public class AIXProject {

	private Path buildPath = null;
	private Path aixOutputPath = null;

	public Path getBuildPath() {
		return buildPath;
	}

	public void setOutputPath(Path outputPath) {
		this.buildPath = outputPath;
		this.outputClassPath = outputPath.forward("classes");
		this.outputComponentDir = outputPath.forward("components");
		this.aixOutputPath = buildPath.forward("outputs");
	}

	private Path outputClassPath = null;
	private Path outputComponentDir = null;
	private HashSet<Path> sourcePaths = Sets.newHashSet();
	private HashSet<Path> libraryPaths = Sets.newHashSet();
	private HashSet<Path> aiwebresPaths = Sets.newHashSet();
	private HashSet<Path> jniDirPaths = Sets.newHashSet();
	private HashSet<Path> assetPaths = Sets.newHashSet();

//	private TextQuoter textQuoter=null;
//	public void setTextQuoter(TextQuoter textQuoter) {
//		this.textQuoter=textQuoter;
//	}

	public AIXProject(Path outputPath, Path... sourcePaths) {
		this.addSourcePaths(sourcePaths);
		this.setOutputPath(outputPath);
	}

	public void clean() {
		if (buildPath != null && buildPath.exists()) {
			Logger.log("开始清理编译输出目录");
			buildPath.deleteAll();
		}
	}

	// public Map<String,Set<Path>> collectSourceFiles() {

	// }

	// 编译java源码
	public void compileJava() {
		Logger.log("正在编译Java代码");
		ECJCompiler ecj = new ECJCompiler();
		ecj.addSourcePath(sourcePaths.toArray(new Path[sourcePaths.size()]));
		ecj.setCompileLevel("1.8", "1.8");
		ecj.setEncding("utf-8");
		ecj.saveParmsName = true;
		Logger.log("当前AppInventor运行时环境目录：" + RuntimeEnvironment.RUNTIME_LIBRARY_DIR);
		// 添加系统ClassPath环境
		ecj.addClassPath(RuntimeEnvironment.RUNTIME_LIBRARY_DIR);
		// 添加用户ClassPath环境
		ecj.addClassPath(libraryPaths.toArray(new Path[libraryPaths.size()]));
		ecj.setOutputPath(outputClassPath);
		ecj.compile();
	}

	// 编译java源码
//	public void compileKotlin() {
//
//		
//		Logger.log("正在编译Kotlin代码");
//		KotlinCompiler ecj = new KotlinCompiler();
//		ecj.addSourcePath(sourcePaths.toArray(new Path[sourcePaths.size()]));
//		ecj.setCompileLevel("1.8", "1.8");
//		ecj.setEncding("utf-8");
//		ecj.saveParmsName = true;
//		Logger.log("当前AppInventor运行时环境目录：" + RuntimeEnvironment.RUNTIME_LIBRARY_DIR);
//		// 添加系统ClassPath环境
//		ecj.addClassPath(RuntimeEnvironment.RUNTIME_LIBRARY_DIR);
//		// 添加用户ClassPath环境
//		ecj.addClassPath(libraryPaths.toArray(new Path[libraryPaths.size()]));
//		ecj.setOutputPath(outputClassPath);
//		return;
//		//ecj.compile();
//	}

	public void packJar() {
		Logger.log("准备打包AndroidRuntime.jar");
		try {
			outputClassPath.compressAsZip(buildPath.forward("AndroidRuntime.jar"));
		} catch (ZipException e) {
			Logger.err("打包AndroidRuntime.jar发生异常");
			e.printStackTrace();
		}
	}

	public static void writeMarkdowns(Path parentPath, Map<String, String> mdMap) {
		for (String fileName : mdMap.keySet()) {
			String content = mdMap.get(fileName);
			try {
				parentPath.forward(fileName).appendText(content);
			} catch (IOException e) {
				Logger.log("写入Markdown文档" + fileName + "发生IO异常 ");
				e.printStackTrace();
			}
		}
	}

	public static void writeXmls(Path parentPath, Map<String, String> mdMap) {
		for (String fileName : mdMap.keySet()) {
			String content = mdMap.get(fileName);
			try {
				parentPath.forward(fileName).appendText(content);
			} catch (IOException e) {
				Logger.log("写入Xml文档" + fileName + "发生IO异常 ");
				e.printStackTrace();
			}
		}
	}

	public void buildAixChildProjects() {
		Logger.log("准备构建所有的aix子工程");

		ComponentClassLoader components = new ComponentClassLoader(outputClassPath,
				libraryPaths.toArray(new Path[libraryPaths.size()]));
		for (String packageName : components.getPackageNames()) {
			Logger.log("开始构建包" + packageName);
			Set<CtClass> ccs = components.getAIXClasses(packageName);
			try {
				AIXChildProject aixChildProject = new AIXChildProject(ccs.toArray(new CtClass[ccs.size()]));
				AIXChildProjectBuilder aixChildBuilder = new AIXChildProjectBuilder(aixChildProject,
						buildPath.forward("AndroidRuntime.jar"), outputComponentDir.forward(packageName));
				aixChildBuilder.addAiwebreses(aiwebresPaths);
				aixChildBuilder.addAssets(assetPaths);
				aixChildBuilder.addJniDirs(jniDirPaths);
				aixChildBuilder.addLibraries(libraryPaths);
				aixChildBuilder.build();
				writeXmls(aixOutputPath.forward("descriptors"), aixChildBuilder.generateXmls());
				writeMarkdowns(aixOutputPath, aixChildBuilder.generateMarkdowns());
				Logger.log("包" + packageName + "构建完毕");
			} catch (HasDifferentPackage e) {
				Logger.log("不在同一个包下的组件类不能组合为一个aix项目");
				e.printStackTrace();
			} catch (NotComponentClass e) {
				Logger.log("找不到组件类");
				e.printStackTrace();
			}

		}
	}

	public void packAIX() {
		Path[] componentOutputs = buildPath.forward("components").listChildren();

		aixOutputPath.mkdirs();
		for (Path componentOutput : componentOutputs) {
			try {
				ZipUtils.pack(aixOutputPath.forward(componentOutput.getName() + ".aix"), componentOutput);
			} catch (ZipException e) {
				Logger.err("打包aix阶段发生异常,相关路径" + componentOutput);
				e.printStackTrace();
			}
		}
		if (componentOutputs.length > 1) {
			try {
				ZipUtils.pack(aixOutputPath.forward("extensions_" + componentOutputs.length + "in1.aix"),
						componentOutputs);
			} catch (ZipException e) {
				Logger.err("打包aix阶段发生异常,相关路径" + componentOutputs);
				e.printStackTrace();
			}
		}
	}

	// 将要打包的各种文件组放入列表，调用如下几个add方法（放入不一定打包，需要在aix源代码中使用相关注解声明）
	public void addSourcePaths(Path... sourcePaths) {
		this.sourcePaths.addAll(Arrays.asList(sourcePaths));
	}

	public void addWebreses(Path... webreses) {
		for (Path webres : webreses) {
			aiwebresPaths.add(webres);
		}
	}

	public void addJniDirs(Path... jniDirs) {
		for (Path jniDir : jniDirs) {
			jniDirPaths.add(jniDir);
		}
	}

	public void addXmlDescriptors(Path... xmls) {
		for (Path xml : xmls) {
			if (xml.exists()) {
				if (xml.isDirectory()) {
					for (Path xmlFile : xml.listAllChildren(new PathFilter() {
						@Override
						public boolean accept(Path path) {
							return path.getName().endsWith(".xml");
						}
					})) {
						DescriptorManager.getDescriptorManager().addXmlDescriptor(xmlFile.readAsText());
					}
				} else {
					DescriptorManager.getDescriptorManager().addXmlDescriptor(xml.readAsText());
				}
			}
		}
	}

	public void addLibraries(Path... libraries) {
		for (Path library : libraries) {
			libraryPaths.add(library);
		}
	}

	public void addAssets(Path... assets) {
		for (Path asset : assets) {
			assetPaths.add(asset);
		}
	}
}
