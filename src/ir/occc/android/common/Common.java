package ir.occc.android.common;

import java.util.regex.Pattern;

/**
 * @author Ahmad
 * Common class that contain global or shared values or used on various classes
 */
public class Common {

	/**
	 * Persian alphabet pattern
	 */
	public static final Pattern RtlPersianPattern = Pattern.compile("[\u0600-\u06FF\u0750-\u077F\u0590-\u05FF\uFE70-\uFEFF]");
	
	/**
	 * Receiver name for response of service
	 */
	public static final String RECEIVER = "receiver";
	/**
	 * Command key name. It's used on wiki or other services. 
	 */
	public static final String COMMAND = "command";
	/**
	 * RSS link key name.
	 */
	public static final String RSS_LINK = "rss_link";
	
}
