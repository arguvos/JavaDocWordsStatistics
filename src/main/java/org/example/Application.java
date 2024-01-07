package org.example;

import picocli.CommandLine;
import picocli.CommandLine.Option;

import java.util.HashMap;
import java.util.Map;

@CommandLine.Command(name = "example", mixinStandardHelpOptions = true, version = "Picocli example 4.0")
public class Application implements Runnable {

    @Option(names = {"-lk", "--link"},
            description = "Verbose mode. Helpful for troubleshooting. Multiple -v options increase the verbosity.",
            defaultValue = "https://docs.oracle.com/javase/8/docs/api/")
    private String link;

    @Option(names = {"-lt", "--limit"},
            description = "Verbose mode. Helpful for troubleshooting. Multiple -v options increase the verbosity.")
    private int limit;

    @Option(names = {"-f", "--filter"},
            description = "Verbose mode. Helpful for troubleshooting. Multiple -v options increase the verbosity.",
            defaultValue = "5")
    private int filter;

    @Option(names = {"-d", "--delay"},
            description = "Verbose mode. Helpful for troubleshooting. Multiple -v options increase the verbosity.",
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
        int exitCode = new CommandLine(new Application()).execute(args);
        System.exit(exitCode);
    }

}