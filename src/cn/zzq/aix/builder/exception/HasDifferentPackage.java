package cn.zzq.aix.builder.exception;

/**
 * @author Zhiqiang 当一个aix包下存在不同类的组件时，产生该异常
 */
public class HasDifferentPackage extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public HasDifferentPackage(String packageName1, String packageName2) {
		super("含有如下不同的包名\n" + packageName1 + "\n" + packageName2 + "\n" + "不同类名的组件类不能存在于同一个aix子工程项目中");
	}

}