package cn.zzq.aix.builder.doc;

import com.google.gson.Gson;

import cn.zzq.aix.builder.blocks.beans.BlockBean;
import cn.zzq.aix.builder.blocks.beans.ComponentBean;

public class AI2BlockyBean {
	public String name;
	public String componentName;
	public transient String description = "该代码块没有描述内容。";

	public AI2BlockyBean(ComponentBean component, BlockBean blockBean) {
		this.name = blockBean.name;
		this.componentName = component.name;
		this.description = blockBean.description;
	}

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
