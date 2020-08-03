package cn.zzq.aix.builder.utils;

import cn.zzq.aix.builder.DescriptorManager;
import cn.zzq.aix.builder.RuntimeEnvironment;

/**
 * @author Zhiqiang AIXC 资源管理器
 */
public class AssetsManager {
	// 单例模式，将自身实例化对象设置为一个属性，并用static修饰
	private volatile static AssetsManager instance = null;

	/**
	 * @param assetName 资源文件名
	 * @return 资源文件的Path对象
	 */
	public Path getAssets(String assetName) {
		return RuntimeEnvironment.ASSET_DIR.forward(assetName);
	}

	private AssetsManager() {
	}

	public static AssetsManager getAssetsManager() {
		// 第一次检查是否实例化，如果没有进入if
		if (instance == null) {
			synchronized (AssetsManager.class) {
				// 由某个线程成功取得了类的锁，实例化对象前再次检查instance是否被实例化
				if (instance == null) {
					instance = new AssetsManager();
				}
			}
		}
		return instance;
	}
}
