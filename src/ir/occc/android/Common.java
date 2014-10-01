package ir.occc.android;

import java.util.regex.Pattern;

public class Common {

	public static final Pattern RtlPersianPattern = Pattern.compile("[\u0600-\u06FF\u0750-\u077F\u0590-\u05FF\uFE70-\uFEFF]");
	
	public static final String RECEIVER = "receiver";
	public static final String COMMAND = "command";
	public static final String RSS_LINK = "rss_link";
	
}
