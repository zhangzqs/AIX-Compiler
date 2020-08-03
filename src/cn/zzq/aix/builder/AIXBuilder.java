package cn.zzq.aix.builder;

import java.io.IOException;

import cn.zzq.aix.builder.project.AIXProject;
import cn.zzq.aix.builder.utils.Logger;
import cn.zzq.aix.builder.utils.Path;

public class AIXBuilder {

	public static AIXProject activeProject = null;

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println(Helper.getBaseHelpString());
			System.out.println("当前AIX编译器版本为：v" + Version.VERSION);
			System.out.println(RuntimeEnvironment.getDirInfo());
			System.exit(0);
		}
		long beforeTime = System.currentTimeMillis();
		// 项目文件夹(测试用)
		// Path path = new Path().backward().forward("Scene3D", "scene3d-main",
		// "build.json");

		// 项目文件夹，Release正式发布用
		Path path = new Path(args[0]);
		if (path.exists()) {
			if (path.isFile()) {

				compileProcessor(initFromBuildInfo(BuildInfo.buildFromJson(path)));
			} else {
				compileProcessor(initByDefaultPath(path));
			}
		} else {
			System.err.println("没有这样的文件或文件夹：" + path);
		}
		long m = System.currentTimeMillis() - beforeTime;
		Logger.log("编译完成，累计用时" + m / 1000 + "s");

	}

	public static AIXProject initFromBuildInfo(BuildInfo buildInfo) {
		if (buildInfo.srcs.isEmpty() || buildInfo.output == null) {
			System.err.println("错误，请至少保证项目的build.json拥有src和output数据");
		}

		// 输出目录
		Path output = new Path(buildInfo.output);

		// 创建一个aix项目（项目源码，输出路径）
		AIXProject aixProject = new AIXProject(output, Path.createPathArray(buildInfo.srcs));

		aixProject.addLibraries(Path.createPathArray(buildInfo.libraries));

		// 添加资源目录
		aixProject.addWebreses(Path.createPathArray(buildInfo.aiwebres));

		aixProject.addAssets(Path.createPathArray(buildInfo.assets));

		aixProject.addJniDirs(Path.createPathArray(buildInfo.jniDirs));

		aixProject.addXmlDescriptors(Path.createPathArray(buildInfo.descriptorXmls));
		return aixProject;
	}

	public static AIXProject initByDefaultPath(Path projectPath) {

		// 项目源码
		Path src = projectPath.forward("src");
		// 输出目录
		Path output = projectPath.forward("build");
		// 创建一个aix项目（项目源码，输出路径）
		AIXProject aixProject = new AIXProject(output, src);
		// 添加lib/libs目录作为依赖目录
		Path lib = projectPath.forward("lib");
		Path libs = projectPath.forward("libs");
		if (lib.exists()) {
			aixProject.addLibraries(lib);
		}
		if (libs.exists()) {
			aixProject.addLibraries(libs);
		}
		// 添加资源目录
		Path aiwebresPath = projectPath.forward("aiwebres");
		Path assetsPath = projectPath.forward("assets");
		if (aiwebresPath.exists()) {
			aixProject.addWebreses(aiwebresPath);
		}
		if (assetsPath.exists()) {
			aixProject.addAssets(assetsPath);
		}
		Path jniPath = projectPath.forward("jni");
		if (jniPath.exists()) {
			aixProject.addJniDirs(jniPath);
		}
		Path descriptorPath = projectPath.forward("descriptors");
		if (descriptorPath.exists()) {
			aixProject.addXmlDescriptors(descriptorPath);
		}
		return aixProject;
	}

	public static void compileProcessor(AIXProject aixProject) {
		AIXBuilder.activeProject = aixProject;
		// 清空build目录的内容
		aixProject.clean();

		// 开始调用ecj编译器编译所有java代码
		aixProject.compileJava();

		// 开始编译kotlin代码
		// aixProject.compileKotlin();
		// 打包AndroidRuntime.jar
		aixProject.packJar();

		// 构建子扩展组件工程
		aixProject.buildAixChildProjects();

		// 打包所有组件
		aixProject.packAIX();

		// 输出日志信息
		try {
			aixProject.getBuildPath().forward("build.log").appendText(Logger.getLoggerText());
		} catch (IOException e) {
			Logger.log("输出日志信息发生异常");
			e.printStackTrace();
		}
	}
}
