package cn.zzq.aix.builder.doc;

import cn.zzq.aix.builder.blocks.beans.ComponentBean;
import cn.zzq.aix.builder.blocks.beans.MethodBean;


public class AI2Method extends AI2Event{
	
	public boolean output;
	public int margin_left=8;
	public AI2Method(ComponentBean component, MethodBean methodBean) {
		super(component, methodBean);
		output=methodBean.returnType!=null;
	}
	
}
