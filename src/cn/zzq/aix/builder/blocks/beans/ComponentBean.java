package cn.zzq.aix.builder.blocks.beans;

import java.util.List;

import com.google.appinventor.components.runtime.collect.Lists;

public class ComponentBean extends BlockBean {
	public String categoryString, dateBuilt, iconName, helpUrl, type, version, helpString;
	public int androidMinSdk;
	public String nonVisible, external, showOnPalette;
	public List<EventBean> events = Lists.newArrayList();
	public List<MethodBean> methods = Lists.newArrayList();
	public List<BlockPropertyBean> blockProperties = Lists.newArrayList();
	public List<DesigablePropertyBean> properties = Lists.newArrayList();
}
