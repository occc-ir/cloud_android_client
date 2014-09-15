package ir.occc.android.model;

public class WikiPageItem {
	private final String title;
	private final String content;

	public WikiPageItem(String title, String content) {
		this.title = title;
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}
}
