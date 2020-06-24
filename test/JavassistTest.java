import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class JavassistTest {
	public static void main(String[] args) throws NotFoundException {
		ClassPool cp=ClassPool.getDefault();
		CtClass cc=cp.getCtClass(A.class.getName());
		for (CtMethod cm:cc.getDeclaredMethods()) {
			System.out.println(cm);
		}
	}
}

class A {
	public static void a() {

	}

	public void a(String a) {
		
	}
	public void b() {

	}

	public void c() {

	}
	
	public void assa() {
		
	}
}
