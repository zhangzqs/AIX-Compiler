package cn.zzq.aix.builder.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Collection;

import net.lingala.zip4j.exception.ZipException;

/**
 * @author zhang
 * 
 */
public class Path extends File {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 根据一个绝对路径创建一个Path对象
	 * 
	 * @param absPath
	 *            绝对路径
	 */
	public Path(String absPath) {
		super(absPath);
	}

	/**
	 * 根据File对象创建一个Path对象
	 * 
	 * @param file
	 *            File对象
	 */
	public Path(File file) {
		super(file.getAbsolutePath());
	}

	public Path() {
		this("");
	}

	public static Path createPathByPackageName(Path parentPath,
			String packageName) {
		return parentPath.forward(packageName.split("[.]"));
	}

	/**
	 * @param childFileNames
	 *            前往该路径下的指定子文件夹
	 * @return 返回该子文件夹的Path对象
	 */
	public Path forward(String... childFileNames) {
		StringBuilder sb = new StringBuilder();
		sb.append(getAbsolutePath());
		for (String childFileName : childFileNames) {
			sb.append(File.separator);
			sb.append(childFileName.replace("/", File.separator));
		}
		return new Path(sb.toString());
	}

	public Path[] listChildren() {
		return toPathArray(this.listFiles());
	}

	public interface PathFilter {
		public boolean accept(Path path);
	}

	public Path[] listAllChildrenBySuffix(final String suffix) {
		return listAllChildren(new PathFilter() {
			@Override
			public boolean accept(Path path) {
				return path.getName().endsWith(suffix);
			}
		});
	}

	/**
	 * 列出所有的文件
	 * 
	 * @return
	 */
	public Path[] listAllChildren() {
		return listAllChildren(new PathFilter() {
			@Override
			public boolean accept(Path path) {
				return true;
			}
		});
	}

	public Path[] listAllChildren(final PathFilter pathFilter) {
		if (this.isFile()) {
			return new Path[] { this };
		}

		FileTraverser traverser = new FileTraverser(this);
		if (pathFilter == null) {
			traverser.traverseFile("");
		} else {
			traverser.traverseFile(new FileFilter() {
				@Override
				public boolean accept(File pathname) {
					return pathFilter.accept(new Path(pathname));
				}
			});
		}
		return toPathArray(traverser.getResult());
	}

	/**
	 * 返回上一级文件夹
	 * 
	 * @return 上一级文件夹的Path对象
	 */
	public Path backward() {
		// StringBuilder sb = new StringBuilder();
		// String[] elements = pathString.toString().replace(File.separatorChar,
		// '
		// ').split(" ");
		// for (int i = 0; i < elements.length - 1; i++) {
		// String element = elements[i];
		// sb.append(element);
		// sb.append(File.separator);
		// }
		// sb.deleteCharAt(sb.length() - 1);
		// return new Path(sb.toString());
		return new Path(new File(getAbsolutePath()).getParentFile());
	}

	public void deleteAll() {

		Logger.log("正在删除：" + this);
		if (this.exists()) {
			deleteAllFiles(this);
		}

	}

	/**
	 * 删除一个文件，或一个目录下的所有文件
	 * 
	 * @param file
	 *            一个文件对象或目录
	 */
	private static void deleteAllFiles(File file) {

		// 只要file还存在就不断删除
		while (file.exists()) {
			if (file.isFile()) {
				// 如果是文件直接删除
				file.delete();
			} else if (file.isDirectory()) {
				if (file.list().length == 0) {
					// 如果是空目录直接删除
					file.delete();
				} else {
					// 如果是非空目录就遍历目录里面
					for (File file1 : file.listFiles()) {
						deleteAllFiles(file1);
					}
				}
			}
		}

	}

	public static Path createTempFile(String prefix, String suffix) {
		try {
			return new Path(File.createTempFile(prefix, suffix));
		} catch (IOException e) {
			Logger.err("创建临时文件发生IO异常" + prefix + suffix);
			e.printStackTrace();
		}
		return null;
	}

	public InputStream buildInputStream() throws IOException {
		return new FileInputStream(this);
	}

	public Path compressAsZip(Path filePath) throws ZipException {
		if(this.isDirectory()){
			ZipUtils.pack(filePath, this.listChildren());
		}else{
			ZipUtils.pack(filePath, this);
		}
		return filePath;
	}

