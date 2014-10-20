package ir.occc.android.common;

/**
 * @author Ahmad
 * Fragments enumeration
 */
public enum Fragments {
	Wiki, News, Forum, IrcWeb, IrcConsole, Feedback;
	
	/** Convert fragment string to Fragments enumeration
	 * @param value Fragment string which want to convert
	 * @return Fragments enumeration
	 */
	public static Fragments toFragments(String value) {
		try {
			return valueOf(value);
		} catch (Exception e) {
			return Fragments.News;
		}
	}
}
