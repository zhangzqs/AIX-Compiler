package cn.zzq.aix.builder.blocks.beans;

import com.google.gson.Gson;

import cn.zzq.aix.builder.descriptor.Translatable;

public class BlockBean extends Translatable{
	public String name="";
	public transient String description="";
	public transient boolean userVisible=true;

	@Override
	public String toString() {
		Gson gson = new Gson();
		return gson.toJson(this);
	}
}
