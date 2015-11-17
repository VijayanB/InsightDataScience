cd src
echo "Compiling Feature 1 Program !, it takes input from folder tweet_input and store results as ft1.txt under tweet_output"
javac -cp .:org.json.jar TweetsCleaned.java
echo " Executing TweetsCleaned "
java -cp .:org.json.jar TweetsCleaned
echo "Compiling Feature 2 Program !, it takes input from folder tweet_input and store results as ft2.txt under tweet_output"
javac -cp .:org.json.jar AverageDegree.java
echo " Executing AverageDegree"
java -cp .:org.json.jar AverageDegree
echo "*****************Running Junit************************"
echo "Testing TweetsCleaned"
javac -cp ".:org.json.jar:junit-4.10.jar" TestsTweetsCleaned.java
echo " Executing Test Case using Junit"
java -cp ".:org.json.jar:junit-4.10.jar" org.junit.runner.JUnitCore TestsTweetsCleaned
echo "Testing AverageDegree feature"
javac -cp ".:org.json.jar:junit-4.10.jar" TestsAverageDegree.java
echo " Executing Test case for AverageDegree"
java -cp ".:org.json.jar:junit-4.10.jar" org.junit.runner.JUnitCore TestsAverageDegree


