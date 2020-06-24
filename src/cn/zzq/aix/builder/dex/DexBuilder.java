package cn.zzq.aix.builder.dex;

import java.util.Set;

import com.google.appinventor.components.runtime.collect.Sets;

import cn.zzq.aix.builder.utils.CommandBuilder;
import cn.zzq.aix.builder.utils.Path;

public abstract class DexBuilder {
	CommandBuilder commandBuilder = new CommandBuilder();

	protected Path outputPath;

	/**
	 * 设置编译器的输出路径
	 * 
	 * @param path 输出路径
	 */
	public void setOutputPath(Path outputPath) {
		this.outputPath = outputPath;
		outputPath.mkdirs();
	}

	protected Set<Path> classPaths = Sets.newHashSet();

	/**
	 * 添加编译dex时的源文件
	 * 
	 * @param classPaths jar文件或class文件或文件夹
	 */
	public void addClassPaths(Path... classPaths) {
		for (Path path : classPaths) {
			if (path.exists()) {
				this.classPaths.add(path);
			} else {
				throw new RuntimeException("添加ClassPath错误，不存在 文件或文件夹路径：" + path);
			}
		}
	}

	// 设置最低sdk版本
	protected int minSdk = -1;

	public void setMinSdk(int sdk_int) {
		minSdk = sdk_int;
	}

	abstract public void start();

}
