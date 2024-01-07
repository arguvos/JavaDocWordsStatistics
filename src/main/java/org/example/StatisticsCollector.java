package org.example;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

import java.util.*;
import java.util.stream.Collectors;

public class StatisticsCollector {
    public static final String ALL_CLASSES_HTML = "/allclasses-frame.html";
    public static final String PUNCT_REGEX = "\\p{Punct}";
    public static final String NBSP_REGEX = "\u00a0";
    public static final String SPACE_REGEX = "\\s+";
    public static final String ONLY_LETTERS_REGEXP = "/[^a-zA-Z]+/g";
    public static final int MIN_WORD_LENGTH = 3;
    private final String link;
    private final int limit;
    private final int threshold;
    private final long delayMillis;

    public StatisticsCollector(String link, int limit, int threshold, long delayMillis) {
        this.link = link;
        this.limit = limit;
        this.threshold = threshold;
        this.delayMillis = delayMillis;
    }

    public HashMap<String, Integer> scan() throws Exception {
        HashMap<String, Integer> wordToStatMap = new HashMap<>();
        List<String> pages = getPages();
        System.out.println("Found " + pages.size() + " pages");

        for (int i = 0; i < pages.size() && (limit == 0 || i < limit); i++) {
            System.out.println("Scan " + pages.get(i) + " page");
            Elements body = Jsoup.connect(link + pages.get(i)).get().getElementsByTag("body");
            collectWords(wordToStatMap, body.get(0).childNodes());
            System.out.println("Found " + wordToStatMap.size() + " words");
            try {
                System.out.println("Delay before start scan next page");
                Thread.sleep(delayMillis);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return sortAndFilter(wordToStatMap);
    }

    private List<String> getPages() throws Exception {
        Document doc = Jsoup.connect(link + ALL_CLASSES_HTML).get();
        Optional<Node> classNode = doc.getElementsByClass("indexContainer")
                .get(0)
                .childNodes().stream()
                .filter(e -> e instanceof Element).findFirst();

        if (classNode.isEmpty()) {
            System.out.println("Wrong structure of page, statistic can't be calculated");
            return Collections.emptyList();
        }

        return classNode.get().childNodes().stream()
                .filter(e -> e instanceof Element)
                .map(e -> e.childNodes().get(0).attributes().get("href")).toList();
    }

    //DFS
    private static void collectWords(Map<String, Integer> wordToStatMap, List<Node> nodes) {
        for (Node node : nodes) {
            if (node instanceof TextNode && !((TextNode) node).text().isBlank()) {
                String[] wordsInNode = ((TextNode) node).text()
                        .replaceAll(PUNCT_REGEX, "")
                        .replaceAll(NBSP_REGEX, "")
                        .split(SPACE_REGEX);
                List<String> words = filter(Arrays.asList(wordsInNode));
                words = toLowCase(words);
                merge(wordToStatMap, words);
            }
            collectWords(wordToStatMap, node.childNodes());
        }
    }

    private static List<String> filter(List<String> list) {
        return list.stream().filter(e -> e.length() > MIN_WORD_LENGTH
                && !e.matches(ONLY_LETTERS_REGEXP)
                && !hasStrangeCapitalLetter(e)
        ).collect(Collectors.toList());
    }

    private static boolean hasStrangeCapitalLetter(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (Character.isDigit(str.charAt(i)) || (Character.isUpperCase(str.charAt(i)) && i != 0)) {
                return true;
            }
        }
        return false;
    }

    private static List<String> toLowCase(List<String> list) {
        return list.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }

    private static void merge(Map<String, Integer> wordToStatMap, List<String> words) {
        for (String word : words) {
            wordToStatMap.put(word, wordToStatMap.getOrDefault(word, 0) + 1);
        }
    }

    private LinkedHashMap<String, Integer> sortAndFilter(HashMap<String, Integer> wordToStat) {
        return wordToStat.entrySet().stream()
                .filter(e -> e.getValue() > threshold)
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));
    }
}
