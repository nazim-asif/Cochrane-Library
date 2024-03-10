package com.cochranelibrary.core.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Service;

/**
 * @author Nazim Uddin Asif
 * @Since 1.0.0
 */
@Getter
@Setter
@Service
public class RegexPattern {

    private String authorPattern =  "<div class=\"search-result-authors\">\\s*<div>(.*?)</div>";
    private String authorAnomalyPattern =  "<sup>a</sup>";
    private String datePattern = "<div class=\"search-result-date\">\\s*<div>(.*?)</div>";
    private String urlPattern = "<a\\s+target=\"_blank\"\\s+href=\"(/[^\\s\"]+)\"";
    private String titlePattern = "<h3 class=\"result-title\">\\s*<a[^>]*>(.*?)</a>";
    private String topicNamePattern = "<button[^>]*>(.*?)</button></a>";
    private String topicUrlsPattern = "<li class=\"browse-by-list-item\">\\s*<a href=\"([^\"]+)\"";
    private String nextUrlPattern = "(https://www\\.cochranelibrary\\.com/en/search[^\\s\"]+&cur=2\")";

}
