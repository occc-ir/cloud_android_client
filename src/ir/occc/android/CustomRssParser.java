package ir.occc.android;

import ir.occc.android.model.RssItem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

public class CustomRssParser {

	// We don't use namespaces
	private final String ns = null;

	public List<RssItem> parse(InputStream inputStream) throws XmlPullParserException, IOException {
		try {
			XmlPullParser parser = Xml.newPullParser();
			parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
			parser.setInput(inputStream, null);
			parser.nextTag();
			return readFeed(parser);
		} finally {
			if (inputStream != null)
				inputStream.close();
		}
	}

	private List<RssItem> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, "rss");
		String title = null;
		String link = null;
		String description = null;
		String pubDate = null;
		String thumbUrl = null;
		List<RssItem> items = new ArrayList<RssItem>();
		while (parser.next() != XmlPullParser.END_DOCUMENT) {
			if (parser.getEventType() != XmlPullParser.START_TAG) {
				continue;
			}
			String name = parser.getName();
			if (name.equalsIgnoreCase("title")) {
				title = readTitle(parser);
			} else if (name.equalsIgnoreCase("link")) {
				link = readLink(parser);
			} else if (name.equalsIgnoreCase("description")) {
				description = readDescription(parser);
			} else if (name.equalsIgnoreCase("pubdate")) {
				pubDate = readDate(parser);
			} else if (name.equalsIgnoreCase("thumburl")) {
				thumbUrl = readThumbUrl(parser);
			}
			if (title != null && link != null && pubDate != null /*&& thumbUrl != null*/) {
				RssItem item = new RssItem(title, link, description, pubDate, thumbUrl);
				items.add(item);
				title = null;
				link = null;
				description = null;
				pubDate = null;
				thumbUrl = null;
			}
		}
		return items;
	}

	private String readLink(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "link");
		String link = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "link");
		return link;
	}

	private String readTitle(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "title");
		String title = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "title");
		return title;
	}

	private String readDescription(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "description");
		String description = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "description");
		return description;
	}

	private String readDate(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "pubDate");
		String date = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "pubDate");
		return date;
	}

	private String readThumbUrl(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, ns, "thumburl");
		String thumbUrl = readText(parser);
		parser.require(XmlPullParser.END_TAG, ns, "thumburl");
		return thumbUrl;
	}

	// For the tags title and link, extract their text values.
	private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
		String result = "";
		if (parser.next() == XmlPullParser.TEXT) {
			result = parser.getText();
			parser.nextTag();
		}
		return result;
	}
}
