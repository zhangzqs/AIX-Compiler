package cn.zzq.aix.builder;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.parser.XmlTreeBuilder;

import com.google.appinventor.components.runtime.collect.Maps;

import cn.zzq.aix.builder.blocks.beans.ComponentBean;
import cn.zzq.aix.builder.blocks.beans.ParameterBean;
import cn.zzq.aix.builder.descriptor.Component;
import cn.zzq.aix.builder.descriptor.Event;
import cn.zzq.aix.builder.descriptor.Method;
import cn.zzq.aix.builder.descriptor.Parameter;
import cn.zzq.aix.builder.descriptor.Property;

public class DescriptorManager {
	// 单例模式，将自身实例化对象设置为一个属性，并用static修饰
	private volatile static DescriptorManager instance = null;

	Map<String, Component> descriptorMap = Maps.newHashMap();

	public void addXmlDescriptor(Element element) {
		Component descriptor = new Component(element);
		descriptorMap.put(descriptor.className, descriptor);
	}

	public void addXmlDescriptor(Document doc) {

		doc.getElementsByTag("Component").forEach((ele) -> {
			addXmlDescriptor(ele);
		});
		// System.out.println(doc);
	}

	public void addXmlDescriptor(String xmlText) {
		addXmlDescriptor(Jsoup.parse(xmlText, "", new Parser(new XmlTreeBuilder())));
	}

	public void process(ComponentBean cb) {
		String className = cb.type;
		if (!descriptorMap.containsKey(className)) {
			return;
		}
		// 此时对于每个组件都有一个xml文件
		Component descriptor = descriptorMap.get(className);
		if (descriptor.helpString != null && !descriptor.helpString.isEmpty()) {
			cb.helpString = descriptor.helpString;
		}
		// 事件注释替换
		cb.events.forEach((eb) -> {
			// eb为全组件所有的事件bean对象
			String eventName = eb.name;
			if (descriptor.events.containsKey(eventName)) {
				// 此时必定不为null
				Event e = descriptor.events.get(eventName);
				eb.dict = e.dict;
				eb.description = e.description;
				for (ParameterBean pb : eb.params) {
					String parmName = pb.name;
					if (e.parms.containsKey(parmName)) {
						// 此时parm必定不为null
						Parameter parm = e.parms.get(parmName);
						pb.dict = e.dict;
						pb.description = parm.description;
					}
				}
				// 做注释的后期处理
				eb.description = eb.getDescription();
			}
		});
		// 方法注释替换
		cb.methods.forEach((mb) -> {
			String methodName = mb.name;
			if (descriptor.methods.containsKey(methodName)) {
				// 此时必定不为null
				Method e = descriptor.methods.get(methodName);
				mb.description = e.description;
				mb.dict = e.dict;
				mb.returnDescription = e.returnValue;
				for (ParameterBean pb : mb.params) {
					String parmName = pb.name;
					if (e.parms.containsKey(parmName)) {
						// 此时parm必定不为null
						Parameter parm = e.parms.get(parmName);
						pb.description = parm.description;
						pb.dict = parm.dict;
					}
				}
				// 做注释的后期处理
				mb.description = mb.getDescription();
			}
		});
		// 属性注释替换
		cb.blockProperties.forEach((pb) -> {
			String pbName = pb.name;
			if (descriptor.properties.containsKey(pbName)) {
				Property p = descriptor.properties.get(pbName);
				pb.description = p.description;
				pb.dict = p.dict;
			}
		});
	}

	private DescriptorManager() {
	}

	public static DescriptorManager getDescriptorManager() {
		// 第一次检查是否实例化，如果没有进入if
		if (instance == null) {
			synchronized (DescriptorManager.class) {
				// 由某个线程成功取得了类的锁，实例化对象前再次检查instance是否被实例化
				if (instance == null) {
					instance = new DescriptorManager();
				}
			}
		}
		return instance;
	}
}
