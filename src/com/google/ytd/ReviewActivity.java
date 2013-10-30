package com.google.ytd;
import java.util.List;

import com.google.ytd.db.Assignment;
import com.google.ytd.db.DbHelper;
import com.services.youtube.AsyncYouTubePlaylist;
import com.services.youtube.PostEntry;
import com.services.youtube.YoutubeUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.view.View.OnClickListener;

public class ReviewActivity extends Activity {

	private DbHelper dbHelper = null;
	private String ytdDomain = null;
	private String assignmentId = null;
	private TextView domainHeader = null;
	private String playlistId = null;
	private String description = null;
	public YoutubeUtils service;
	private ReviewsArrayAdapter reviewsArrayAdapter = null;
	private List<PostEntry> reviews;
	private ListView reviewsListView = null;
	public double retrievedReview = -1.0;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.review);
		dbHelper = new DbHelper(this);
		dbHelper = dbHelper.open();
		Intent intent = getIntent();
		this.ytdDomain = intent.getStringExtra(DbHelper.YTD_DOMAIN);
		this.assignmentId = intent.getStringExtra(DbHelper.ASSIGNMENT_ID);
		this.playlistId = intent.getStringExtra(DbHelper.PLAYLISTID);		
		this.description = intent.getStringExtra(DbHelper.DESCRIPTION);

		TextView assignmentDescription = (TextView) findViewById(R.id.assignmentDescription);		    
		assignmentDescription.setText("Resposes: " + this.description);

		reviewsListView = (ListView) findViewById(R.id.reviewslistView);
		
		this.service = new YoutubeUtils();		    
		new AsyncYouTubePlaylist(this).execute(this.playlistId);
	}


	public void displayReviewsList(List<PostEntry> reviewsNew) {		
		if(reviewsNew==null || reviewsNew.size() == 0){
			Toast.makeText(this, "No videos to review!! \ncheck later..", Toast.LENGTH_LONG).show();
		} else{
			reviews = reviewsNew;		  
			reviewsArrayAdapter = new ReviewsArrayAdapter(this, R.layout.review_list_item, reviews);
			reviewsListView.setAdapter(reviewsArrayAdapter);
			reviewsListView.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Intent intent = new Intent(ReviewActivity.this, ReviewDetailsActivity.class);
					intent.putExtra(DbHelper.YTD_DOMAIN, ytdDomain);
					intent.putExtra(DbHelper.ASSIGNMENT_ID, assignmentId);
					intent.putExtra(DbHelper.PLAYLISTID, playlistId);

					PostEntry postentry = reviewsArrayAdapter.getItem(position);
					intent.putExtra(DbHelper.YT_ACCOUNT, postentry.getAuthor_name());
					intent.putExtra("view_count", postentry.getViewCount() + "");		        
					intent.putExtra("video_id", postentry.getVideo_id());
					intent.putExtra("title", postentry.getTitle());
					intent.putExtra("description", postentry.getDescription());		
					startActivity(intent);
				}
			});
		}
	}


	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		dbHelper.close();
	}

}
