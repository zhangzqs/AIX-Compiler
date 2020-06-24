package cn.zzq.aix.builder.blocks;

import com.google.appinventor.components.annotations.DesignerProperty;

import cn.zzq.aix.builder.blocks.beans.DesigablePropertyBean;
import javassist.CtMethod;

public class DesigableProperty extends DesigablePropertyBean {

	public DesigableProperty(CtMethod method) {
		try {
			DesignerProperty designerProperty = (DesignerProperty) method
					.getAnnotation(DesignerProperty.class);
			if (designerProperty == null) {
				throw new RuntimeException("构造AppInventor的设计视图的方法" + method
						+ "发生错误，不存在@DesignerProperty声明");
			} else {
				this.defaultValue = designerProperty.defaultValue();
				this.alwaysSend = designerProperty.alwaysSend();
				this.name = method.getName();
				this.editorArgs = designerProperty.editorArgs();
				this.editorType = designerProperty.editorType();
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}
