package cn.zzq.aix.builder;

import cn.zzq.aix.builder.utils.AssetsManager;

public class Helper {
	public static String getBaseHelpString() {
		return AssetsManager //
				.getAssetsManager() //
				.getAssets("base_use.txt") //
				.readAsText(); //
	}
}
