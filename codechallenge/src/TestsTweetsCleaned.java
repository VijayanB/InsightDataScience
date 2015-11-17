import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestsTweetsCleaned {

	TweetsCleaned cleanTweetsList;
	String OUTPUT_FILE_NAME = "output.txt";

	@Before
	public void setUp() {
		cleanTweetsList = new TweetsCleaned();
	}

	@Test
	public void testUnicodeTweets() {
		 System.out.println("Testing cleaning of Unicode Tweets");
		cleanTweetsList.readTweetsFromFile("../tweet_input/sampleUnicode.txt",
				OUTPUT_FILE_NAME);
		assertUnicodeTweets();
		 System.out.println("Test case passed");
		

	}
	
	@Test
	public void testUnicodeTweetsLine() {
                System.out.println("Testing Number of Unicode Tweet");
		cleanTweetsList.readTweetsFromFile("../tweet_input/sampleUnicode.txt",
				OUTPUT_FILE_NAME);
		assertUnicodeLinesCount();
                System.out.println("Test case passed");
		

	}

	private void assertUnicodeLinesCount() {
		Assert.assertEquals(cleanTweetsList.getUnicodeTweets(), new Integer(6));
	}

	private void assertUnicodeTweets() {
		// read file
		try (BufferedReader br = new BufferedReader(new FileReader(
				OUTPUT_FILE_NAME))) {
			String line;
			while ((line = br.readLine()) != null) {
				Assert.assertTrue(line.chars().allMatch(c -> c < 128));
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found !!");
		} catch (IOException e) {
			System.out.println("Not able to read file");
		}

	}

	@After
	public void tearDown() {
		File outputFile = new File(OUTPUT_FILE_NAME);
		if (outputFile.exists()) {
			outputFile.delete();
		}
	}

}
