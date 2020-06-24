package cn.zzq.aix.builder.blocks;

import com.google.appinventor.components.annotations.DesignerProperty;
import com.google.appinventor.components.annotations.SimpleProperty;

import javassist.CtMethod;
import javassist.NotFoundException;

public class PropertyPair {
	public CtMethod readable;
	public CtMethod writable;
	public String name;
	SimpleProperty writableAnnotation;
	SimpleProperty readableAnnotation;
	DesignerProperty designerProperty;

	public PropertyPair(CtMethod property1, CtMethod property2) {
		try {
			if (property1.getName().equals(property2.getName())) {
				if (property1.getParameterTypes().length == 0) {
					this.readable = property1;
					this.writable = property2;
				} else {
					this.readable = property2;
					this.writable = property1;
				}
				this.name = readable.getName();
				writableAnnotation = (SimpleProperty) writable.getAnnotation(SimpleProperty.class);
				readableAnnotation = (SimpleProperty) writable.getAnnotation(SimpleProperty.class);
				designerProperty = (DesignerProperty) writable.getAnnotation(DesignerProperty.class);
			}
		} catch (NotFoundException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public String getType() {
		try {
			return BlockUtil.javaTypeToYailType(readable.getReturnType());
		} catch (NotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public String getDescription() {
		if (writableAnnotation.description().length() >= readableAnnotation.description().length()) {
			return writableAnnotation.description();
		} else {
			return readableAnnotation.description();
		}
	}

	public boolean isDeprecated() {
		try {
			return readable.getAnnotation(Deprecated.class) != null && writable.getAnnotation(Deprecated.class) != null;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}

	public boolean userVisible() {
		return readableAnnotation.userVisible() && writableAnnotation.userVisible();
	}

	public boolean matchRw() {
		try {
			boolean equalsName = readable.getName().equals(writable.getName());
			boolean readPropertyParmsCountMatch = readable.getParameterTypes().length == 0;
			boolean writePropertyParmsCountMatch = writable.getParameterTypes().length == 1;

			boolean equalsType = BlockUtil.javaTypeToYailType(readable.getReturnType())
					.equals(BlockUtil.javaTypeToYailType(writable.getParameterTypes()[0]));

			boolean writableHasAnnotation = writable.getAnnotation(SimpleProperty.class) != null;
			boolean readableHasAnnotation = readable.getAnnotation(SimpleProperty.class) != null;

			return equalsName && readPropertyParmsCountMatch && writePropertyParmsCountMatch && equalsType
					&& writableHasAnnotation && readableHasAnnotation;
		} catch (NotFoundException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public String toString() {
		return writableAnnotation + " " + readableAnnotation;
	}
}
