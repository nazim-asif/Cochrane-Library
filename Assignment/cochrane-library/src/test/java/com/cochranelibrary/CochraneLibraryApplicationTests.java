package com.cochranelibrary;

import com.cochranelibrary.api.ParserServiceManager;
import com.cochranelibrary.api.ParserSyncService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class CochraneLibraryApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void cochraneLibraryApplicationIntegrationTest() throws Exception {

        CochraneLibraryApplication cochraneLibraryApplication = new CochraneLibraryApplication();
        cochraneLibraryApplication.main(new String[]{});

        assertTrue(true);
    }

}
