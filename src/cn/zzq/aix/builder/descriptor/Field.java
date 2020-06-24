package cn.zzq.aix.builder.descriptor;

import org.jsoup.nodes.Element;

import com.google.gson.Gson;

import cn.zzq.aix.builder.blocks.beans.BlockBean;

/**
 * @author Zhiqiang 字段类，比如一个属性，一个方法参数，方法名，均为字段
 */
public abstract class Field extends Translatable {
	public String name;// 块名称

	public String description;// 块描述

	public Field(Element element) {
		super(element);
		name = element.attr("name");

		description=element.ownText();
	}

	public Field(BlockBean bb) {
		super();
		this.name = bb.name;
		this.description = bb.description;
		this.dict = bb.dict;
	}

	@Override
	public String toString() {
		return new Gson().toJson(this);
	}

	public abstract Element toXmlElement();

	protected Element toXmlElement(String tag) {
		Element element = new Element(tag);
		element.attributes().put("name", name);
		appendTranslateToElement(element);
		element.append(description);
		return element;
	}
}
