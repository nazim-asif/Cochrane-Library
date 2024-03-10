package com.cochranelibrary.domain.port;

import java.io.IOException;

/**
 * @author Nazim Uddin Asif
 * @Since 1.0.0
 */
public interface ReportGenerationPort {

    void generateFile(StringBuilder document, String topicNameFinal) throws IOException;
}
