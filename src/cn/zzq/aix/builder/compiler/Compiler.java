package cn.zzq.aix.builder.compiler;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import cn.zzq.aix.builder.utils.Path;
import cn.zzq.aix.builder.utils.Path.PathFilter;

public abstract class Compiler {
	// 类文件输出路径
	protected Path outputPath;
	// 源码路径
	protected Set<Path> sourcePath = new HashSet<Path>();
	// 依赖路径
	protected Set<Path> dependencies = new HashSet<Path>();
	// 源码水平，编译目标水平
	protected String sourceLevel = "1.8", targetLevel = "1.8";
	protected String encoding = "utf-8";
	public boolean saveParmsName;

	public void setEncding(String encoding) {
		this.encoding = encoding;
	}

	/**
	 * 设置编译器的输出路径
	 * 
	 * @param path 输出路径
	 */
	public void setOutputPath(Path outputPath) {
		this.outputPath = outputPath;
	}

	/**
	 * @param path 存放源代码的路径
	 */
	public void addSourcePath(Path... paths) {
		for (Path path : paths) {

			if (path.exists()) {
				sourcePath.add(path);
			} else {
				throw new RuntimeException("添加源码目录错误，不存在路径：" + path);
			}
		}
	}

	/**
	 * 添加ClassPath路径
	 * 
	 * @param path jar文件路径或者存放class文件的文件夹路径
	 */
	public void addClassPath(Path... paths) {
		for (Path path : paths) {
			if (path.exists()) {
				dependencies.add(path);
				Path[] jars = path.listAllChildren(new PathFilter() {
					@Override
					public boolean accept(Path path) {
						return path.getName().endsWith(".jar");
					}
				});
				dependencies.addAll(Arrays.asList(jars));
			} else {
				throw new RuntimeException("添加ClassPath错误，不存在文件或文件夹路径：" + path);
			}
		}
	}

	/**
	 * 设置编译器的编译等级 Compliance options: use 1.3 compliance (-source 1.3 -target 1.1)
	 * use 1.4 compliance (-source 1.3 -target 1.2) use 1.5 compliance (-source 1.5
	 * -target 1.5) use 1.6 compliance (-source 1.6 -target 1.6) use 1.7 compliance
	 * (-source 1.7 -target 1.7) use 1.8 compliance (-source 1.8 -target 1.8) set
	 * source level: 1.3 to 1.8 (or 5, 5.0, etc) set classfile target: 1.1 to 1.8
	 * (or 5, 5.0, etc) cldc1.1 can also be used to generate the StackMap attribute
	 */

	public void setCompileLevel(String sourceLevel, String targetLevel) {
		this.sourceLevel = sourceLevel;
		this.targetLevel = targetLevel;
	}

	public abstract void compile();
}
