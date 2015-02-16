package com.triptailor.classifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import com.triptailor.model.Hostel;

/**
 * A classifier of Hostels, based on a Hostel or a list of attributes
 * 
 * @author lgaleana
 *
 */
public abstract class HostelClassifier {
	
	final static private double WEIGHT_BASE = 1.5;	// Weight used to penalize Hostels, depending on its number of reviews
	final static private int QUEUE_SIZE = 6;	// Size of the queue that holds the Hostel tags
	
	/**
	 * @param model - A list of Hostels. It includes the base Hostel
	 * @param hostel - The base Hostel to compare Hostels against to
	 * @return A list of hostels, ordered in terms of similarity aginst the base hostel
	 */
	public static List<Hostel> classify(List<Hostel> model, Hostel hostel) {
		// The highest number of reviews is used to penalize Hostels
		int reviews = getHighestNoReviews(model);
		int highestNoReviews = hostel.getNoReviews() < reviews ? hostel.getNoReviews() : reviews;
		
		for(Hostel modelEntry : model) {
			double rating = 0;
			HashMap<String, Double> modelHostel = modelEntry.getAttributes();	// The Hostel vector
			PriorityQueue<TagHolder> sharedQueue = new PriorityQueue<TagHolder>(QUEUE_SIZE, new TagComparator());
			
			for(Map.Entry<String, Double> hostelEntry : hostel.getAttributes().entrySet()) {
				String attribute = hostelEntry.getKey();
				double distance, modelHostelRating;
				double hostelRating = hostelEntry.getValue();
				
				// The Hostel vectors are compared using Euclidean distance
				if(modelHostel.containsKey(attribute)) {
					modelHostelRating = modelHostel.get(attribute);
					distance = hostelRating - modelHostelRating;
					// The attribute is added to the queue of tags
					addToQueue(sharedQueue, new TagHolder(attribute, modelHostelRating, 0), QUEUE_SIZE / 2);
					modelHostel.remove(attribute);
				}
				else {
					distance = hostelRating;
				}
				rating += Math.pow(distance, 2);	// No square root is applied in the end, to highlight similarities and differences
			}
			
			// Tags not shared with the base Hostel are added
			int size = QUEUE_SIZE - sharedQueue.size();
			PriorityQueue<TagHolder> queue = new PriorityQueue<TagHolder>(QUEUE_SIZE, new TagComparator());
			for(Map.Entry<String, Double> modelHostelEntry : modelHostel.entrySet())
				addToQueue(queue, new TagHolder(modelHostelEntry.getKey(), modelHostelEntry.getValue(), 1), size);
			
			// Tags are grouped in a single queue
			for(TagHolder holder : sharedQueue)
				addToQueue(queue, holder, QUEUE_SIZE);
			
			// A penalty is applied, based on the number of reviews
			highestNoReviews++;
			int modelReviews = modelEntry.getNoReviews() + 1;
			int reviewDifference = highestNoReviews / modelReviews;
			rating = (rating * Math.pow(WEIGHT_BASE, reviewDifference)) / WEIGHT_BASE;
			
			modelEntry.setTags(queue);
			modelEntry.setRating(rating);
		}
		
		// The model is ordered
		Collections.sort(model, new HostelComparator());
		
		return model;
	}
	
