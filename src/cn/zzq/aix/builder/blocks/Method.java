package cn.zzq.aix.builder.blocks;

import com.google.appinventor.components.annotations.SimpleFunction;

import cn.zzq.aix.builder.blocks.beans.MethodBean;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class Method extends MethodBean {

	public Method(CtMethod method) {
		try {
			boolean deprecated = method.getAnnotation(Deprecated.class) != null;
			this.deprecated = Boolean.toString(deprecated);

			this.name = method.getName();

			SimpleFunction simpleFunction = (SimpleFunction) method
					.getAnnotation(SimpleFunction.class);
			this.description = simpleFunction.description();
			this.userVisible=simpleFunction.userVisible();
			this.params = Parameter.getMethodParameters(method);

			
			if (method.getReturnType() != CtClass.voidType) {
				this.returnType = BlockUtil.javaTypeToYailType(method
						.getReturnType());
			}
		} catch (ClassNotFoundException | NotFoundException e) {
			e.printStackTrace();
		}
	}

}
