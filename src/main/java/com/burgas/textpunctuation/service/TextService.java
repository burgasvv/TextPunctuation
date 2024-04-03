package com.burgas.textpunctuation.service;

import com.burgas.textpunctuation.exception.TextServletException;
import com.burgas.textpunctuation.manager.PropertiesManager;
import com.burgas.textpunctuation.util.Util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.String.join;
import static java.util.Arrays.stream;

public class TextService {

    private BufferedReader bufferedReader;

    public String getMaxLengthPunctuationString(List<String> punctuation) {

        @SuppressWarnings(Util.UPDATE_OF_COLLECTION_WARNING)
        List<String> maxLengthPunctuationList = new ArrayList<>();

        String maxLengthPunctuation = punctuation.stream()
                .max(Comparator.comparing(String::length)).orElseThrow();

        punctuation.forEach(s -> {
            if (s.length() == maxLengthPunctuation.length())
                maxLengthPunctuationList.add(s);
        });

        return join(Util.DELIMITER, maxLengthPunctuation);
    }

    public String getStringPunctuation(List<String> punctuation) {
        return join(Util.DELIMITER, punctuation);
    }

    public String getText() {
        try {
            bufferedReader = new BufferedReader(
                    new FileReader(PropertiesManager.getProperties().getProperty(Util.TEXT_FILE_KEY))
            );
            return bufferedReader.lines().collect(Collectors.joining());

        } catch (FileNotFoundException e) {
            throw new TextServletException(e.getCause());
        }

    }

    public List<String> findPunctuationSeries(String text)
            throws FileNotFoundException {

        List<String>punctuation = new ArrayList<>();
        String[] symbols = text.split(Util.EMPTY_REGEX);
        StringBuilder stringBuilder = new StringBuilder();

        for (String symbol : symbols) {

            for (char _char = Util.MINIMAL_PUNCTUATION_CHAR; _char < Util.MAXIMAL_PUNCTUATION_CHAR; _char++) {

                if (symbol.equals(String.valueOf(_char))) {
                    stringBuilder.append(symbol);
                    break;
                }
            }

            for (String letter : getLetters()) {

                if (stringBuilder.length() > Util.SINGLE_CHAR && symbol.equals(letter)) {
                    punctuation.add(stringBuilder.toString());
                    stringBuilder.delete(Util.MINIMAL_ELEMENT, stringBuilder.length());
                    break;
                }

                if (symbol.equals(letter)) {

                    if (!stringBuilder.isEmpty())
                        stringBuilder.delete(Util.MINIMAL_ELEMENT, stringBuilder.length());
                }
            }
        }

        return punctuation;
    }

    public List<String> getLetters() throws FileNotFoundException {
        bufferedReader = new BufferedReader(
                new FileReader(PropertiesManager.getProperties().getProperty(Util.LETTERS_FILE_KEY))
        );
        return new ArrayList<>(
                stream(bufferedReader.lines().collect(Collectors.joining()).split(Util.EMPTY_REGEX)).toList()
        );
    }
}
