package org.example;

import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.util.HashMap;
import java.util.Map;

@CommandLine.Command(name = "jdws", mixinStandardHelpOptions = true, version = "1.0")
public class JavaDocWordsStatisticsApplication implements Runnable {

    @Option(names = {"-lk", "--link"},
            description = "Link to the site where statistics will be collected.",
            defaultValue = "https://docs.oracle.com/javase/8/docs/api/")
    private String link;

    @Option(names = {"-lt", "--limit"},
            description = "Limit of visited links.")
    private int limit;

    @Option(names = {"-f", "--filter"},
            description = "Words that appear less than the specified number of times will not be present in the final statistics.",
            defaultValue = "5")
    private int filter;

    @Option(names = {"-d", "--delay"},
            description = "Delay between requests to the site.",
            defaultValue = "5000")
    private long delayMillis;

    public void run() {
        StatisticsCollector statisticsCollector = new StatisticsCollector(link, limit, filter, delayMillis);
        HashMap<String, Integer> wordToStatMap = null;

        try {
            wordToStatMap = statisticsCollector.scan();
        } catch (Exception e) {
            System.out.println("Fail to scan link " + link + "\nError: " + e.getMessage());
            return;
        }

        System.out.println("Found " + wordToStatMap.size() + " words: ");
        for (Map.Entry<String, Integer> wordToStat : wordToStatMap.entrySet()){
            System.out.println(wordToStat.getKey() + " = " + wordToStat.getValue());
        }
    }

    public static void main(String[] args) {
        int exitCode = new CommandLine(new JavaDocWordsStatisticsApplication()).execute(args);
        System.exit(exitCode);
    }

}