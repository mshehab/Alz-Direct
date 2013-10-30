package com.services.youtube;
import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.http.client.ClientProtocolException;

import com.google.ytd.ReviewDetailsActivity;

import android.app.ProgressDialog;
import android.os.AsyncTask;

public class AsyncReviewGet extends AsyncTask<Void, Void, Double> {	
	private final ProgressDialog dialog;
	private final ReviewDetailsActivity myActivity;	
	private YoutubeUtils service;
	private String uid, aid, pid, vid;
	
	public AsyncReviewGet(ReviewDetailsActivity myActivity, String uid, String aid, 
			String pid, String vid){
		this.uid = uid;
		this.aid = aid;
		this.pid = pid;
		this.vid = vid;
		this.myActivity = myActivity;
		this.service = myActivity.service;
		this.dialog = new ProgressDialog(myActivity);
	}

	@Override
	protected void onPreExecute() {
		dialog.setMessage("Retrieving Reivews...");
		dialog.show();
	}

	@Override
	protected Double doInBackground(Void... params) {
		double review = -1.0;
		try {
			review = service.getReview(uid, aid, pid, vid);
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
		return review;
	}
	
	@Override
	protected void onPostExecute(Double result) {
		myActivity.retrievedReview = result.doubleValue();
		if(result.doubleValue() >= 0){
			myActivity.rateButton.setEnabled(false);			
		}
		dialog.dismiss();
	}


}
