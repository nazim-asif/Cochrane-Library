package com.cochranelibrary.domain;

import com.cochranelibrary.core.model.Review;
import com.cochranelibrary.domain.port.DocumentParserPort;
import com.cochranelibrary.infrastructure.config.PropertyConfig;
import com.cochranelibrary.infrastructure.config.PropertyKeySource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Nazim Uddin Asif
 * @Since 1.0.0
 */
@Service
public class DocumentParserService implements DocumentParserPort {
    private final PropertyConfig propertyConfig;

    public DocumentParserService() {
        propertyConfig = new PropertyConfig();
    }

    @Override
    public List<Review> collect(String topic, String html) {

        List<String> titles = getTitles(html);
        List<String> urls = getUrls(html);
        List<String> authors = getAuthors(html);
        List<String> dates = getDates(html);

        List<Review> reviews = new ArrayList<>();
        for (int i = 0; i < titles.size(); i++) {
            reviews.add(new Review(urls.get(i),titles.get(i),topic,authors.get(i),dates.get(i)));
        }
        return reviews;
    }

    @Override
    public List<String> getTopicNames(String html) {

        Matcher nameMatcher = getMatcher(html, propertyConfig.getPropertyValue(PropertyKeySource.TOPIC_NAME_PATTERN));

        List<String> topicNames= new ArrayList<>();
        while (nameMatcher.find()) {
            String extractedText = nameMatcher.group(1);
            String decodedText = extractedText.replace("&amp;", "&");
            topicNames.add(decodedText);
        }
        return topicNames;
    }

    @Override
    public List<String> getTopicUrls(String html) {

        Matcher urlMatcher = getMatcher(html, propertyConfig.getPropertyValue(PropertyKeySource.TOPIC_URL_PATTERN));

        List<String> topicUrls = new ArrayList<>();
        while (urlMatcher.find()) {
            topicUrls.add(urlMatcher.group(1));
        }
        return topicUrls;
    }

    @Override
    public String getNextLink(String html) {

        Matcher matcher = getMatcher(html, "<a\\s+href=\"([^\"]+)\">Next</a>");
        String nextUrl = null;
        if (matcher.find()) {
            nextUrl = matcher.group(1).replace("&amp;","&");
        }
        return nextUrl;
    }

    private List<String> getTitles(String responseStr) {

        Matcher titleMatcher = getMatcher(responseStr, propertyConfig.getPropertyValue(PropertyKeySource.TITLE_PATTERN));

        List<String> reviewTitles = new ArrayList<>();
        while (titleMatcher.find()) {
            reviewTitles.add(titleMatcher.group(1));
        }
        return reviewTitles;
    }

    private List<String> getAuthors(String responseStr) {

        Matcher reviewAuthorsMatcher = getMatcher(responseStr, propertyConfig.getPropertyValue(PropertyKeySource.AUTHOR_PATTERN));
        List<String> reviewAuthors = new ArrayList<>();
        while (reviewAuthorsMatcher.find()) {
            String extractedAuthors = reviewAuthorsMatcher.group(1);
            String cleanedAuthors = extractedAuthors.replaceAll(propertyConfig.getPropertyValue(PropertyKeySource.AUTHOR_PATTERN_ANOMALY), "");
            reviewAuthors.add(cleanedAuthors);
        }
        return reviewAuthors;
    }

    private List<String> getDates(String responseStr) {

        Matcher reviewDatesMatcher = getMatcher(responseStr, propertyConfig.getPropertyValue(PropertyKeySource.DATE_PATTERN));

        List<String> reviewDates = new ArrayList<>();
        while (reviewDatesMatcher.find()) {
            reviewDates.add(convertDate(reviewDatesMatcher.group(1)));
        }
        return reviewDates;
    }

    private List<String> getUrls(String responseString) {

        Matcher urlMatcher = getMatcher(responseString, propertyConfig.getPropertyValue(PropertyKeySource.URL_PATTERN));

        List<String> reviewUrls = new ArrayList<>();
        while (urlMatcher.find()) {
            String extractedUrl = urlMatcher.group(1);
            String replacedUrl = extractedUrl.replaceFirst("/cdsr", "http://onlinelibrary.wiley.com");
            reviewUrls.add(replacedUrl);
        }
        return reviewUrls;
    }

    private Matcher getMatcher(String responseString, String pattern){
        Pattern urlPattern = Pattern.compile(pattern);
        return urlPattern.matcher(responseString);
    }

    private String convertDate(String date) {
        String[] breakdown = date.trim().split(" ");
        String delim = "-";
        String month = "";
        switch(breakdown[1]) {
            case "January": month = "01"; break;
            case "February": month = "02"; break;
            case "March": month = "03"; break;
            case "April": month = "04"; break;
            case "May": month = "05"; break;
            case "June": month = "06"; break;
            case "July": month = "07"; break;
            case "August": month = "08"; break;
            case "September": month = "09"; break;
            case "October": month = "10"; break;
            case "November": month = "11"; break;
            case "December": month = "12"; break;
            default: month = "01";
        }
        return String.join(delim, breakdown[2], month, breakdown[0]);
    }
}
