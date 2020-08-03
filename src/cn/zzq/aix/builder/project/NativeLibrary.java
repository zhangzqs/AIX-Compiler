package cn.zzq.aix.builder.project;

import cn.zzq.aix.builder.buildinfo.ComponentBuildInfo;
import cn.zzq.aix.builder.utils.Path;

public class NativeLibrary {
	public static final String X86 = "x86";
	public static final String X86_64 = "x86_64";
	public static final String ARM64_V8A = "arm64-v8a";
	public static final String ARMEABI_V7A = "armeabi-v7a";
	public String architecture;

	public String getArchitecture() {
		return architecture;
	}

	public Path getLibFile() {
		return libFile;
	}

	public Path libFile;

	public NativeLibrary(String architecture, Path libFile) {
		this.architecture = architecture;
		this.libFile = libFile;
	}

	/**
	 * 由ai的jni库的存放路径和native库名来创建一个Native库对象
	 * 
	 * @param jniDir            存放jni的目录
	 * @param nativeLibraryName native库的名称 一般为 so文件名称-架构后缀
	 *                          所构成，比如libhello.so-v7a,表示从jni的armeabi-v7a目录查找
	 */
	public static NativeLibrary getNativeLibrary(Path jniDir, String nativeLibraryName) {
		String architecture = getArchitectureByName(nativeLibraryName);
		String soFileName = nativeLibraryName.substring(0, nativeLibraryName.length() - 4);// 消除-x86,-64,-v8a,-v7a后缀
		Path libFile = jniDir.forward(architecture, soFileName);
		if (!libFile.exists()) {
			return null;
		}
		return new NativeLibrary(architecture, libFile);
	}

	public String getSimpleArchitecture() {
		return getArchitectSuffixByFullName(architecture);
	}

	@Override
	public String toString() {
		return "Architecture:" + architecture + "; File:" + libFile + ";";
	}

	private static String getArchitectSuffixByFullName(String fullName) {
		if (fullName.equals(X86)) {
			return ComponentBuildInfo.X86_SUFFIX;
		} else if (fullName.equals(X86_64)) {
			return ComponentBuildInfo.X86_64_SUFFIX;
		} else if (fullName.equals(ARM64_V8A)) {
			return ComponentBuildInfo.ARM64_V8A_SUFFIX;
		} else if (fullName.equals(ARMEABI_V7A)) {
			return ComponentBuildInfo.ARMEABI_V7A_SUFFIX;
		}
		return null;
	}

	private static String getArchitectureByName(String nativeLibraryName) {
		if (nativeLibraryName.endsWith(ComponentBuildInfo.X86_SUFFIX)) {
			return X86;
		} else if (nativeLibraryName.endsWith(ComponentBuildInfo.X86_64_SUFFIX)) {
			return X86_64;
		} else if (nativeLibraryName.endsWith(ComponentBuildInfo.ARMEABI_V7A_SUFFIX)) {
			return ARMEABI_V7A;
		} else if (nativeLibraryName.endsWith(ComponentBuildInfo.ARM64_V8A_SUFFIX)) {
			return ARM64_V8A;
		}
		return null;
	}
}
