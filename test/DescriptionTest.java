import cn.zzq.aix.builder.AIXBuilder;
import cn.zzq.aix.builder.utils.Path;

public class DescriptionTest {
	public static void main(String[] args) {
		Path pj = new Path().backward().forward("AIX-BitmapHandler");
		AIXBuilder.main(new String[] { pj.toString() });
	}
}
