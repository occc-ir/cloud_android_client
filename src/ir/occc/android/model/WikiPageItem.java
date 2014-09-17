package ir.occc.android.model;

public class WikiPageItem {
	private final String title;
	private final String content;
	private final int pageId;

	public WikiPageItem(String title, String content, int pageId) {
		this.title = title;
		this.content = content;
		this.pageId = pageId;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}
	
	public int getPageId() {
		return pageId;
	}
}
