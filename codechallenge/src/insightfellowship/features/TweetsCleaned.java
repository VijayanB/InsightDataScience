package insightfellowship.features;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

import org.json.JSONException;
import org.json.JSONObject;

public class TweetsCleaned {
	private Integer unicodeTweets = 0;

	public Integer getUnicodeTweets() {
		return unicodeTweets;
	}

	public String cleanTweetText(String input) {
		String removedUnicodeString = input.replaceAll("[^\\p{ASCII}]", "");
		if (removedUnicodeString.compareTo(input) != 0) {
			unicodeTweets++;
		}
		return removeEscapeString(removedUnicodeString);
	}

	private String removeEscapeString(String input) {
		return input.trim().replaceAll("[\r\n\t]", " ").replaceAll("\\/", "/")
				.replaceAll("\\\\+", "\\\\").replaceAll("\'", "'")
				.replaceAll("[\"]", "\"").replaceAll(" +", " ");
	}

	public void readTweetsFromFile(String inputFile, String outputFile) {
		PrintWriter writeIntoFile = null;
		try {
			writeIntoFile = new PrintWriter(outputFile);
		} catch (FileNotFoundException e) {
			System.out
					.println("Exception raised while opening file to write output.");
			return;
		}
		// read twitter from input file
		try (BufferedReader br = new BufferedReader(new FileReader(inputFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				JSONObject jsonObject;
				try {
					jsonObject = new JSONObject(line);
					String text = (String) jsonObject.get("text");
					String processedText = cleanTweetText(text);
					String timeStamp = (String) jsonObject.get("created_at");
					writeIntoFile.println(processedText + "  " + "(timestamp: "
							+ timeStamp + ")");
				} catch (JSONException e) {
					continue;
				}

			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found !!");
		} catch (IOException e) {
			System.out.println("Not able to read file");
		}
		writeIntoFile.println("\n" + unicodeTweets
				+ " tweets contained unicode.");

		if (writeIntoFile != null)
			writeIntoFile.close();

	}

	public static void main(String[] args) {
		new TweetsCleaned().readTweetsFromFile("tweets.txt", "result.txt");
	}

}
