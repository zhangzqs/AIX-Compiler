package cn.zzq.aix.builder.descriptor;

import org.jsoup.nodes.Element;

import cn.zzq.aix.builder.blocks.beans.ParameterBean;

public class Parameter extends Field {
	public Parameter(Element element) {
		super(element);
	}

	public Parameter(ParameterBean pb) {
		super(pb);
	}

	public Element toXmlElement() {
		return super.toXmlElement("Parameter");
	}
}