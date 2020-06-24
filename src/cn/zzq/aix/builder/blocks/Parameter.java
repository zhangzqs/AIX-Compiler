package cn.zzq.aix.builder.blocks;

import cn.zzq.aix.builder.blocks.beans.ParameterBean;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.Modifier;
import javassist.NotFoundException;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.LocalVariableAttribute;
import javassist.bytecode.MethodInfo;

public class Parameter extends ParameterBean{

	public Parameter(String name,CtClass type){
		this.name=name;
		this.type=BlockUtil.javaTypeToYailType(type);
	}
	
	//获取一个方法的所有参数对象
	public static Parameter[] getMethodParameters(CtMethod cm)
			throws NotFoundException {
		MethodInfo methodInfo = cm.getMethodInfo();
		CtClass[] parameterTypes = cm.getParameterTypes();
		CodeAttribute codeAttribute = methodInfo.getCodeAttribute();
		LocalVariableAttribute attr = (LocalVariableAttribute) codeAttribute
				.getAttribute(LocalVariableAttribute.tag);
		String[] paramNames = new String[parameterTypes.length];
		int pos = Modifier.isStatic(cm.getModifiers()) ? 0 : 1;
		Parameter[] parameters = new Parameter[parameterTypes.length];
		for (int i = 0; i < paramNames.length; i++) {
			CtClass cc = parameterTypes[i];
			//String className = cc.getName();
			String name = "arg" + i;
			if (attr != null) {
				name = attr.variableName(i + pos);
			}
			parameters[i] = new Parameter(name, cc);
		}
		return parameters;
	}
}
