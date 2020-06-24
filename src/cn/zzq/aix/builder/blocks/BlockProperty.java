package cn.zzq.aix.builder.blocks;

import com.google.appinventor.components.annotations.SimpleProperty;

import cn.zzq.aix.builder.blocks.beans.BlockPropertyBean;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class BlockProperty extends BlockPropertyBean {
	public static final String READ_WRITE = "read-write";
	public static final String READ_ONLY = "read-only";
	public static final String WRITE_ONLY = "write-only";

	public BlockProperty(CtMethod method) {
		try {
			// DesignerProperty designerProperty = (DesignerProperty)
			// method.getAnnotation(DesignerProperty.class);
			// if (designerProperty != null) {
			// this.defaultValue = designerProperty.defaultValue();
			// this.alwaysSend = Boolean.toString(designerProperty.alwaysSend());
			// }

			// 判断是否是废弃的代码块
			this.deprecated = Boolean.toString(method.getAnnotation(Deprecated.class) != null);

			this.name = method.getName();

			SimpleProperty simpleProperty = (SimpleProperty) method.getAnnotation(SimpleProperty.class);

			this.description = simpleProperty.description();
			this.userVisible = simpleProperty.userVisible();
			if (method.getParameterTypes().length == 1 && method.getReturnType() == CtClass.voidType) {
				// 如果参数数目为1，且无返回值，那么就是可写
				this.rw = WRITE_ONLY;
				this.type = BlockUtil.javaTypeToYailType(method.getParameterTypes()[0]);
			} else if (method.getParameterTypes().length == 0 && method.getReturnType() != CtClass.voidType) {
				// 如果无参数，且有返回值，那么就是可读
				this.rw = READ_ONLY;
				this.type = BlockUtil.javaTypeToYailType(method.getReturnType());
			} else {
				throw new RuntimeException("构造AppInventor的Property方法" + method + "发生错误，参数数目或返回值异常");
			}
		} catch (ClassNotFoundException | NotFoundException e) {
			e.printStackTrace();
		}

	}

	public BlockProperty(PropertyPair pair) {
		if (pair.matchRw()) {
			this.name = pair.name;

			this.rw = READ_WRITE;

//			if (pair.designerProperty != null) {
//				this.defaultValue = pair.designerProperty.defaultValue();
//				this.alwaysSend = Boolean.toString(pair.designerProperty.alwaysSend());
//			}

			// 判断是否是废弃的代码块
			this.deprecated = Boolean.toString(pair.isDeprecated());

			this.type = pair.getType();

			this.description = pair.getDescription();

			this.userVisible = pair.userVisible();
		} else {
			throw new RuntimeException("请传入一对相同名称,类型对应,写属性为一个参数，读属性无参数,且均附带SimpleProperty注解的属性方法" + pair.name);
		}
	}
}
