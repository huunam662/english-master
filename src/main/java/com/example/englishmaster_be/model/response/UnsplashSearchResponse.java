package com.example.englishmaster_be.model.response;

import java.util.List;

public class UnsplashSearchResponse {
    private List<UnsplashResult> results;

    public List<UnsplashResult> getResults() {
        return results;
    }

    public void setResults(List<UnsplashResult> results) {
        this.results = results;
    }
}

