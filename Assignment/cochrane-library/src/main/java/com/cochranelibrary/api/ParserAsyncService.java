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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author Nazim Uddin Asif
 * @Since 1.0.0
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ParserAsyncService implements ParserServiceManager{
    private final DocumentParserPort documentParserPort = new DocumentParserService();
    private final ReportGenerationPort reportGeneratorPort = new ReportGeneratorService();
    private final HttpClientPort httpClientService = new HttpClientService();

    private final PropertyConfig propertyConfig;

    public ParserAsyncService() {
        propertyConfig = new PropertyConfig();
    }

    private final ExecutorService executor = Executors.newFixedThreadPool(10);

    public void parse() {
        try {
            String responseString = httpClientService.executeHttpGet(propertyConfig.getPropertyValue(PropertyKeySource.COCHRANE_LIBRARY_URL));
            List<String> topicUrls = documentParserPort.getTopicUrls(responseString);
            List<String> topicNames = documentParserPort.getTopicNames(responseString);

            if (topicNames.isEmpty() || topicNames.size() != topicUrls.size())
                throw new TopicNamesNotFoundException("Topic Names and URLs not aligned or not properly scraped: Topic Names: " + topicNames.size() + ", Topic URLs: " + topicUrls.size());

            CompletableFuture<?>[] futures = new CompletableFuture<?>[topicNames.size()];
            for (int topicIndex = 0; topicIndex < topicNames.size(); topicIndex++) {
                final String topicName = topicNames.get(topicIndex);
                String nextUrl = topicUrls.get(topicIndex);
                futures[topicIndex] = processTopicAsync(topicName, nextUrl);
            }

            // Wait for all topics to be processed
            CompletableFuture.allOf(futures).join();

        } catch (Exception e) {
            log.error("Unable to connect to Cochrane Library, Internet may be disconnected", e);
        } finally {
            try {
                httpClientService.close();
            } catch (IOException e) {
                log.error("Exception during close client: " + e);
            }
            executor.shutdown();
        }

        log.error("----finished task----");

    }

    private CompletableFuture<Void> processTopicAsync(final String topicName, String url) {
        return CompletableFuture.runAsync(() -> {
            try {
                String nextUrlLocal = url;
                StringBuilder reportBuilder = new StringBuilder();
                while (Objects.nonNull(nextUrlLocal)) {
                    String responseStr = httpClientService.executeHttpGet(nextUrlLocal);
                    List<Review> reviews = documentParserPort.collect(topicName, responseStr);

                    if (reviews.isEmpty())
                        throw new ReviewNotFoundException("Review Titles and URLs not aligned or scraped in topic " + topicName + "\n" + " at URL: " + nextUrlLocal);

                    synchronized (reportBuilder) {
                        prepareReportBuilder(reviews, reportBuilder);
                    }

                    nextUrlLocal = documentParserPort.getNextLink(responseStr);
                }

                reportGeneratorPort.generateFile(reportBuilder, topicName);
            } catch (RuntimeException e) {
                log.error("Failed to execute response for topic " + topicName, e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, executor);
    }

    private void prepareReportBuilder(List<Review> reviews, StringBuilder reportBuilder) {
        for (Review review : reviews) {
            reportBuilder.append(review.toString()).append("\n\n");
        }
    }

}
