package com.cochranelibrary;

import com.cochranelibrary.api.ParserAsyncService;
import com.cochranelibrary.api.ParserServiceManager;
import com.cochranelibrary.api.ParserSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;

@SpringBootApplication
@RequiredArgsConstructor
public class CochraneLibraryApplication {

    public static void main(String[] args) throws IOException {

//        SpringApplication.run(CochraneLibraryApplication.class, args);
//        ParserServiceManager parserServiceManager = new ParserAsyncService(); // for Async parser
        ParserServiceManager parserServiceManager = new ParserSyncService(); // for Sync parser
        parserServiceManager.parse();
    }

}
