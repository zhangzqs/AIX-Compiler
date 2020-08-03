package cn.zzq.aix.builder.dex;

import java.util.Arrays;

import com.android.tools.r8.D8;

import cn.zzq.aix.builder.RuntimeEnvironment;
import cn.zzq.aix.builder.utils.Logger;

public class D8Builder extends DexBuilder {

	private void buildDependenciesPathCommand() {
//		commandBuilder.add("--classpath");
//		for (Path dependence : RuntimeEnvironment.RUNTIME_LIBRARY_DIR.listAllChildrenBySuffix(".jar")) {
//			if (!dependence.getName().endsWith("source.jar")) {
//				commandBuilder.add(dependence);
//			}
//		}
		commandBuilder.add("--lib", RuntimeEnvironment.RUNTIME_LIBRARY_DIR.forward("android.jar"));
		// commandBuilder.add(
		// RuntimeEnvironment.RUNTIME_LIBRARY_DIR.forward("appinventor-runtime.jar"));

	}

	/**
	 * 构建设置编译器输出路径的命令行
	 * 
	 * @return 设置编译器输出路径的命令行
	 */
	private void buildOutputPathCommand() {
		commandBuilder.add(Arrays.asList("--output", outputPath.toString()));
	}

	/**
	 * 构建最小sdk版本命令行，若为默认版本，则返回空列表
	 * 
	 * @return
	 */
	private void buildMinSdkCommand() {
		if (minSdk > 0) {
			commandBuilder.add(Arrays.asList("--min-api", Integer.toString(minSdk)));
		}
	}

	private void buildClassPathsCommand() {
		classPaths.forEach(commandBuilder::add);
	}

	private void buildCommand() {
		buildClassPathsCommand();
		commandBuilder.add("--no-desugaring");
		buildDependenciesPathCommand();
		buildMinSdkCommand();
		buildOutputPathCommand();
		// --no-desugaring
	}

	@Override
	public void start() {
		buildCommand();
		outputPath.mkdirs();
		String command = commandBuilder.toString();
		Logger.log("执行的d8命令 " + command);
		D8.main(commandBuilder.buildCommandArray());
	}

}
