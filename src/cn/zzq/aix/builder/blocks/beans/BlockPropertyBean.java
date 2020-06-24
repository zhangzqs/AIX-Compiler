package cn.zzq.aix.builder.blocks.beans;

import com.google.gson.Gson;

public class BlockPropertyBean extends BlockBean{
	public String rw,type;//,defaultValue;
	public String deprecated;
	//public String alwaysSend;
	public String description="";
	@Override
	public String toString() {
		Gson gson=new Gson();
		return gson.toJson(this, BlockPropertyBean.class);
	}
}
