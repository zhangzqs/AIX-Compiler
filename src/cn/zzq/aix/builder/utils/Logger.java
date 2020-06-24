package cn.zzq.aix.builder.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.google.appinventor.components.runtime.collect.Lists;

public class Logger {

	public static List<Log> logList = Lists.newArrayList();

	public static void log(Object message) {
		log(message, true);
	}

	public static void log(Object message, boolean appendNextLine) {
		Log msg = new Log(Log.INFO, message);
		logList.add(msg);
		System.out.println((appendNextLine ? '\n' : "") + msg.toString());
	}

	public static void warn(String message) {
		Log msg = new Log(Log.WARN, message);
		logList.add(msg);
		System.err.println('\n' + msg.toString());
	}

	public static void err(Object message) {

		Log msg = new Log(Log.ERROR, message);
		logList.add(msg);
		System.err.println('\n' + msg.toString());
	}

	public static String getLoggerText() {
		StringBuffer sb = new StringBuffer();
		for (Log log : logList) {
			sb.append(log + "\n");
		}
		return sb.toString();
	}

	public static class Log {
		public static final String INFO = "I";
		public static final String WARN = "W";
		public static final String ERROR = "E";
		private String level;

		private Object message;

		public String getLevel() {
			return level;
		}

		public Object getMessage() {
			return message;
		}

		public Log(String level, Object message) {
			this.level = level;
			this.message = message;
		}

		@Override
		public String toString() {
			return level + ":[" + getCurrentTime() + "]:" + message;
		}
	}

	private static String getCurrentTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
		String time = sdf.format(new Date());
		return time;
	}
}
