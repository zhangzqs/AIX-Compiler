package cn.zzq.aix.builder.blocks;

import java.util.List;
import java.util.Map;

import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleProperty;
import com.google.appinventor.components.runtime.collect.Lists;
import com.google.appinventor.components.runtime.collect.Maps;

import cn.zzq.aix.builder.blocks.beans.BlockPropertyBean;
import cn.zzq.aix.builder.blocks.beans.DesigablePropertyBean;
import cn.zzq.aix.builder.blocks.beans.EventBean;
import cn.zzq.aix.builder.blocks.beans.MethodBean;
import cn.zzq.aix.builder.blocks.beans.ParameterBean;
import cn.zzq.aix.builder.utils.Logger;
import javassist.CtMethod;

/**
 * @author root 块分类器，用于输入所有的javaMethod并筛选出ai的方法，块属性，设计属性，事件对象
 */
public class BlockClassifier {
	// 先将所有的属性存起来
	private List<CtMethod> blockyPropertiesRaw = Lists.newArrayList();

	public List<BlockPropertyBean> blockProperties = Lists.newArrayList();
	public List<DesigablePropertyBean> desigableProperties = Lists.newArrayList();
	public List<EventBean> events = Lists.newArrayList();
	public List<MethodBean> methods = Lists.newArrayList();

	public BlockClassifier(CtMethod[] javaMethods) {
		try {
			for (CtMethod method : javaMethods) {
				if (method.getAnnotation(SimpleFunction.class) != null) {
					MethodBean mb = new Method(method);
					if (mb.userVisible)
						methods.add(mb);
				}
				if (method.getAnnotation(SimpleProperty.class) != null) {
					blockyPropertiesRaw.add(method);
				}
				if (method.getAnnotation(DesignerProperty.class) != null) {
					desigableProperties.add(new DesigableProperty(method));
				}
				if (method.getAnnotation(SimpleEvent.class) != null) {
					EventBean eb = new Event(method);
					events.add(eb);
				}
			}
			processProperties();
			printlnBlocky();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	private static String toMethodString(MethodBean mb) {
		String returnType = mb.returnType;
		StringBuffer sb = new StringBuffer();
		sb.append(mb.name + "(");
		if (mb.params.length != 0) {
			for (ParameterBean mp : mb.params) {
				sb.append(mp.name + ": " + mp.type + ", ");
			}
			sb.deleteCharAt(sb.length() - 2);
		}
		sb.append(")");
		if (returnType != null) {
			sb.append(": " + returnType);
		}
		return sb.toString();
	}

	private void printlnBlocky() {
		Logger.log("获取到事件：");
		for (EventBean e : events) {
			Logger.log(toMethodString(e), false);
		}
		Logger.log("获取到方法：");
		for (MethodBean m : methods) {

			Logger.log(toMethodString(m), false);
		}
		Logger.log("获取到属性：");
		for (BlockPropertyBean blockPropertyBean : blockProperties) {
			String msg = blockPropertyBean.name + ": " + blockPropertyBean.type + ' ' + "(" + blockPropertyBean.rw
					+ ")";
			Logger.log(msg, false);
		}
	}

	private void processProperties() {
		Map<String, List<CtMethod>> map = Maps.newHashMap();
		for (CtMethod blockPropertyRaw : blockyPropertiesRaw) {
			String name = blockPropertyRaw.getName();
			List<CtMethod> thisNameProperties = map.get(name);
			if (thisNameProperties == null) {
				thisNameProperties = Lists.newArrayList();
				map.put(name, thisNameProperties);
			}
			thisNameProperties.add(blockPropertyRaw);
		}

		for (String key : map.keySet()) {
			List<CtMethod> properties = map.get(key);
			BlockProperty blockProperty;
			if (properties.size() == 1) {
				blockProperty = new BlockProperty(properties.get(0));
			} else {
				blockProperty = new BlockProperty(new PropertyPair(properties.get(0), properties.get(1)));
			}
			if (blockProperty != null && blockProperty.userVisible) {
				blockProperties.add(blockProperty);
			}
		}

	}

}
