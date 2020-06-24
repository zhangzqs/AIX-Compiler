package cn.zzq.aix.builder.doc;

import cn.zzq.aix.builder.blocks.BlockUtil;
import cn.zzq.aix.builder.blocks.beans.ComponentBean;
import cn.zzq.aix.builder.blocks.beans.MethodBean;


public class AI2Event extends AI2BlockyBean{

	public String[] param;
	
	public AI2Event(ComponentBean component, MethodBean eventBean) {
		super(component, eventBean);
		param=BlockUtil.getParameterNames(eventBean.params);
	}

	
}
