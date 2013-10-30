/* Copyright (c) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.ytd;

import java.util.List;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.google.ytd.db.Assignment;
import com.google.ytd.db.DbHelper;

public class DetailsActivity extends Activity {
  private static final String LOG_TAG = DetailsActivity.class.getSimpleName();
  private static final int CAPTURE_RETURN = 1;
  private static final int GALLERY_RETURN = 2;
  private static final int SUBMIT_RETURN = 3;
  private DbHelper dbHelper = null;
  private String ytdDomain = null;
  private String assignmentId = null;
  private String playlistId = null;
  private Assignment assignment = null;
  private TextView domainHeader = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    this.setContentView(R.layout.details);
    
    dbHelper = new DbHelper(this);
    dbHelper = dbHelper.open();

    Intent intent = getIntent();
    this.ytdDomain = intent.getStringExtra(DbHelper.YTD_DOMAIN);
    this.assignmentId = intent.getStringExtra(DbHelper.ASSIGNMENT_ID);

    this.domainHeader = (TextView) this.findViewById(R.id.domainHeader);
    domainHeader.setText(SettingActivity.getYtdDomains(this).get(this.ytdDomain));

    if (intent.hasExtra("notificationId")) {
      NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
      nm.cancel(intent.getIntExtra("notificationId", 0));
    }

    if (!Util.isNullOrEmpty(ytdDomain) && !Util.isNullOrEmpty(assignmentId)) {
      Log.d(LOG_TAG, "ytdDomain=" + ytdDomain + " id=" + assignmentId);
      displayAssignmentDetails();
    } else {
      displayAnonymousAssignment();
    }

    findViewById(R.id.captureButton).setOnClickListener(new OnClickListener() {
      @Override
      public void onClick(View v) {
        Intent i = new Intent();
        i.setAction(MediaStore.ACTION_VIDEO_CAPTURE);
        i.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 30);        
        startActivityForResult(i, CAPTURE_RETURN);
      }
    });

    findViewById(R.id.reviewButton).setOnClickListener(new OnClickListener() {
		@Override
		public void onClick(View v) {
			 Intent intent = new Intent(getBaseContext(), ReviewActivity.class);
			 intent.putExtra(DbHelper.YTD_DOMAIN, DetailsActivity.this.ytdDomain);
			 intent.putExtra(DbHelper.ASSIGNMENT_ID, DetailsActivity.this.assignmentId);
			 intent.putExtra(DbHelper.PLAYLISTID, DetailsActivity.this.assignment.getPlaylistId());
			 intent.putExtra(DbHelper.DESCRIPTION, DetailsActivity.this.assignment.getDescription());
			 startActivity(intent);
		}
	});

  }

  public void displayAssignmentDetails() {
    assignment = dbHelper.getAssignment(this.ytdDomain, this.assignmentId);    
    
    TextView assignmentDescription = (TextView) findViewById(R.id.assignmentDescription);
    assignmentDescription.setText(assignment.getDescription());
    
    TextView assignmentInstructions = (TextView) findViewById(R.id.assignmentInstructions);
    assignmentInstructions.setText(assignment.getInstructions());
  }

  public void displayAnonymousAssignment() {
    TextView assignmentDescription = (TextView) findViewById(R.id.assignmentDescription);
    assignmentDescription.setText("Upload your " + SettingActivity.getYtdDomains(this).get(this.ytdDomain)
        + " news videos");
    assignmentDescription.setTypeface(null, Typeface.BOLD);
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    switch (requestCode) {
    case CAPTURE_RETURN:
    case GALLERY_RETURN:
      if (resultCode == RESULT_OK) {
        Intent intent = new Intent(this, SubmitActivity.class);
        intent.setData(data.getData());
        intent.putExtra(DbHelper.YTD_DOMAIN, this.ytdDomain);
        if (!Util.isNullOrEmpty(this.assignmentId)) {
          intent.putExtra(DbHelper.ASSIGNMENT_ID, this.assignmentId);
        }
        intent.putExtra(DbHelper.DESCRIPTION, "RE:" + assignment.getDescription());
        intent.putExtra(DbHelper.INSTRUCTIONS, "RE:" + assignment.getInstructions());
        startActivityForResult(intent, SUBMIT_RETURN);
      }
      break;
    case SUBMIT_RETURN:
      if (resultCode == RESULT_OK) {
        Toast.makeText(DetailsActivity.this, "thank you!", Toast.LENGTH_LONG).show();
      } else {
        // Toast.makeText(DetailsActivity.this, "submit failed or cancelled",
        // Toast.LENGTH_LONG).show();
      }
      break;
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