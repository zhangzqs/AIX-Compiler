package cn.zzq.aix.builder.doc;

import java.util.List;

import com.google.appinventor.components.runtime.collect.Lists;

import cn.zzq.aix.builder.blocks.BlockProperty;
import cn.zzq.aix.builder.blocks.Component;
import cn.zzq.aix.builder.blocks.beans.BlockPropertyBean;
import cn.zzq.aix.builder.blocks.beans.DesigablePropertyBean;
import cn.zzq.aix.builder.blocks.beans.EventBean;
import cn.zzq.aix.builder.blocks.beans.MethodBean;

public class ComponentDocumentBase {
	protected Component component;
	protected List<AI2Event> events = Lists.newArrayList();
	protected List<AI2Method> methods = Lists.newArrayList();
	protected List<AI2Property> properties = Lists.newArrayList();
	protected List<DesigablePropertyBean> desigablePropertyBeans = Lists.newArrayList();

	public ComponentDocumentBase(Component component) {
		this.component = component;
		for (MethodBean method : component.methods) {
			AI2Method ai2Method = new AI2Method(component, method);
			methods.add(ai2Method);
		}
		for (EventBean event : component.events) {
			AI2Event ai2Event = new AI2Event(component, event);
			events.add(ai2Event);
		}
		for (BlockPropertyBean property : component.blockProperties) {
			if (property.rw.equals(BlockProperty.READ_ONLY)) {
				AI2Property ai2Property = new AI2Property(component, property);
				ai2Property.getter = true;
				properties.add(ai2Property);
			} else if (property.rw.equals(BlockProperty.WRITE_ONLY)) {
				AI2Property ai2Property = new AI2Property(component, property);
				ai2Property.getter = false;
				properties.add(ai2Property);
			} else {
				AI2Property ai2Property1 = new AI2Property(component, property);
				ai2Property1.getter = true;
				properties.add(ai2Property1);
				AI2Property ai2Property2 = new AI2Property(component, property);
				ai2Property2.getter = false;
				properties.add(ai2Property2);
			}
		}

		desigablePropertyBeans = component.properties;
	}

}
