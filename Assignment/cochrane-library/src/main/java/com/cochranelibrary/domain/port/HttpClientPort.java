package com.cochranelibrary.domain.port;

import java.io.IOException;

/**
 * @author Nazim Uddin Asif
 * @Since 1.0.0
 */
public interface HttpClientPort {

    String executeHttpGet(String url) throws IOException;
    void close() throws IOException;
}
