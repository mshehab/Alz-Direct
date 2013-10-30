package com.services.youtube;

public class PostEntry {
	String title;
	String description;
	String content_src;
	String video_id;
	public String getVideo_id() {
		return video_id;
	}
	public void setVideo_id(String video_id) {
		this.video_id = video_id;
	}
	String author_name;
	String thumbnail;
	int viewCount=0;
	int favoriteCount=0;
	public String getTitle() {
		return title;
	}
	public String getDescription() {
		return description;
	}
	public String getContent_src() {
		return content_src;
	}
	public String getAuthor_name() {
		return author_name;
	}
	public String getThumbnail() {
		return thumbnail;
	}
	public int getViewCount() {
		return viewCount;
	}
	public int getFavoriteCount() {
		return favoriteCount;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void setContent_src(String content_src) {
		this.content_src = content_src;
	}
	public void setAuthor_name(String author_name) {
		this.author_name = author_name;
	}
	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}
	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	public void setFavoriteCount(int favoriteCount) {
		this.favoriteCount = favoriteCount;
	}
	@Override
	public String toString() {
		return "PostEntry [title=" + title + ", description=" + description
				+ ", content_src=" + content_src + ", author_name="
				+ author_name + ", thumbnail=" + thumbnail + ", viewCount="
				+ viewCount + ", favoriteCount=" + favoriteCount + "]";
	}
	
}