package cn.zzq.aix.builder.blocks;

import com.google.appinventor.components.annotations.SimpleEvent;

import cn.zzq.aix.builder.blocks.beans.EventBean;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class Event extends EventBean {
	public Event(CtMethod method) {
		try {
			boolean deprecated = method.getAnnotation(Deprecated.class) != null;
			this.deprecated = Boolean.toString(deprecated);

			this.name = method.getName();

			SimpleEvent simpleFunction = (SimpleEvent) method.getAnnotation(SimpleEvent.class);
			this.description = simpleFunction.description().isEmpty() ? "Here is the description of " + name + " event."
					: simpleFunction.description();
			this.userVisible = simpleFunction.userVisible();
			this.params = Parameter.getMethodParameters(method);

			if (method.getReturnType() != CtClass.voidType) {
				this.returnType = BlockUtil.javaTypeToYailType(method.getReturnType());
			}
		} catch (ClassNotFoundException | NotFoundException e) {
			e.printStackTrace();
		}
	}

}
