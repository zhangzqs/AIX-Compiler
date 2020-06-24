package cn.zzq.aix.builder;

import java.io.IOException;

import cn.zzq.aix.builder.project.AIXProject;
import cn.zzq.aix.builder.utils.Logger;
import cn.zzq.aix.builder.utils.Path;

public class AIXBuilder {

	public static AIXProject activeProject = null;

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("欢迎使用AppInventor扩展组件编译器");
			System.out.println("作者：Zhangzqs");
			System.out.println("作者邮箱：2428698039@qq.com");
			System.out.println("交流qq群：694521655");

			System.out.println("当前AIX编译器版本为：v" + Version.version + "\n");
			System.out.println("用法1(一般用法)：aixc <aix项目目录>");
			System.out.println("将按照 以下项目结构编译AppInventor扩展组件");
			System.out.println("组件项目目录");
			System.out.println("  src");
			System.out.println("    <组件的所有java源代码>");
			System.out.println("  assets");
			System.out.println("    <组件的所有依赖资源>");
			System.out.println("  jni");
			System.out.println("    arm64-v8a");
			System.out.println("      <组件的依赖的arm64-v8a架构的so库>");
			System.out.println("    armeabi-v7a");
			System.out.println("      <组件的依赖的armeabi-v7a架构的so库>");
			System.out.println("    x86");
			System.out.println("      <组件的依赖的x86架构的so库>");
			System.out.println("    x64");
			System.out.println("      <组件的依赖的x64架构的so库>");
			System.out.println("  lib");
			System.out.println("    <组件的依赖的所有jar包>");
			System.out.println("  libs");
			System.out.println("    <组件的依赖的所有jar包>");
			System.out.println("\n" + "用法2(高级用法)：aixc <build.json文件>");
			System.out.println("你需要在build.json中写入以下json构建信息,srcs，output为必填项");
			System.out.println("{");
			System.out.println("	\"srcs\":[\"./src\"],");
			System.out.println("	\"output\":\"./build\",");
			System.out.println("	\"descriptor\":\"./descriptor.xml\",");
			System.out.println("	\"assets\":[\"./assets\"],");
			System.out.println("	\"aiwebres\":[\"./aiwebres\"],");
			System.out.println("	\"jniDirs\":[\"./jni\"],");
			System.out.println("	\"libraries\":[\"./lib\"]");
			System.out.println("}");
			System.out.println("以上json信息中.代表当前build.json所在的目录,也可以直接填写绝对路径");

			Path cp = RuntimeEnvironment.RUNTIME_LIBRARY_DIR;
			System.out.println("当前AppInventor运行时环境目录：" + cp);

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
		if(descriptorPath.exists()) {
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
		//aixProject.compileKotlin();
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
