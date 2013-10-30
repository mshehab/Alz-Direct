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

import android.content.Context;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.services.youtube.PostEntry;

public class ReviewsArrayAdapter extends ArrayAdapter<PostEntry> {
	private static final String LOG_TAG = MainActivity.class.getSimpleName();
	private final LayoutInflater inflater; 
	private int resource;
	private Context context;
	List<PostEntry> objects;
	

	private static class ViewHolder {  
		public ImageView thumbnailView;  
		public TextView titleTextView;  
		public TextView authorTextView;  
	}  
	
	public ReviewsArrayAdapter(Context context, int resource, List<PostEntry> objects) {
		super(context, resource, objects);
		this.resource = resource;
		this.context = context;
		this.objects = objects;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        BitmapManager.INSTANCE.setPlaceholder(BitmapFactory.decodeResource(context.getResources(), R.drawable.nothumbnail)); 
	}

	
	//90 120
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;		
		if (convertView == null) {			
			convertView = inflater.inflate(this.resource, null);
			
			TextView titleTextView = (TextView) convertView.findViewById(R.id.reviewtitle);  
            TextView authorTextView = (TextView) convertView.findViewById(R.id.reviewviews);  
            ImageView thumbnailView = (ImageView) convertView.findViewById(R.id.imageViewThumbnail); 
            holder = new ViewHolder();
            holder.titleTextView = titleTextView;
            holder.authorTextView = authorTextView;
            holder.thumbnailView = thumbnailView;
            convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		PostEntry postentry = getItem(position);		
		holder.titleTextView.setText(postentry.getTitle());
		holder.authorTextView.setText("by " + postentry.getAuthor_name() + "  " + postentry.getViewCount() + " views");
		holder.thumbnailView.setTag(postentry.getThumbnail());

		int width = 120, height=90;
		//int width = 100, height=75;
		BitmapManager.INSTANCE.loadBitmap(postentry.getThumbnail(), holder.thumbnailView, width,  height); 
        return convertView;
	}	
}
