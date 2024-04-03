package com.burgas.textpunctuation.servlet;

import com.burgas.textpunctuation.manager.PropertiesManager;
import com.burgas.textpunctuation.service.TextService;
import com.burgas.textpunctuation.util.Util;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static java.net.URLEncoder.encode;

@WebServlet(name = "textServlet", urlPatterns = "/text-servlet")
public class TextServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        TextService textService = new TextService();
        List<String> punctuation = textService.findPunctuationSeries(textService.getText());

        req.setAttribute(Util.ATTRIBUTE_NAME_OF_TEXT, textService.getText());
        req.setAttribute(Util.ATTRIBUTE_NAME_OF_PUNCTUATION, textService.getStringPunctuation(punctuation));
        req.setAttribute(Util.ATTRIBUTE_NAME_OF_AMOUNT, punctuation.size());

        Cookie cookie = new Cookie(
                Util.COOKIE_NAME_OF_MAX_LENGTH_PUNCTUATION,
                encode(textService.getMaxLengthPunctuationString(punctuation), StandardCharsets.UTF_8)
        );
        cookie.setMaxAge(Util.DURATION_DURING_THE_SESSION_VALUE);
        resp.addCookie(cookie);

        req.getRequestDispatcher(PropertiesManager.getProperties().getProperty(Util.TEXT_JSP_FILE_KEY))
                        .forward(req, resp);
    }
}
