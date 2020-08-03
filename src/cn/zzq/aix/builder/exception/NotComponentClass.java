package cn.zzq.aix.builder.exception;

import javassist.CtClass;

public class NotComponentClass extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public NotComponentClass(CtClass class_) {
		this(class_, "");
	}

	public NotComponentClass(CtClass class_, String message) {
		super(class_.getName() + " 不是一个组件类" + "\n" + message);
	}
}
