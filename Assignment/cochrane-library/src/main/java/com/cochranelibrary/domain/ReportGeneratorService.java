package com.cochranelibrary.domain;

import com.cochranelibrary.domain.port.ReportGenerationPort;
import com.cochranelibrary.infrastructure.config.PropertyConfig;
import com.cochranelibrary.infrastructure.config.PropertyKeySource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Nazim Uddin Asif
 * @Since 1.0.0
 */
@RequiredArgsConstructor
@Service
@Slf4j
public class ReportGeneratorService implements ReportGenerationPort {

    private final PropertyConfig propertyConfig;

    public ReportGeneratorService(){
        this.propertyConfig = new PropertyConfig();
    }

    @Override
    public void generateFile(StringBuilder document, String topicNameFinal){

        Path filePath = Paths.get(propertyConfig.getPropertyValue(PropertyKeySource.OUTPUT_FILE_NAME));

        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
                log.info("File created: cochrane_reviews.txt");
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile()));
            writer.write(String.valueOf(document));
            writer.close();
            log.info("Content has been written to the file successfully for "+topicNameFinal+". here: "+ filePath.toAbsolutePath());
        } catch (IOException e) {
            log.error("Error writing to the file: " + e.getMessage());
        }
    }
}
