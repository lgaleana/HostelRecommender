package com.triptailor.model;

import java.util.HashMap;
import java.util.PriorityQueue;

import com.triptailor.classifier.HostelClassifier.TagHolder;

/**
 * The model for a Hostel
 * 
 * @author lgaleana
 *
 */
public class Hostel {
	
	private long id;
	private String name;
	private String description;
	private float price;
	private String image;
	private String link;
	private String city;
	private String country;
	private int noReviews;
	private HashMap<String, Double> attributes;
	
	private double rating;
	private PriorityQueue<TagHolder> tags;
	
	public Hostel(long id, String name, int noReviews, float price, String link, HashMap<String, Double> attributes) {
		this.id = id;
		this.name = name;
		this.noReviews = noReviews;
		this.price = price;
		this.link = link;
		this.attributes = attributes;
	}
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public String getImage() {
		return image;
	}
	public void setImage(String image) {
		this.image = image;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public int getNoReviews() {
		return noReviews;
	}
	public void setNoReviews(int no_reviews) {
		this.noReviews = no_reviews;
	}
	public HashMap<String, Double> getAttributes() {
		return attributes;
	}
	public void setAttributes(HashMap<String, Double> attributes) {
		this.attributes = attributes;
	}
	public double getRating() {
		return rating;
	}
	public void setRating(double rating) {
		this.rating = rating;
	}
	public PriorityQueue<TagHolder> getTags() {
		return tags;
	}
	public void setTags(PriorityQueue<TagHolder> tags) {
		this.tags = tags;
	}
}