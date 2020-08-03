package cn.zzq.aix.builder.descriptor;

import org.jsoup.nodes.Element;

import cn.zzq.aix.builder.blocks.beans.BlockPropertyBean;

//为保持逻辑清晰，而建立此类
public class Property extends Field {

	public Property(Element element) {
		super(element);
	}

	public Property(BlockPropertyBean pb) {
		super(pb);
	}

	@Override
	public Element toXmlElement() {
		return super.toXmlElement("Property");
	}
}
