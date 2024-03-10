package com.cochranelibrary.api;

import com.cochranelibrary.core.model.Review;
import com.cochranelibrary.domain.DocumentParserService;
import com.cochranelibrary.domain.HttpClientService;
import com.cochranelibrary.domain.ReportGeneratorService;
import com.cochranelibrary.domain.exception.ReviewNotFoundException;
import com.cochranelibrary.domain.exception.TopicNamesNotFoundException;
import com.cochranelibrary.domain.port.DocumentParserPort;
import com.cochranelibrary.domain.port.HttpClientPort;
import com.cochranelibrary.domain.port.ReportGenerationPort;
import com.cochranelibrary.infrastructure.config.PropertyConfig;
import com.cochranelibrary.infrastructure.config.PropertyKeySource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author Nazim Uddin Asif
 * @Since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ParserSyncService implements ParserServiceManager {
    private final DocumentParserPort documentParserPort = new DocumentParserService();
    private final ReportGenerationPort reportGeneratorPort = new ReportGeneratorService();
    private final HttpClientPort httpClientService = new HttpClientService();

    private final PropertyConfig propertyConfig;

    public ParserSyncService() {
        propertyConfig = new PropertyConfig();

    }

    public void parse() {
        try {

            String responseString = httpClientService.executeHttpGet(propertyConfig.getPropertyValue(PropertyKeySource.COCHRANE_LIBRARY_URL));
            List<String> topicUrls = documentParserPort.getTopicUrls(responseString);
            List<String> topicNames = documentParserPort.getTopicNames(responseString);

            if (topicNames.isEmpty() || topicNames.size() != topicUrls.size())
                throw new TopicNamesNotFoundException("Topic Names and URLs not aligned or not properly scraped: Topic Names: " + topicNames.size() + " Topic URLs: " + topicUrls.size());

            int numTopics = topicNames.size();
            StringBuilder reportBuilder = new StringBuilder();
            for (int topicIndex = 0; topicIndex < numTopics; topicIndex++) {

                final String topicNameFinal = topicNames.get(topicIndex);
                String nextUrl = topicUrls.get(topicIndex);

                while (Objects.nonNull(nextUrl)) {

                    try {
                        String responseStr = httpClientService.executeHttpGet(nextUrl);
                        List<Review> reviews = documentParserPort.collect(topicNameFinal, responseStr);

                        if (reviews.isEmpty())
                            throw new ReviewNotFoundException("Review Titles and URLs not aligned or scraped in topic "
                                    + topicNameFinal + "\n"
                                    + " at URL: " + nextUrl);

                        prepareReportBuilder(reviews, reportBuilder);

                        nextUrl = documentParserPort.getNextLink(responseStr);

                    } catch (RuntimeException e) {
                        log.error("Failed to execute response in topic: " + topicNameFinal, e);
                    }
                }
                reportGeneratorPort.generateFile(reportBuilder, topicNameFinal);

            }

        } catch (Exception e) {
            log.error("Unable to connect to Cochrane Library, Internet may be disconnected", e);
        } finally {
            try {
                httpClientService.close();
            } catch (IOException e) {
                log.error("Exception during close client: " + e);
            }
        }
        log.info("----finished task----");


    }

    private void prepareReportBuilder(List<Review> reviews, StringBuilder reportBuilder) {
        for (Review review : reviews) {
            reportBuilder.append(review.toString()).append("\n\n");
        }
    }

}
