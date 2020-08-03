package cn.zzq.aix.builder.compiler;

//
//import java.io.File;
//import java.util.Arrays;
//
//import cn.zzq.aix.builder.RuntimeEnvironment;
//import cn.zzq.aix.builder.utils.CommandBuilder;
//import cn.zzq.aix.builder.utils.Logger;
//import cn.zzq.aix.builder.utils.Path;
//
public class KotlinCompiler extends Compiler {
//
//	public KotlinCompiler() {
//		super();
//		this.addClassPath(RuntimeEnvironment.KOTLIN_HOME_DIR);
//	}
//
//	// 编译结果是否包含kotlin的运行时库文件？
//	public boolean include_runtime = true;
//	public boolean no_jdk = true;
//
//	private CommandBuilder buildTargetClassLevelCommand(CommandBuilder commandBuilder) {
//		return commandBuilder.add("-jvm-target", targetLevel);
//	}
//
//	private CommandBuilder buildOutputCommand(CommandBuilder commandBuilder) {
//		return commandBuilder.add("-d", outputPath);
//	}
//
//	private CommandBuilder buildSourcePathCommand(CommandBuilder commandBuilder) {
//		return commandBuilder.add(sourcePath);
//	}
//
//	private CommandBuilder buildClassPathCommand(CommandBuilder commandBuilder) {
//		if (!dependencies.isEmpty()) {
//			commandBuilder.add("-classpath");
//			StringBuilder sb = new StringBuilder();
//			for (Path path : dependencies) {
//				sb.append(path.toString());
//				sb.append(File.pathSeparator);
//			}
//			return commandBuilder.add(sb);
//		}
//		return commandBuilder;
//	}
//
//	private CommandBuilder buildCommand(CommandBuilder commandBuilder) {
//		buildSourcePathCommand(commandBuilder);
//		buildClassPathCommand(commandBuilder);
//		buildTargetClassLevelCommand(commandBuilder);
//		if (include_runtime) {
//			commandBuilder.add("-include-runtime");
//		}
//		if (no_jdk) {
//			commandBuilder.add("-no-jdk");
//		}
//		if (saveParmsName) {
//			commandBuilder.add("-java-parameters");
//		}
//		commandBuilder.add("-kotlin-home", RuntimeEnvironment.KOTLIN_HOME_DIR);
//
//		buildOutputCommand(commandBuilder);
//		return commandBuilder;
//	}
//
	@Override
	public void compile() {
//		Logger.log("开始编译Kotlin代码");
//		CommandBuilder command = buildCommand(new CommandBuilder());
//		Arrays.asList(command.buildCommandArray()).forEach(System.out::println);
//		Logger.log("执行Kotlinc编译器命令:" + command);
//
//		org.jetbrains.kotlin.cli.jvm.K2JVMCompiler.main(command.buildCommandArray());
//		Logger.log("Kotlin代码编译成功");
	}
//
}
