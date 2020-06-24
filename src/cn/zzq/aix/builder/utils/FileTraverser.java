package cn.zzq.aix.builder.utils;

import java.io.File;
import java.io.FileFilter;
import java.util.HashSet;


/**
 * @author Zhangzqs
 *
 */
public class FileTraverser {
	private File rootDirectory;
	private File tempDirectory;
	private HashSet<File> files = new HashSet<File>();
	private long length=0;
	/**
	 * 使用File对象作为根目录初始化遍历器
	 * @param rootDorectory
	 */
	public FileTraverser(File rootDirectory) {
		this.rootDirectory=rootDirectory;
		this.tempDirectory=rootDirectory;
	}
	
	/**
	 * 获取遍历所有文件的结果
	 * @return 遍历结果的File数组
	 */
	public File[] getResult() {
		return files.toArray(new File[files.size()]);
	}
	
	public long getLength() {
		return length;
	}
	/**
	 * 获得初始化本遍历器时的根目录
	 * @return 根目录的Path对象
	 */
	public Path getRootDirectory() {
		return new Path(rootDirectory);
	}
	
	
	/**
	 * 根据文件格式筛选文件
	 * @param suffix 文件后缀，如.java .class class java等
	 */
	public void traverseFile(final String suffix) {
		traverseFile(new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(suffix);
			}
		});
	}
	
	/**
	 * 启动遍历器，开始遍历文件，将会按照文件过滤器筛选所有的文件
	 * @param fileFilters 文件过滤器
	 */
	public void traverseFile(FileFilter... fileFilters) {
		for (File file : tempDirectory.listFiles()) {
			if (file.isFile()) {
				boolean a=true;
				for (FileFilter fileFilter : fileFilters) {
					a&=fileFilter.accept(file);
				}
				if(a) {
					files.add(file);
					length+=file.length();
				}
			} else if(file.isDirectory()){
				tempDirectory=file;
				this.traverseFile(fileFilters);
			}
		}
	}
	

}
