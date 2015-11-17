package insightfellowship.features;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class AverageDegree {

	private Queue<TweetNode> hashTags = new PriorityQueue<>();
	private Map<String, List<String>> graph = new HashMap<>();
	private PrintWriter writeIntoFile;
	private double prevAverage = 0.00;

	class TweetNode implements Comparable<TweetNode> {
		String timeStamp;
		Set<String> hashTags;

		public TweetNode(String timeStamp, Set<String> hashTags) {
			this.timeStamp = timeStamp;
			this.hashTags = hashTags;
		}

		@Override
		public int compareTo(TweetNode o) {

			return parseTwitterUTC(timeStamp).compareTo(
					parseTwitterUTC(o.timeStamp));

		}
	}

	public Date parseTwitterUTC(String date) {

		String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZ yyyy";
		SimpleDateFormat sf = new SimpleDateFormat(twitterFormat,
				Locale.ENGLISH);
		sf.setLenient(true);
		try {
			return sf.parse(date);
		} catch (ParseException e) {
			return new Date();
		}
	}

	public void calculateAverageDegree(String sourceFile, String output) {
		try {
			writeIntoFile = new PrintWriter(output); // write average to output
														// file
		} catch (FileNotFoundException e1) {
			System.out.println("File not found !!");
			return;
		}
		// read twitter from input file
		try (BufferedReader br = new BufferedReader(new FileReader(sourceFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (line.contains("timestamp:")) {
					String[] columns = line.split("timestamp:");
					processTweet(columns); // process tweet line by line
				} else {
					break;
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found !!");
		} catch (IOException e) {
			System.out.println("Not able to read file");
		} finally {
			if (writeIntoFile != null)
				writeIntoFile.close();
		}

	}

	private void processTweet(String[] columns) {
		String tweet = columns[0];
		String timeStamp = columns[1].replace(")", "");
		String tweetWords[] = tweet.substring(0, tweet.length() - 1).split(" ");
		String hashTag = "";
		Set<String> hashtagList = new HashSet<String>();
		for (int i = 0; i < tweetWords.length; i++) {
			if (tweetWords[i].startsWith("#")) { // hashtag
				hashTag = tweetWords[i].replaceAll("[!$%^&*+.]", "");
				if (hashTag.length() > 2)
					hashtagList.add(hashTag.toLowerCase());
			}
		}
		constructGraph(timeStamp.trim(), hashtagList);

	}

	private void constructGraph(String timeStamp, Set<String> hashtagList) {
		// check 60 sec time window

		boolean removedOldTweets = removeTweets60SecsOld(timeStamp);
		if (hashtagList.size() > 1) { // constraint NO 2 -> Dont add to graph if
										// there is not more than one hash tag
										// in a tweet
			for (String node : hashtagList) {
				List<String> connectedNodes = new ArrayList<String>();
				connectedNodes.addAll(hashtagList);
				connectedNodes.remove(node);
				if (!graph.containsKey(node)) {
					graph.put(node, connectedNodes);
				} else {
					graph.get(node).addAll(connectedNodes);
				}
			}
		}
		TweetNode node = new TweetNode(timeStamp, hashtagList);
		hashTags.offer(node);
		calculateRollingAverage(removedOldTweets, !hashtagList.isEmpty());
	}

	private void calculateRollingAverage(boolean removedOldTweets,
			boolean newTweets) {

		long totalEdges = graph.values().stream()
				.mapToInt((x) -> new HashSet<>(x).size()).sum();
		float totalNodes = graph.size();
		Double average = prevAverage;
		if (removedOldTweets || newTweets) {
			if (!(totalNodes == 0 || totalEdges == 0)) {
				average = (double) (totalEdges / totalNodes);
			}
			DecimalFormat df = new DecimalFormat("#.##");
			average = Double.valueOf(df.format(average));
		}
		if (writeIntoFile != null) {
			writeIntoFile.println(average);
			prevAverage = average;
		}
	}

	private boolean removeTweets60SecsOld(String timeStamp) {

		Date newTweetTimeStamp = parseTwitterUTC(timeStamp);
		Map<String, List<String>> deprecatedGraph = new HashMap<>();
		while (isQueueContainsMorethan60SecsTweet(newTweetTimeStamp)) {
			TweetNode tweet = hashTags.poll();
			if (tweet.hashTags.size() < 2) {
				// it would not have contributed to the graph
				continue;
			}
			for (String node : tweet.hashTags) {
				List<String> connectedNodes = new ArrayList<String>();
				connectedNodes.addAll(tweet.hashTags);
				connectedNodes.remove(node);
				if (!deprecatedGraph.containsKey(node)) {
					deprecatedGraph.put(node, connectedNodes);
				} else {
					deprecatedGraph.get(node).addAll(connectedNodes);
				}
			}

		}
		// remove the nodes,edges from graph contains more than 60secs interval
		if (!deprecatedGraph.isEmpty()) {
			String value;
			for (Map.Entry<String, List<String>> hashTagKeys : deprecatedGraph
					.entrySet()) {
				List<String> hashList = graph.get(hashTagKeys.getKey());
				Iterator<String> iter = hashList.iterator();
				while (iter.hasNext()) {
					value = iter.next();
					if (hashTagKeys.getValue().contains(value)) {
						hashTagKeys.getValue().remove(value);
						iter.remove();
					}
				}
				if (hashList.isEmpty()) {
					graph.remove(hashTagKeys.getKey());
				}
			}
			return true;
		}
		return false;
	}

	private boolean isQueueContainsMorethan60SecsTweet(Date timeStamp) {
		if ((!hashTags.isEmpty()) // check whether tweet already exists or not
				&& getSecondsDiff(timeStamp,
						parseTwitterUTC(hashTags.peek().timeStamp),
						TimeUnit.SECONDS) > 60) // compare time in seconds
												// between most recent against
												// oldest based on time
			return true;
		else
			return false;
	}

	public long getSecondsDiff(Date date1, Date date2, TimeUnit timeUnit) {
		long diffInMillies = Math.abs(date2.getTime() - date1.getTime());
		return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
	}

	public static void main(String[] args) {
		new AverageDegree().calculateAverageDegree(args[0], args[1]);
	}

}
