package cn.zzq.aix.builder;

import java.util.List;

import com.google.appinventor.components.runtime.collect.Lists;
import com.google.gson.Gson;

import cn.zzq.aix.builder.utils.Path;

public class BuildInfo {
	public transient Path buildJsonParentPath;
	public String output = null;
	public List<String> descriptorXmls = Lists.newArrayList();
	public List<String> srcs = Lists.newArrayList();
	public List<String> libraries = Lists.newArrayList();
	public List<String> aiwebres = Lists.newArrayList();
	public List<String> assets = Lists.newArrayList();
	public List<String> jniDirs = Lists.newArrayList();

	// build.json所需的依赖但是不会打包进组件
//	public class Dependence {
//		public List<String> srcs = Lists.newArrayList();
//		public List<String> libraries = Lists.newArrayList();
//	}

	// public Dependence dependences = null;

	public static BuildInfo buildFromJson(Path jsonPath) {
		BuildInfo buildInfo = new Gson().fromJson(jsonPath.readAsText(), BuildInfo.class);
		buildInfo.buildJsonParentPath = jsonPath.backward();
		buildInfo.init();
		return buildInfo;
	}

	private void replaceOffsetDir(List<String> strs) {
		if (!strs.isEmpty()) {
			// strs.stream().map(str -> buildFullPath(str));
			for (int i = 0; i < strs.size(); i++) {
				String str = strs.get(i);
				strs.set(i, buildFullPath(str));
			}
		}
	}

	public void init() {
		if (output != null) {
			output = buildFullPath(output);
		}
		replaceOffsetDir(descriptorXmls);
		replaceOffsetDir(srcs);
		replaceOffsetDir(aiwebres);
		replaceOffsetDir(assets);
		replaceOffsetDir(jniDirs);
		replaceOffsetDir(libraries);
//		dependences.stream().forEach((dependence) -> {
//			dependence.srcs.stream().map(src -> buildFullPath(src));
//			dependence.libraries.stream().map(library -> buildFullPath(library));
//		});
	}

	public String buildFullPath(String offsetPath) {
		Path p = buildJsonParentPath.forward(offsetPath.substring(2));
		System.out.println(p);
		return p.toString();
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}
}
