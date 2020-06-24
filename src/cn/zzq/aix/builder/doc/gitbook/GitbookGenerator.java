package cn.zzq.aix.builder.doc.gitbook;

import cn.zzq.aix.builder.blocks.Component;
import cn.zzq.aix.builder.doc.AI2Event;
import cn.zzq.aix.builder.doc.AI2Method;
import cn.zzq.aix.builder.doc.AI2Property;
import cn.zzq.aix.builder.doc.ComponentDocumentBase;

public class GitbookGenerator extends ComponentDocumentBase {

	public GitbookGenerator(Component component) {
		super(component);
	}

	public String generatorMarkdown() {

		StringBuffer docBuffer = new StringBuffer();
		docBuffer.append("# " + component.name + "\n");
		docBuffer.append("---" + "\n\n");

		docBuffer.append("## 组件描述：" + "\n\n");
		docBuffer.append(component.helpString + "\n\n");

		docBuffer.append("## 事件" + "\n\n");
		for (AI2Event ai2Event : events) {
			docBuffer.append("* 事件描述：" + ai2Event.description + "\n");

			docBuffer.append("  " + getEventDeclare(ai2Event) + "\n\n");
		}

		docBuffer.append("## 方法" + "\n\n");
		for (AI2Method ai2method : methods) {
			docBuffer.append("* 方法描述：" + ai2method.description + "\n");

			docBuffer.append("  " + getMethodDeclare(ai2method) + "\n\n");
		}

		docBuffer.append("## 属性" + "\n\n");
		for (AI2Property ai2Property : properties) {
			docBuffer.append("* 属性描述：" + ai2Property.description + "\n\n");

			docBuffer.append("  " + getPropertyDeclare(ai2Property) + "\n\n");
		}
		return docBuffer.toString();
	}

	private static String getMethodDeclare(AI2Method ai2Method) {
		return "{% Ai2Method %}" + ai2Method + "{% endAi2Method %}";
	}

	private static String getEventDeclare(AI2Event ai2Event) {
		return "{% Ai2Event %}" + ai2Event + "{% endAi2Event %}";
	}

	private static String getPropertyDeclare(AI2Property ai2Property) {
		return "{% Ai2Property %}" + ai2Property + "{% endAi2Property %}";
	}

}
