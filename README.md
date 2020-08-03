# AIX-Compiler
This is an independent appinventor extension compiler, aixc.


当前AIX编译器版本为：v0.21.2

用法1(一般用法)：aixc <aix项目目录>
将按照 以下项目结构编译AppInventor扩展组件
组件项目目录
  src
    <组件的所有java源代码>
  assets
    <组件的所有依赖资源>
  jni
    arm64-v8a
      <组件的依赖的arm64-v8a架构的so库>
    armeabi-v7a
      <组件的依赖的armeabi-v7a架构的so库>
    x86
      <组件的依赖的x86架构的so库>
    x64
      <组件的依赖的x64架构的so库>
  lib
    <组件的依赖的所有jar包>
  aiwebres
	<存放组件图标文件>
  descriptor
	<存放组件外部注释/翻译Xml文件>
	
用该方法编译完成后，将

用法2(高级用法)：aixc <build.json文件>
你需要在build.json中写入以下json构建信息,srcs，output为必填项
{
        "srcs":["./src"],
        "output":"./build",
        "descriptor":"./descriptor.xml",
        "assets":["./assets"],
        "aiwebres":["./aiwebres"],
        "jniDirs":["./jni"],
        "libraries":["./lib"]
}

以上json信息中.代表当前build.json所在的目录,也可以直接填写绝对路径