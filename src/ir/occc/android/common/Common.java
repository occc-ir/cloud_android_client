package ir.occc.android.common;

import java.util.regex.Matcher;
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
	 * @param text The text that wanted to check 
	 * @return true for RLT required and false for RTL NOT required 
	 */
	public static Boolean isRtlRequired(String text) {
		if (text.length() > 0) {
			Matcher matcher = Common.RtlPersianPattern.matcher(text.substring(0, 1));
			return matcher.find();
		}
		return false;
	}
	
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
	/**
	 * Wiki link key name.
	 */
	public static final String WIKI_LINK = "wiki_link";
	
}
