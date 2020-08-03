package cn.zzq.aix.builder;

import java.io.File;

import cn.zzq.aix.builder.utils.Path;

public class RuntimeEnvironment {
	// 当前aixc安装目录
	public static Path HOME_DIR = new Path(System.getProperty("java.class.path").split(File.pathSeparator)[0])
			.backward();

	// 默认的RuntimeLibrary路径目录为与jar同目录下的runtime-library文件夹或eclipse项目的runtime-library文件夹
	public static Path RUNTIME_LIBRARY_DIR = HOME_DIR.forward("runtime-library");

	// aixc资源目录
	public static Path ASSET_DIR = HOME_DIR.forward("assets");

	// Kotlin运行库目录
	public static Path KOTLIN_HOME_DIR = RUNTIME_LIBRARY_DIR.backward().forward("kotlin-home");

	/**
	 * 获取各种目录的信息字符串
	 * 
	 * @return
	 */
	public static String getDirInfo() {
		StringBuffer sb = new StringBuffer();
		sb.append("当前AIXC安装目录：");
		sb.append(HOME_DIR + "\n");
		sb.append("当前使用的AppInventor运行时环境目录：");
		sb.append(RUNTIME_LIBRARY_DIR + "\n");
		sb.append("当前AIXC资源目录：");
		sb.append(ASSET_DIR);
		return sb.toString();
	}
}
