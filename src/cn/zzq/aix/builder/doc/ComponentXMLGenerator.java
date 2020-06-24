package cn.zzq.aix.builder.doc;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Document.OutputSettings;
import org.jsoup.nodes.Document.OutputSettings.Syntax;
import org.jsoup.nodes.Element;

import cn.zzq.aix.builder.blocks.Component;

public class ComponentXMLGenerator extends ComponentDocumentBase {
	public ComponentXMLGenerator(Component component) {
		super(component);
	}

	public String generateXMLDescriptor() {
		Element e= new cn.zzq.aix.builder.descriptor.Component(component).toXmlElement();
		Document doc=new Document("");
		doc.appendChild(e);
		OutputSettings os=new OutputSettings();
		os.charset("utf-8");
		//os.prettyPrint(false);
		os.indentAmount(4);
		//os.escapeMode(EscapeMode.xhtml);
		os.syntax(Syntax.xml);
		doc.outputSettings(os);
		return doc.toString();
	}
}
