package cn.zzq.aix.builder.descriptor;

import org.jsoup.nodes.Element;

import cn.zzq.aix.builder.blocks.beans.EventBean;

//无意义,但是要保持类结构清晰
public class Event extends Method {

	public Event(Element element) {
		super(element);
	}

	public Event(EventBean eb) {
		super(eb);
	}

	@Override
	public Element toXmlElement() {
		Element element = toXmlElement("Event");
		parms.forEach((name,parm) -> {
			element.appendChild(parm.toXmlElement());
		});
		return element;
	}
}
