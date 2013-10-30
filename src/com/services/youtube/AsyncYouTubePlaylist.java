package com.services.youtube;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.xmlpull.v1.XmlPullParserException;

import com.google.ytd.ReviewActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;

public class AsyncYouTubePlaylist extends AsyncTask<String, Void, Playlist> {
	private final ProgressDialog dialog;
	private final ReviewActivity myActivity;	
	private YoutubeUtils service;
	
	public AsyncYouTubePlaylist(ReviewActivity myActivity){
		this.myActivity = myActivity;
		this.service = myActivity.service;
		this.dialog = new ProgressDialog(myActivity);
	}

	@Override
	protected void onPreExecute() {
		dialog.setMessage("Loading playlist...");
		dialog.show();
	}

	@Override
	protected Playlist doInBackground(String... params) {
		Playlist pl = null;
		try {
			pl = service.getPlaylist(params[0]);
			return pl;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Playlist result) {
		dialog.dismiss();
		myActivity.displayReviewsList(result.posts);
	}
}
