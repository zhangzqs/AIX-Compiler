package cn.zzq.aix.builder.dex;

//
//import com.android.dx.command.Main;
//
//import cn.zzq.aix.builder.utils.Logger;
//import cn.zzq.aix.builder.utils.Path;
//
public class DxBuilder extends DexBuilder {
//
//	/**
//	 * 构建设置编译器输出路径的命令行
//	 * 
//	 * @return 设置编译器输出路径的命令行
//	 */
//	private String buildOutputPathCommand() {
//		StringBuilder sb = new StringBuilder();
//		sb.append("--output ");
//		sb.append(outputPath);
//		return sb.toString();
//	}
//
//	private String buildMinSdkCommand() {
//		if (minSdk < 0) {
//			return "";
//		}
//		return "--min-sdk-version " + minSdk;
//	}
//
//	private String buildClassPathCommand() {
//		StringBuffer result = new StringBuffer();
//		if (libraries.isEmpty()) {
//			result.append("");
//		} else {
//			for (Path path : libraries) {
//				result.append(path.toString());
//				result.append(" ");
//			}
//		}
//
//		if (classPaths.isEmpty()) {
//			result.append("");
//		} else {
//			for (Path path : classPaths) {
//				result.append(path.toString());
//				result.append(" ");
//			}
//		}
//		if (result.length() != 0) {
//			result.deleteCharAt(result.length() - 1);
//		}
//		return result.toString();
//	}
//
//	private String buildCommand() {
//		StringBuffer sb = new StringBuffer("--dex ");
//		sb.append(buildOutputPathCommand() + ' ');
//		sb.append(buildMinSdkCommand() + ' ');
//		sb.append(buildClassPathCommand());
//		return sb.toString();
//	}
//
	@Override
	public void start() {
//		outputPath.mkdirs();
//		String command = buildCommand();
//		Logger.log("执行的dx命令 " + command);
//		String[] commands = command.toString().split(" ");
//		Main.main(commands);
	}
}
