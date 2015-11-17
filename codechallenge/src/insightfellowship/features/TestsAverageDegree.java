package insightfellowship.features;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class TestsAverageDegree {

	private AverageDegree avgTweet;
	
	private static String OUTPUT_FILE_NAME = "output.txt";  //constants
	private static String INPUT_FILENAME = "sampleAverageCount.txt";  //constants

	@Before
	public void setUp() {
		avgTweet = new AverageDegree();
	}

	@Test
	public void testAverageDegreeTweets() {
		avgTweet.calculateAverageDegree(INPUT_FILENAME, OUTPUT_FILE_NAME);
		assertRollingAverage();

	}

	private void assertRollingAverage() {
		List<String> result = new ArrayList<>();
		result.add("1.0");
		result.add("2.0");
		result.add("2.0");
		result.add("2.0");
		result.add("2.0");
		result.add("1.67");
		Iterator<String>  itr = result.iterator();
		// read file
		try (BufferedReader br = new BufferedReader(new FileReader(
				OUTPUT_FILE_NAME))) {
			String line;
			while ((line = br.readLine()) != null) {
				Assert.assertEquals(line,itr.next());
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
