package cn.zzq.aix.builder.utils;

import java.util.ArrayList;
import java.util.Collection;

import com.google.appinventor.components.runtime.collect.Lists;

public class CommandBuilder {
	private ArrayList<String> commands = Lists.newArrayList();

	public CommandBuilder add(Object... commands) {
		for (Object command : commands) {
			this.commands.add(command.toString());
		}
		return this;
	}

	public CommandBuilder add(Collection<?> commands) {
		commands.forEach(this::add);
		return this;
	}

	public String[] buildCommandArray() {
		return toString().split(" ");
	}

	@Override
	public String toString() {
		// commands.forEach(System.out::println);
		if (commands.isEmpty()) {
			return "";
		} else {
			StringBuffer sb = new StringBuffer();
			commands.forEach(it -> sb.append(it + " "));
			return sb.toString().trim();
		}
	}
}
