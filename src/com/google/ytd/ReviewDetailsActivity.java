package com.google.ytd;

import com.google.ytd.db.DbHelper;
import com.services.youtube.AsyncReviewGet;
import com.services.youtube.AsyncReviewSubmit;
import com.services.youtube.AsyncYouTubePlaylist;
import com.services.youtube.YoutubeUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebSettings.PluginState;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

public class ReviewDetailsActivity extends Activity {

	private String ytdDomain = null;
	private String assignmentId = null;
	private TextView domainHeader = null;
	private String playlistId = null;
	private String owner_account = null;
	private String view_count = null;
	private String video_id = null;
	private String title = null;
	private String description = null;
	private String youTubeName = null;
	private static final int DIALOG_RATING = 0;
	private ProgressDialog submitDialog = null;
	public YoutubeUtils service;
	private SharedPreferences preferences = null;
	public double retrievedReview = -1.0;
	public Button rateButton;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.review_details);
		retrievedReview = -1.0;

		Intent intent = getIntent();
		this.ytdDomain = intent.getStringExtra(DbHelper.YTD_DOMAIN);
		this.assignmentId = intent.getStringExtra(DbHelper.ASSIGNMENT_ID);
		this.playlistId = intent.getStringExtra(DbHelper.PLAYLISTID);		
		this.owner_account = intent.getStringExtra(DbHelper.YT_ACCOUNT);
		this.view_count = intent.getStringExtra("view_count");
		this.video_id = intent.getStringExtra("video_id");
		this.title = intent.getStringExtra("title");
		this.description = intent.getStringExtra("description");

		this.preferences = this.getSharedPreferences(MainActivity.SHARED_PREF_NAME,
				Activity.MODE_PRIVATE);
		this.youTubeName = preferences.getString(DbHelper.YT_ACCOUNT, null);

		setupDisplay();

		this.service = new YoutubeUtils();	

		
		findViewById(R.id.fullButton).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + video_id + "?rel=0")));
				startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/v/" + video_id + "?version=3&f=playlists&app=youtube_gdata")));
			}
		});

		rateButton = (Button)findViewById(R.id.rateButton);
		rateButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				createRatingDialog().show();
			}
		});
		new AsyncReviewGet(ReviewDetailsActivity.this, youTubeName, assignmentId, playlistId, video_id).execute();
	}

	private Dialog createRatingDialog() {
		final Dialog dialog = new Dialog(ReviewDetailsActivity.this);
		dialog.setTitle("Rate Video");
		dialog.setContentView(R.layout.rating);
		dialog.setCancelable(false);		
		dialog.findViewById(R.id.agree).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {	        
				RatingBar rb = (RatingBar)dialog.findViewById(R.id.ratingBarVideo);
				String yourRating = rb.getRating() + "";				
				Toast.makeText(getBaseContext(), "You rated it " + yourRating.trim() + " out of 5", Toast.LENGTH_LONG).show();
				new AsyncReviewSubmit(ReviewDetailsActivity.this, 
						youTubeName, assignmentId, playlistId, video_id, rb.getRating()+"").execute();
				dialog.cancel();
			}
		});
		dialog.findViewById(R.id.notagree).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.cancel();
			}
		});
		return dialog;
	}


	public class AsyncRatingSubmit extends AsyncTask<Void, Void, Void>{
		@Override
		protected void onPreExecute() {
			submitDialog = new ProgressDialog(getBaseContext());
			submitDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			submitDialog.setMessage("Storing Rating ...");
			submitDialog.setCancelable(false);
			submitDialog.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
		}		
	}



	private void setupDisplay(){	
		TextView videoTitleView = (TextView) findViewById(R.id.videoTitle);
		videoTitleView.setText(this.title);

		TextView videoReviewView = (TextView) findViewById(R.id.videoReviewviews);
		videoReviewView.setText("by " + this.owner_account + "   " + this.view_count + " views");

		double ratio = 360.0/640.0;
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);

		int width = (int)( 0.625 * (double)metrics.widthPixels);
		int height = (int) (ratio * ((double)width));

		Log.d(MainActivity.TAG, width + "," + height + " : " + ratio + ", " + ratio * ((double)width));

		WebView  mWebView = (WebView) findViewById(R.id.webViewVideo);
		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.getSettings().setPluginState(PluginState.ON);
		String html = "<html><head></head><body>" +
				"<iframe class=\"youtube-player\" type=\"text/html\" width=\"" + width +"\" height=\"" + height +
				"\" src=\"http://www.youtube.com/embed/"+video_id+"?rel=0\" frameborder=\"0\" allowfullscreen>" +
				"</body></html>";
		mWebView.loadData(html,"text/html", "utf-8");
	}

}
