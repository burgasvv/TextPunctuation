package com.burgas.textpunctuation.servlet;

import com.burgas.textpunctuation.exception.TextServletException;
import com.burgas.textpunctuation.manager.PropertiesManager;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

import static java.net.URLEncoder.encode;
import static java.util.Arrays.*;


@WebServlet(name = "textServlet", urlPatterns = "/text-servlet")
public class TextServlet extends HttpServlet {

    public static final String TEXT_FILE_KEY = "textFile";
    public static final String LETTERS_FILE_KEY = "lettersFile";
    public static final String TEXT_JSP_FILE_KEY = "textJspFile";
    public static final String ATTRIBUTE_NAME_OF_TEXT = "text";
    public static final String ATTRIBUTE_NAME_OF_PUNCTUATION = "punctuation";
    public static final String ATTRIBUTE_NAME_OF_AMOUNT = "amount";
    public static final String COOKIE_NAME_OF_MAX_LENGTH_PUNCTUATION = "maxLengthPunctuation";
    public static final String UPDATE_OF_COLLECTION_WARNING = "MismatchedQueryAndUpdateOfCollection";
    public static final String SAME_PARAMETER_VALUE_WARNING = "SameParameterValue";
    public static final String DELIMITER = "▬▬";
    public static final int MINIMAL_PUNCTUATION_CHAR = 32;
    public static final int MAXIMAL_PUNCTUATION_CHAR = 65;
    public static final int SINGLE_CHAR = 1;
    public static final int MINIMAL_ELEMENT = 0;
    public static final int DURATION_DURING_THE_SESSION_VALUE = -1;
    private BufferedReader bufferedReader;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<String> punctuation = new ArrayList<>();

        findPunctuationSeries(getText(), punctuation);
        requestProcess(req, punctuation);
        addCookie(resp, createCookie(getMaxLengthPunctuationString(punctuation)));
        forwardRequest(req, resp, PropertiesManager.getProperties().getProperty(TEXT_JSP_FILE_KEY));
    }

    private String getCookieValueByName(HttpServletRequest req, Cookie myCookie) {
        return stream(req.getCookies())
                .filter(cookie -> myCookie.getName().equals(cookie.getName()))
                .map(Cookie::getValue).findAny().orElseThrow();
    }

    private Cookie createCookie(String maxLengthPunctuationString) {
        return new Cookie(
                COOKIE_NAME_OF_MAX_LENGTH_PUNCTUATION,
                encode(maxLengthPunctuationString, StandardCharsets.UTF_8)
        );
    }

    private void addCookie(HttpServletResponse resp, Cookie cookie) {
        cookie.setMaxAge(DURATION_DURING_THE_SESSION_VALUE);
        resp.addCookie(cookie);
    }

    private String getMaxLengthPunctuationString(List<String> punctuation) {

        @SuppressWarnings(UPDATE_OF_COLLECTION_WARNING)
        List<String>maxLengthPunctuationList = new ArrayList<>();

        String maxLengthPunctuation = punctuation.stream()
                .max(Comparator.comparing(String::length)).orElseThrow();

        punctuation.forEach(s -> {
            if (s.length() == maxLengthPunctuation.length())
                maxLengthPunctuationList.add(s);
        });

        return String.join(DELIMITER, maxLengthPunctuation);
    }

    private String getStringPunctuation(List<String> punctuation) {
        return String.join(DELIMITER, punctuation);
    }

    private String getText() {
        try {
            bufferedReader = new BufferedReader(
                    new FileReader(PropertiesManager.getProperties().getProperty(TEXT_FILE_KEY))
            );
            return bufferedReader.lines().collect(Collectors.joining());

        } catch (FileNotFoundException e) {
            throw new TextServletException(e.getCause());
        }

    }

    private void findPunctuationSeries(String text, List<String> punctuation)
            throws FileNotFoundException {

        String[] symbols = text.split("");
        StringBuilder stringBuilder = new StringBuilder();

        for (String symbol : symbols) {

            for (char _char = MINIMAL_PUNCTUATION_CHAR; _char < MAXIMAL_PUNCTUATION_CHAR; _char++) {

                if (symbol.equals(String.valueOf(_char))) {
                    stringBuilder.append(symbol);
                    break;
                }
            }

            for (String letter : getLetters()) {

                if (stringBuilder.length() > SINGLE_CHAR && symbol.equals(letter)) {
                    punctuation.add(stringBuilder.toString());
                    stringBuilder.delete(MINIMAL_ELEMENT, stringBuilder.length());
                    break;
                }

                if (symbol.equals(letter)) {

                    if (!stringBuilder.isEmpty())
                        stringBuilder.delete(MINIMAL_ELEMENT, stringBuilder.length());
                }
            }
        }
    }

    private List<String> getLetters() throws FileNotFoundException {
        bufferedReader = new BufferedReader(
                new FileReader(PropertiesManager.getProperties().getProperty(LETTERS_FILE_KEY))
        );
        return new ArrayList<>(
                stream(bufferedReader.lines().collect(Collectors.joining()).split("")).toList()
        );
    }

    private void requestProcess(HttpServletRequest req, List<String> punctuation) {
        req.setAttribute(ATTRIBUTE_NAME_OF_TEXT, getText());
        req.setAttribute(ATTRIBUTE_NAME_OF_PUNCTUATION, getStringPunctuation(punctuation));
        req.setAttribute(ATTRIBUTE_NAME_OF_AMOUNT, punctuation.size());
    }

    private void forwardRequest(HttpServletRequest req, HttpServletResponse resp,
                                       @SuppressWarnings(SAME_PARAMETER_VALUE_WARNING) String path)
            throws ServletException, IOException {
        req.getRequestDispatcher(path).forward(req, resp);
    }

    @Override
    public void destroy() {
        try {
            bufferedReader.close();

        } catch (IOException e) {
            throw new TextServletException(e.getCause());
        }
    }
}