	/**
	 * @param model - A list of Hostels
	 * @param attributes - A list of attributes
	 * @return A list of hostels, ordered in terms of which contain more strongly the attributes passed
	 */
	public static List<Hostel> classifyByTags(List<Hostel> model, List<String> attributes) {
		// The average number of reviews is used to penalize hostels
		int averageNoReviews = getAverageNoReviews(model);
		
		for(Hostel modelEntry : model) {
			double rating = 0;
			HashMap<String, Double> modelHostel = modelEntry.getAttributes();	// The hostel vector
			PriorityQueue<TagHolder> sharedQueue = new PriorityQueue<TagHolder>(QUEUE_SIZE, new TagComparator());
			
			for(String attribute : attributes) {
				double value;
				
				// The importance of an attribute to a Hostel is the measure used
				if(modelHostel.containsKey(attribute)) {
					value = modelHostel.get(attribute);
					addToQueue(sharedQueue, new TagHolder(attribute, value, 0), QUEUE_SIZE);
				}
				else
					value = 0;
				rating += value;
			}
			
			// The other important attributes of the Hostel are added
			int size = QUEUE_SIZE - sharedQueue.size();
			PriorityQueue<TagHolder> queue = new PriorityQueue<TagHolder>(QUEUE_SIZE, new TagComparator());
			for(Map.Entry<String, Double> modelHostelEntry : modelHostel.entrySet())
				if(!attributes.contains(modelHostelEntry.getKey()))
					addToQueue(queue, new TagHolder(modelHostelEntry.getKey(), modelHostelEntry.getValue(), 1), size);
			
			// Tags are grouped in a single queue
			for(TagHolder holder : sharedQueue)
				addToQueue(queue, holder, QUEUE_SIZE);
			
			// A penalty is applied based on the number of reviews
			averageNoReviews++;
			int modelReviews = modelEntry.getNoReviews() + 1;
			int reviewDifference = averageNoReviews / modelReviews;
			reviewDifference = reviewDifference < 1 ? 1 : reviewDifference;
			rating = (rating / Math.pow(WEIGHT_BASE, reviewDifference)) / WEIGHT_BASE;
			
			modelEntry.setTags(queue);
			modelEntry.setRating(rating);
		}
		
		// The model is ordered
		Collections.sort(model, new HostelComparator2());
		
		return model;
	}
	
	/**
	 * This method will add the element passed if its rating is higher than that of the element with the lowest rating
	 * 
	 * @param queue - A queue to add an element to
	 * @param element - The element to add to the queue
	 * @param size - The maximum size of the queue
	 * 
	 */
	private static void addToQueue(PriorityQueue<TagHolder> queue, TagHolder element, int size) {
		if(queue.size() == size) {
			ArrayList<TagHolder> aux = new ArrayList<TagHolder>(size - 1);
			while(queue.size() > 1)
				aux.add(queue.poll());
			TagHolder exit = queue.poll();
			for(TagHolder h : aux)
				queue.add(h);
			if(element.rating > exit.rating)
				queue.add(element);
			else {
				queue.add(exit);
			}
		}
		else
			queue.add(element);
	}
	
	/**
	 * Gets the highest number of reviews in the model
	 * 
	 * @param model - A list of Hostels
	 * @return
	 */
	private static int getHighestNoReviews(List<Hostel> model) {
		int highestNoReviews = 0;
		for(Hostel hostel : model) {
			int hostelReviews = hostel.getNoReviews();
			if(hostelReviews > highestNoReviews)
				highestNoReviews = hostelReviews;

		}
		return highestNoReviews;
	}
	
	/**
	 * Gets the average number of reviews in the model
	 * 
	 * @param model - A list of Hostels
	 * @return
	 */
	private static int getAverageNoReviews(List<Hostel> model) {
		int averageNoReviews = 0;
		for(Hostel entry: model)
			averageNoReviews += entry.getNoReviews();
		return averageNoReviews / model.size();
	}

	/**
	 * A class that holds a tag
	 * 
	 * @author lgaleana
	 *
	 */
	public static class TagHolder {
		public String name;
		public double rating;
		public int type;
		
		public TagHolder(String name, double rating, int type) {
			this.name = name;
			this.rating = rating;
			this.type = type;
		}
	}
	
	/**
	 * A comparator of Hostels in ascending order
	 * 
	 * @author lgaleana
	 *
	 */
	private static class HostelComparator implements Comparator<Hostel> {
	    @Override
	    public int compare(Hostel h1, Hostel h2) {
	        if(h1.getRating() < h2.getRating())
	        	return -1;
	        if(h1.getRating() == h2.getRating())
	        	return 0;
	        else
	        	return 1;
	    }
	}
	
	/**
	 * A comparator of Hostels in descending order
	 * 
	 * @author lgaleana
	 *
	 */
	private static class HostelComparator2 implements Comparator<Hostel> {
	    @Override
	    public int compare(Hostel h1, Hostel h2) {
	        if(h1.getRating() > h2.getRating())
	        	return -1;
	        if(h1.getRating() == h2.getRating())
	        	return 0;
	        else
	        	return 1;
	    }
	}
	
	/**
	 * A comparator of tags in descending order
	 * 
	 * @author lgaleana
	 *
	 */
	private static class TagComparator implements Comparator<TagHolder> {
	    @Override
	    public int compare(TagHolder h1, TagHolder h2) {
	        if(h1.rating > h2.rating)
	        	return -1;
	        if(h1.rating == h2.rating)
	        	return 0;
	        else
	        	return 1;
	    }
	}
}