package ir.occc.android.model;

public class RssItem {
	private final String date;
	private final String description;
	private final String title;
	private final String link;
	private final String thumbUrl;

	public RssItem(String title, String link, String description, String date, String thumbUrl) {
		this.title = title;
		this.link = link;
		this.description = description;
		this.date = date;
		this.thumbUrl = thumbUrl;
	}

	public String getTitle() {
		return title;
	}

	public String getLink() {
		return link;
	}

	public String getDescription() {
		return description;
	}

	public String getDate() {
		return date;
	}

	public String getThumbUrl() {
		return thumbUrl;
	}
}
