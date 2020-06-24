package cn.zzq.aix.builder;

import java.io.File;

import cn.zzq.aix.builder.utils.Path;

public class RuntimeEnvironment {
	// 默认的RuntimeLibrary路径目录为与jar同目录下的runtime-library文件夹或eclipse项目的runtime-library文件夹
	public static Path RUNTIME_LIBRARY_DIR = new Path(System.getProperty(
			"java.class.path").split(File.pathSeparator)[0]).backward()
			.forward("runtime-library");
	public static Path KOTLIN_HOME_DIR=RUNTIME_LIBRARY_DIR.backward().forward("kotlin-home");
	
}
