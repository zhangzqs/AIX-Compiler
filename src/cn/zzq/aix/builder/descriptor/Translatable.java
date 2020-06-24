package cn.zzq.aix.builder.descriptor;

import java.util.LinkedHashMap;
import java.util.Map;

import org.jsoup.nodes.Element;

public class Translatable {

	/**
	 * <pre>
	 * 创建一个字典翻译，用于多国语言翻译 
	 * key：lang(表示某个国家的某种语言)
	 * value：翻译后的内容
	 * </pre>
	 */
	public transient Map<String, String> dict = new LinkedHashMap<>();

	public Translatable(Element element) {
		element.attributes().forEach((attr) -> {
			if (!(attr.getKey().equals("name") || attr.getKey().equals("className"))) {
				dict.put(attr.getKey(), attr.getValue());
			}
		});
	}

	public Translatable() {
	}

	protected void appendTranslateToElement(Element e) {
		dict.forEach((lang, content) -> {
			e.attributes().put(lang, content);
		});
	}

}
