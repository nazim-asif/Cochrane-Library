package com.cochranelibrary.domain.port;

import com.cochranelibrary.core.model.Review;

import java.util.List;

/**
 * @author Nazim Uddin Asif
 * @Since 1.0.0
 */
public interface DocumentParserPort {

    List<Review> collect(String topic, String html);
    List<String> getTopicNames(String html);
    List<String> getTopicUrls(String html);
    String getNextLink(String html);
}
