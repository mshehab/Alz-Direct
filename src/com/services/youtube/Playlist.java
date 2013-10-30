package com.services.youtube;

import java.util.ArrayList;

import com.google.ytd.MainActivity;

import android.util.Log;


public class Playlist {
	String playlist_id;
	String title;
	String description;
	ArrayList<PostEntry> posts;
	
	public Playlist() {
		posts = new ArrayList<PostEntry>();
	}
	
	public void addPostEntry(PostEntry pe){
		posts.add(pe);
	}
	
	public void printList(){
		for(PostEntry pe : posts){
			Log.d(MainActivity.TAG, pe.toString());
		}
		
	}
	
}
