package cn.zzq.aix.builder.blocks.beans;

public class MethodBean extends BlockBean {
	public String description = "";
	public String deprecated;
	public ParameterBean[] params;
	public String returnType;

	// 仅用于生成返回值注释
	public transient String returnDescription = "";

	public String getDescription() {
		String tabs = "-   ";
		StringBuffer sb = new StringBuffer();
		sb.append(description + '\n');
		if (params != null && params.length != 0) {
			sb.append("参数说明：" + '\n');
			for (ParameterBean parameterBean : params) {
				String pd = parameterBean.name + ':' + parameterBean.type;
				pd += '\n' + tabs + parameterBean.description;
				sb.append(pd + '\n');
			}
		}
		if (returnType != null) {
			sb.append("返回值:" + returnType + '\n' + tabs + returnDescription);
		}
		// System.out.println(sb);
		return sb.toString();
	}
}
