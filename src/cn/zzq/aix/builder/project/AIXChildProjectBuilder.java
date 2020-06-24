package cn.zzq.aix.builder.project;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

import com.google.appinventor.components.runtime.collect.Sets;

import cn.zzq.aix.builder.dex.D8Builder;
import cn.zzq.aix.builder.utils.Logger;
import cn.zzq.aix.builder.utils.Path;
import cn.zzq.aix.builder.utils.ZipUtils;
import net.lingala.zip4j.exception.ZipException;

public class AIXChildProjectBuilder {
	private AIXChildProject aixChildExtension = null;
	private Path outputDir;
	private Set<Path> assetsPaths = Sets.newHashSet();
	private Set<Path> librariesPaths = Sets.newHashSet();
	private Set<Path> aiwebresPaths = Sets.newHashSet();
	private Set<Path> jniDirPaths = Sets.newHashSet();
	private Path androidRuntimeJar;

	/**
	 * @param aixChildExtension
	 *            aix子工程对象
	 * @param androidRuntimeJar
	 *            AndroidRuntime.jar文件，一般是工程项目中用户编写的所有代码
	 * @param outputDir
	 *            aix编译出来释放的目录，一般该目录下有一个组件包的components.json,files目录等
	 */
	public AIXChildProjectBuilder(AIXChildProject aixChildExtension,
			Path androidRuntimeJar, Path outputDir) {
		this.aixChildExtension = aixChildExtension;
		this.outputDir = outputDir;
		this.androidRuntimeJar = androidRuntimeJar;
	}

	private void buildDexFile() {
		Logger.log("从AndroidRuntime.jar及所需依赖生成classes.dex文件并打包为classes.jar");
		D8Builder d8Builder = new D8Builder();
		//d8Builder.setMinSdk(24);
		d8Builder.addClassPaths(outputDir.forward("files")
				.listAllChildrenBySuffix(".jar"));
		d8Builder.setOutputPath(outputDir);
		d8Builder.start();
		try {
			Path dex = outputDir.forward("classes.dex");
			ZipUtils.pack(outputDir.forward("classes.jar"), dex);
			dex.delete();

		} catch (ZipException e) {
			Logger.err("dx阶段发生异常");
			e.printStackTrace();
		}
	}

	public void build() {
		Logger.log("开始构建组件包" + aixChildExtension.getPackageName());
		buildComponentsJson();
		buildComponentBuildInfosJson();
		copyJnis();
		copyAssets();
		copyAiwebreses();
		copyLibraries();
		buildDexFile();
	}

	public Map<String, String> generateMarkdowns() {
		return aixChildExtension.generateMarkdowns();
	}

	public Map<String,String> generateXmls(){
		return aixChildExtension.generateXmls();
	}
	
	private Path buildComponentsJson() {
		Logger.log("正在构建components.js");
		try {
			return outputDir.forward("components.json").appendText(
					aixChildExtension.generateComponentsInfo());
		} catch (IOException e) {
			Logger.err("写入components.json发生异常");
			e.printStackTrace();
		}
		return null;
	}

	private Path buildComponentBuildInfosJson() {
		Logger.log("正在构建component_build_infos.json");
		try {
			return outputDir.forward("files","component_build_infos.json").appendText(
					aixChildExtension.generateComponentsBuildInfo());
		} catch (IOException e) {
			Logger.err("写入component_build_infos.json发生异常");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 从jni文件池中按需打包进aix
	 */
	private void copyJnis() {
		Logger.log("开始复制jni");
		Set<NativeLibrary> nativeLibraries = aixChildExtension
				.getNativeLibraries(jniDirPaths);
		for (NativeLibrary nativeLibrary : nativeLibraries) {
			try {
				nativeLibrary.libFile.copyTo(outputDir.forward("jni",
						nativeLibrary.architecture,
						nativeLibrary.libFile.getName()));
			} catch (IOException e) {
				Logger.log("复制Jni库文件是发生异常,相关文件资源：" + nativeLibrary);
				e.printStackTrace();
			}
		}
	}

	/**
	 * 从aiwebres资源池中按需打包网页资源文件
	 */
	private void copyAiwebreses() {
		Logger.log("开始复制网页资源");
		Set<Path> aiwebreses = aixChildExtension.getAiWebreses(aiwebresPaths);
		Path targetDir = outputDir.forward("aiwebres");
		for (Path aiwebres : aiwebreses) {
			try {
				aiwebres.copyTo(targetDir.forward(aiwebres.getName()));
			} catch (IOException e) {
				Logger.log("复制web资源文件是发生异常,相关文件资源：" + aiwebres);
				e.printStackTrace();
			}
		}
	}

	/**
	 * 从jar库文件池中按需打包进aix
	 */
	private void copyLibraries() {
		Logger.log("开始复制依赖的jar库文件");
		Set<Path> libraries = aixChildExtension.getLibraries(librariesPaths);
		Path targetDir = outputDir.forward("files");
		for (Path library : libraries) {
			try {
				library.copyTo(targetDir.forward(library.getName()));
			} catch (IOException e) {
				Logger.log("复制jar库文件发生异常,相关文件资源：" + library);
				e.printStackTrace();
			}
		}

		try {
			androidRuntimeJar.copyTo(targetDir.forward(androidRuntimeJar
					.getName()));
		} catch (IOException e) {
			Logger.log("复制AndroidRuntime.jar时发生异常,相关文件资源：" + androidRuntimeJar);
			e.printStackTrace();
		}
	}

	/**
	 * 从资源文件池中按需打包进aix
	 */
	private void copyAssets() {
		Logger.log("开始复制资源文件");
		// assets资源文件应当放到 扩展包名/assets文件夹下
		Set<Path> assets = aixChildExtension.getAssets(assetsPaths);
		Path targetDir = outputDir.forward("assets");
		for (Path asset : assets) {
			try {
				asset.copyTo(targetDir.forward(asset.getName()));
			} catch (IOException e) {
				Logger.log("复制资源文件是发生异常,相关文件资源：" + asset);
				e.printStackTrace();
			}
		}
	}

	// 将要打包的各种文件组放入列表，调用如下几个add方法（放入不一定打包，需要在aix源代码中使用相关注解声明）

	public void addAiwebreses(Set<Path> webreses) {
		for (Path webres : webreses) {
			aiwebresPaths.add(webres);
		}
	}

	public void addJniDirs(Set<Path> jniDirs) {
		for (Path jniDir : jniDirs) {
			jniDirPaths.add(jniDir);
		}
	}

	public void addLibraries(Set<Path> libraries) {
		for (Path library : libraries) {
			librariesPaths.add(library);
		}
	}

	public void addAssets(Set<Path> assets) {
		for (Path asset : assets) {
			assetsPaths.add(asset);
		}
	}

}
