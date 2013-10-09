package edu.osu.AU13.cse4471.securevote;

import java.io.File;

/**
 * This class represents a poll known to a single client. Its methods will handle encryption
 * and determining the result for a poll. It is assumed that emailing the contents will be the 
 * responsibility of another class. It is just a basic interface right now, it will be fleshed
 * out when we have implementation details.
 */
public class Poll {
	private String title;
	private long pollKey;
	private long personalKey;
	
	private long[] votes;
	private int totalParticipants;
	
	/**
	 * Create a new poll object from an email.
	 * 
	 * @param emailContent The content of the email to parse
	 */
	public Poll(String emailContent) {
		// TODO
	}
	
	/**
	 * Create a new poll object by loading it from memory.
	 * 
	 * @param file The file to load
	 */
	public Poll(File file) {
		// TODO
	}
	
	/**
	 * Encrypt your choice using your personal key.
	 * @param choice Your choice
	 * @return Your choice encrypted
	 */
	public long generateVote(int choice) {
		// TODO
		return -1;
	}
	
	public int countVotes() {
		// TODO
		return -1;
	}
	
	@Override
	public String toString() {
		return title + ":" + totalParticipants + ":" + pollKey + ":" + personalKey;
	}
}
