package ir.occc.android;

public enum Fragments {
	Wiki, News, Forum;
	
	public static Fragments toFragments(String value) {
		try {
			return valueOf(value);
		} catch (Exception e) {
			return Fragments.News;
		}
	}
}
