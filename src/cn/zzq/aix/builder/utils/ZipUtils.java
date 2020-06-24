package cn.zzq.aix.builder.utils;

import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class ZipUtils {

	/**
	 * @param zipFile
	 *            待解压的文件
	 * @param password
	 *            压缩文件的密码，如果无密码，可设为null或空字符串
	 * @param destinationPath
	 *            解压的目标目录
	 * @throws ZipException
	 */
	public static void unpack(Path zipFile, String password, Path destinationPath) throws ZipException {
		ZipFile zip = new ZipFile(zipFile);
		if (password != null) {
			zip.setPassword(password.toCharArray());
		}
		zip.extractAll(destinationPath.toString());
	}

	public static void pack(Path zipFile, Path... files) throws ZipException {

		ZipFile zip = new ZipFile(zipFile);
		for (Path path : files) {
			if (path.isDirectory()) {
				zip.addFolder(path);
			} else if (path.isFile()) {
				zip.addFile(path);
			}
		}

	}

}
