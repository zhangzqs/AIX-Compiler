package cn.zzq.aix.builder.descriptor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import cn.zzq.aix.builder.blocks.beans.MethodBean;
import cn.zzq.aix.builder.blocks.beans.ParameterBean;

public class Method extends Field {
	public Map<String, Parameter> parms = new LinkedHashMap<String, Parameter>();
	public String returnValue;
	private boolean hasReturn = false;

	public Method(Element element) {
		super(element);
		for (Element parmsEle : element.getElementsByTag("Parameter")) {
			Parameter p = new Parameter(parmsEle);
			parms.put(p.name, p);
		}
		Elements es = element.getElementsByTag("Return");
		hasReturn = !es.isEmpty();
		if (hasReturn)
			returnValue = es.html();
	}

	public Method(MethodBean mb) {
		super(mb);
		for (ParameterBean pb : mb.params) {
			parms.put(pb.name, new Parameter(pb));
		}
		hasReturn = mb.returnType != null;
		if (hasReturn)
			returnValue = mb.returnDescription;
	}

	@Override
	public Element toXmlElement() {
		Element element = super.toXmlElement("Method");
		parms.forEach((name, parm) -> {
			element.appendChild(parm.toXmlElement());
		});

		Element returnElement = getReturnElement();
		if (hasReturn)
			element.appendChild(returnElement);
		return element;
	}

	private Element getReturnElement() {
		if (!hasReturn) {
			return null;
		}
		Element returnEle = new Element("Return");
		returnEle.appendText(returnValue);
		return returnEle;
	}

}
