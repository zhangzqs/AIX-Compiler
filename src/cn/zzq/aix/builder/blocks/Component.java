package cn.zzq.aix.builder.blocks;

import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleObject;

import cn.zzq.aix.builder.blocks.beans.ComponentBean;
import cn.zzq.aix.builder.exception.NotComponentClass;
import cn.zzq.aix.builder.utils.MethodTraverser;
import cn.zzq.aix.builder.utils.MethodTraverser.MethodFilter;
import javassist.CtClass;
import javassist.CtMethod;

public class Component extends ComponentBean {

	public Component(CtClass componentClass) throws NotComponentClass {
		try {
			DesignerComponent designerComponent = null;
			try {
				designerComponent = (DesignerComponent) componentClass.getAnnotation(DesignerComponent.class);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			if (designerComponent == null) {
				throw new NotComponentClass(componentClass, "不存在@DesignerComponent注解");
			} else {
				this.categoryString = designerComponent.category().getName().toUpperCase();
				this.dateBuilt = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(new Date());
				this.iconName = designerComponent.iconName();
				this.helpUrl = designerComponent.helpUrl();
				this.type = componentClass.getName();
				this.version = designerComponent.version() + "";
				this.name = componentClass.getSimpleName();

				this.helpString = designerComponent.description();

				this.androidMinSdk = designerComponent.androidMinSdk();
				this.nonVisible = Boolean.toString(designerComponent.nonVisible());
				// this.description = designerComponent.description();

				SimpleObject simpleObject = (SimpleObject) componentClass.getAnnotation(SimpleObject.class);
				if (simpleObject != null) {
					external = Boolean.toString(simpleObject.external());
				}

				showOnPalette = Boolean.toString(designerComponent.showOnPalette());
				// 以下将获取所有的public方法
				MethodTraverser mt = new MethodTraverser(componentClass, new MethodFilter() {

					@Override
					public boolean accept(CtClass cc, CtMethod cm) {
						return Modifier.isPublic(cm.getModifiers());
					}
				});

				// 以下将分类
				BlockClassifier blockClassifier = new BlockClassifier(mt.getResult());
				this.methods = blockClassifier.methods;
				this.events = blockClassifier.events;
				this.blockProperties = blockClassifier.blockProperties;
				this.properties = blockClassifier.desigableProperties;
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

	}

}
