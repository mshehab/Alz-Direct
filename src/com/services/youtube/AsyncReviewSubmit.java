package com.services.youtube;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;

import com.google.ytd.ReviewDetailsActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class AsyncReviewSubmit extends AsyncTask<Void, Void, Void> {
	
	private final ProgressDialog dialog;
	private final ReviewDetailsActivity myActivity;	
	private YoutubeUtils service;
	private String uid, aid, pid, vid, rating;
	
	public AsyncReviewSubmit(ReviewDetailsActivity myActivity, String uid, String aid, 
			String pid, String vid, String rating){
		this.uid = uid;
		this.aid = aid;
		this.pid = pid;
		this.vid = vid;
		this.rating = rating;
		this.myActivity = myActivity;
		this.service = myActivity.service;
		this.dialog = new ProgressDialog(myActivity);
	}

	@Override
	protected void onPreExecute() {
		dialog.setMessage("Submitting Reivew...");
		dialog.show();
	}

	@Override
	protected Void doInBackground(Void... params) {
		try {
			service.submitReview(uid, aid, pid, vid, rating);
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Void result) {
		myActivity.rateButton.setEnabled(false);
		dialog.dismiss();
	}


}
