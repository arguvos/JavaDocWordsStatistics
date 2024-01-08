# JavaDocWordsStatistics
JavaDocWordsStatistics is a small utility for collecting words used in Java documentation. Give the JavaDoc utility a website as input and the utility will display all the words that are used in the documentation in order of frequency of their mention.

How the utility works:
1. The utility requests a page with all classes from the JavaDoc
2. A page is requested for each class
3. Each found line on page undergoes preprocessing: foam marks are cleared; special characters are removed
5. The line is divided into words by spaces
6. Words are filtered 
7. Converting all words to lower case
8. Saving words into a structure with statistical information
9. Output of final statistics

The file java8words.txt provides statistics for the words used for the Java 8 documentation.
## Dependencies
JavaDocWordsStatistics uses:
- java 11
- maven
- picocli
- jsoup

## Compile and run
For compile project:
```bash
mvn package
```

Project can be run by cmd:
```bash
JavaDocWordsStatisticsApplication -lk=https://docs.oracle.com/javase/8/docs/api/ -lt=0 -f=5 -d=5000
```
