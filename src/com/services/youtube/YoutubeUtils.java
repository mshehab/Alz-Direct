package com.services.youtube;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIUtils;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import android.util.Log;

public class YoutubeUtils {
	HttpClient client;
	static private String playlistsURL = "https://gdata.youtube.com/feeds/api/users/HOUoinjavOYXbqVo5Hk-RQ/playlists?v=2";
	static private String playlistBaseURL = "http://gdata.youtube.com/feeds/api/playlists/";
	static private String reviewSubmitURL = "http://cci-webdev.uncc.edu/~mshehab/alz-api/submitReview.php";
	static private String reviewGetURL = "http://cci-webdev.uncc.edu/~mshehab/alz-api/getReview.php";

	public YoutubeUtils() {
		client = new DefaultHttpClient();
	}

	public Playlist getPlaylist(String playlistID)
			throws ClientProtocolException, XmlPullParserException, IOException {
		Playlist pl = parsePlaylist(getInputStream(getPlaylistURL(playlistID)));
		return pl;
	}

	private Playlist parsePlaylist(InputStream xml)
			throws XmlPullParserException, IOException {
		XmlPullParser parser = XmlPullParserFactory.newInstance()
				.newPullParser();
		parser.setInput(xml, "UTF-8");
		int event = parser.getEventType();
		Playlist pl = new Playlist();
		PostEntry e = null;
		while (event != XmlPullParser.END_DOCUMENT) {
			switch (event) {
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("entry")) {
					e = new PostEntry();
				} else if (parser.getName().equals("yt:playlistId")) {
					pl.playlist_id = parser.nextText().trim();
				}

				if (e == null) {
					if (parser.getName().equals("title")) {
						pl.title = parser.nextText().trim();
					} else if (parser.getName().equals("media:description")) {
						pl.description = parser.nextText().trim();
					}
				} else {
					if (parser.getName().equals("media:title")) {
						e.title = parser.nextText().trim();
					} else if (parser.getName().equals("yt:videoid")) {
						e.video_id = parser.nextText().trim();
					} else if (parser.getName().equals("content")) {
						e.content_src = parser.getAttributeValue(null, "src")
								.trim();
					} else if (parser.getName().equals("media:credit")
							&& parser.getAttributeValue(null, "role").trim()
									.equals("uploader")) {
						e.author_name = parser.nextText().trim();
					} else if (parser.getName().equals("media:description")) {
						e.description = parser.nextText().trim();
					} else if (parser.getName().equals("media:thumbnail")
							&& parser.getAttributeValue(null, "yt:name").trim()
									.equals("default")) {
						e.thumbnail = parser.getAttributeValue(null, "url")
								.trim(); // <media:thumbnail
											// url="http://i.ytimg.com/vi/KPyvwyRTzhM/default.jpg"
											// height="90" width="120"
											// time="00:00:07"
											// yt:name="default"/>
					} else if (parser.getName().equals("yt:statistics")) {
						e.favoriteCount = Integer.valueOf(parser
								.getAttributeValue(null, "favoriteCount")
								.trim());
						e.viewCount = Integer.valueOf(parser.getAttributeValue(
								null, "viewCount").trim());
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals("entry")) {
					pl.addPostEntry(e);
					e = null;
				}
				break;
			}
			event = parser.next();
		}
		return pl;
	}

	private InputStream getInputStream(String url)
			throws ClientProtocolException, IOException {
		HttpGet request = new HttpGet(url);
		HttpResponse response;
		response = client.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return response.getEntity().getContent();
		}
		return null;
	}

	public void submitReview(String uid, String aid, String pid, String vid,
			String rating) throws ClientProtocolException, IOException,
			URISyntaxException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("aid", aid));
		params.add(new BasicNameValuePair("pid", pid));
		params.add(new BasicNameValuePair("vid", vid));
		params.add(new BasicNameValuePair("rating", rating));
		InputStream in = submitPostHttp(reviewSubmitURL, params);
	}

	public double getReview(String uid, String aid, String pid, String vid) throws ClientProtocolException, IOException,
			URISyntaxException {
		double review = -1.0;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("uid", uid));
		params.add(new BasicNameValuePair("aid", aid));
		params.add(new BasicNameValuePair("pid", pid));
		params.add(new BasicNameValuePair("vid", vid));
		
		BufferedReader in = new BufferedReader(new InputStreamReader(submitPostHttp(reviewGetURL, params)));
		StringBuffer sb = new StringBuffer("");
		String line = "";
		while ((line = in.readLine()) != null) {
			sb.append(line + "\n");
			Log.d("demo1", line + ",,,,");
		}
		in.close();
		try{
			review = Double.parseDouble(sb.toString());
		} catch(Exception e){
		}
		return review;
	}

	private InputStream submitPostHttp(String url, List<NameValuePair> params)
			throws ClientProtocolException, IOException {
		HttpPost request = new HttpPost(url);
		UrlEncodedFormEntity formParams = new UrlEncodedFormEntity(params);
		request.setEntity(formParams);
		HttpResponse response = client.execute(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return response.getEntity().getContent();
		}
		return null;
	}

	private InputStream submitGetHttp(String domain, String path,
			List<NameValuePair> params) throws ClientProtocolException,
			IOException, URISyntaxException {
		HttpGet request;
		if (params.size() > 0) {
			URI uri = URIUtils.createURI("http", domain, -1, path,
					URLEncodedUtils.format(params, "UTF-8"), null);
			request = new HttpGet(uri);
		} else {
			request = new HttpGet("http://" + domain + "path");
		}
		HttpResponse response;
		response = client.execute(request);
		/*
		 * List<NameValuePair> params = new ArrayList<NameValuePair>();
		 * params.add(new BasicNameValuePair("name", "Bob Smith"));
		 * params.add(new BasicNameValuePair("age", "25"));
		 */
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			return response.getEntity().getContent();
		}
		return null;
	}

	private String getPlaylistURL(String playlistID) {
		Log.d("demo1", playlistBaseURL + playlistID + "?max-results=50&v=2");
		return playlistBaseURL + playlistID + "?max-results=50&v=2";
	}
}