	public byte[] readAsByte() throws IOException {
		InputStream fis = buildInputStream();
		byte[] bytes = new byte[(int) this.length()];
		fis.read(bytes);
		fis.close();
		return bytes;
	}

	public Path appendText(String text) throws IOException {
		if (!this.exists()) {
			this.backward().mkdirs();
		}
		FileOutputStream outputStream = new FileOutputStream(this);
		BufferedWriter bufferedWriter = new BufferedWriter(
				new OutputStreamWriter(outputStream, "UTF-8"));
		bufferedWriter.write(text);
		bufferedWriter.flush();
		bufferedWriter.close();
		outputStream.close();
		return this;
	}

	public String readAsText() {
		FileReader fr = null;
		BufferedReader br = null;
		StringBuffer sb = null;
		try {
			fr = new FileReader(this);
			br = new BufferedReader(fr);
			
			sb = new StringBuffer();
			String line = null;
			while ((line = br.readLine()) != null) {
				sb.append(line);
				sb.append("\n");
			}
			sb.deleteCharAt(sb.length() - 1);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (fr != null) {
					fr.close();
				}
				if (br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	public static Path[] toPathArray(File[] files) {
		Path[] paths = new Path[files.length];
		for (int i = 0; i < files.length; i++) {
			paths[i] = new Path(files[i]);
		}
		return paths;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	public static Path[] createPathArray(Collection<String> pathStrs) {
		return createPathArray(pathStrs.toArray(new String[pathStrs.size()]));
	}

	public static Path[] createPathArray(String... pathStrs) {
		Path[] path = new Path[pathStrs.length];
		for (int i = 0; i < path.length; i++) {
			path[i] = new Path(pathStrs[i]);
		}
		return path;
	}

	@Override
	public String toString() {
		try {
			return this.getCanonicalPath();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this.getAbsolutePath();
	}

	/**
	 * Copies the contents of one local file to another local file.
	 * 
	 * @param inputFileName
	 *            the name of the file to read to
	 * @param outputFileName
	 *            the name of the file to write to
	 * @return the URL for the local file
	 */
	public static String copyFile(String inputFileName, String outputFileName)
			throws IOException {
		InputStream in = new FileInputStream(inputFileName);
		try {
			return writeStreamToFile(in, outputFileName);
		} finally {
			in.close();
		}
	}

	/**
	 * Writes the contents from the given input stream to the given file.
	 * 
	 * @param in
	 *            the InputStream to read from
	 * @param outputFileName
	 *            the name of the file to write to
	 * @return the URL for the local file
	 */
	public static String writeStreamToFile(InputStream in, String outputFileName)
			throws IOException {
		File file = new File(outputFileName);

		// Create the parent directory.
		file.getParentFile().mkdirs();

		OutputStream out = new FileOutputStream(file);
		try {
			copy(in, out);

			// Return the URL to the output file.
			return file.toURI().toString();
		} finally {
			out.flush();
			out.close();
		}
	}

	private static void copy(InputStream in, OutputStream out)
			throws IOException {
		out = new BufferedOutputStream(out, 0x1000);
		in = new BufferedInputStream(in, 0x1000);

		// Copy the contents from the input stream to the output stream.
		while (true) {
			int b = in.read();
			if (b == -1) {
				break;
			}
			out.write(b);
		}
		out.flush();
	}

	public void copyTo(Path target) throws IOException {
		Logger.log("将 " + this + " 复制到 " + target);
		copy(this, target);
	}

	/**
	 * @param source
	 *            要复制的文件或目录
	 * @param target
	 *            复制到的目标文件或目录
	 * @throws IOException
	 */
	public static void copy(Path source, Path target) throws IOException {
		if (source.isFile()) {
			copyFile(source.toString(), target.toString());
		} else {
			// 遍历文件
			for (File file : source.listFiles()) {
				// 如果是一个目录
				if (file.isDirectory()) {
					// 创建要生成目录的绝对路径
					Path dirpath = target.forward(file.getName());
					// 创建一个目录
					dirpath.mkdirs();
					// 调用复制文件夹方法
					copy(new Path(file), dirpath);
				} else {
					// 获得的文件的绝对路径
					Path filepath = target.forward(file.getName());
					// 复制文件内容方法
					copyFile(file.getAbsolutePath(), filepath.toString()); // 将原文件的内容复制到新文件里来；
				}
			}
		}
	}

}
