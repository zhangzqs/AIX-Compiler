package cn.zzq.aix.builder.descriptor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.nodes.Element;

import cn.zzq.aix.builder.blocks.beans.ComponentBean;

public class Component extends Translatable {
	public String className;

	public String helpString;
	public Map<String, Event> events = new LinkedHashMap<>();
	public Map<String, Method> methods = new LinkedHashMap<>();
	public Map<String, Property> properties = new LinkedHashMap<>();

	public Component(Element element) {
		super(element);
		className = element.attr("className");
		helpString = element.getElementsByTag("HelpString").html();
		element.getElementsByTag("Event").forEach((e) -> {
			Event event = new Event(e);
			events.put(event.name, event);
		});
		element.getElementsByTag("Method").forEach((e) -> {
			Method method = new Method(e);
			methods.put(method.name, method);
		});
		element.getElementsByTag("Property").forEach((e) -> {
			Property property = new Property(e);
			properties.put(property.name, property);
		});
	}

	public Component(ComponentBean cb) {
		super();
		className = cb.type;
		helpString = cb.helpString;
		cb.events.forEach((eb) -> {
			String ebName = eb.name;
			Event e = new Event(eb);
			events.put(ebName, e);
		});
		cb.methods.forEach((mb) -> {
			String mbName = mb.name;
			Method m = new Method(mb);
			methods.put(mbName, m);
		});
		cb.blockProperties.forEach((pb) -> {
			String pbName = pb.name;
			Property p = new Property(pb);
			properties.put(pbName, p);
		});
	}

	private Element getHelpStringElement() {
		Element e = new Element("HelpString");
		e.append(helpString);
		return e;
	}

	public Element toXmlElement() {
		Element e = new Element("Component");
		e.attributes().put("className", className);
		appendTranslateToElement(e);
		e.appendChild(getHelpStringElement());
		events.values().forEach((event) -> {
			e.appendChild(event.toXmlElement());
		});
		methods.values().forEach((method) -> {
			e.appendChild(method.toXmlElement());
		});
		properties.values().forEach((property) -> {
			e.appendChild(property.toXmlElement());
		});
		return e;
	}
}
