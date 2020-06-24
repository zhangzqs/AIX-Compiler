import cn.zzq.aix.builder.AIXBuilder;
import cn.zzq.aix.builder.utils.Path;

public class Test {
	public static void main(String[] args) {
		Path pj = new Path().backward().forward("Scene3D","scene3d-main");
		AIXBuilder.main(new String[] { pj.toString() });
	}
}
