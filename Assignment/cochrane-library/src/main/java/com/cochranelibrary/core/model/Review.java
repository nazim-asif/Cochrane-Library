package com.cochranelibrary.core.model;

/**
 * @author Nazim Uddin Asif
 * @Since 1.0.0
 */

public record Review(String url, String topic, String title, String author, String date ) {
    @Override
    public String toString() {
        return String.join(" | ", this.url, this.title, this.topic, this.author, this.date);
    }
}
