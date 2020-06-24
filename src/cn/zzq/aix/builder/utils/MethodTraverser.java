package cn.zzq.aix.builder.utils;

import java.util.List;

import com.google.appinventor.components.runtime.collect.Lists;

import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

public class MethodTraverser {
	public interface MethodFilter {
		public boolean accept(CtClass cc, CtMethod cm);
	}

	List<CtMethod> methods = Lists.newArrayList();

	public MethodTraverser(CtClass cc, MethodFilter filter) {
		while (cc != null) {
			CtMethod[] cms = cc.getDeclaredMethods();
			for (CtMethod cm : cms) {
				if (filter.accept(cc, cm)) {
					methods.add(cm);
				}
			}

			try {
				cc = cc.getSuperclass();
			} catch (NotFoundException e) {
				e.printStackTrace();
				Logger.err("找不到类:" + e.getMessage());
			}
		}
	}

	public CtMethod[] getResult() {
		return methods.toArray(new CtMethod[methods.size()]);
	}
}
