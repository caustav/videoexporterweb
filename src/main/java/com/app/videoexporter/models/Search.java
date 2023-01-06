package com.app.videoexporter.models;

import java.util.List;

public class Search {

    String pageSize;

    String pageToken;

    Filters filters;

    public String getPageSize() {
        return pageSize;
    }

    public Search setPageSize(String pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public String getPageToken() {
        return pageToken;
    }

    public Search setPageToken(String pageToken) {
        this.pageToken = pageToken;
        return this;
    }

    public Filters getFilters() {
        return filters;
    }

    public Search setFilters(Filters filtersObject) {
        this.filters = filtersObject;
        return this;
    }

    public static Search BuildQueryWithMediaTypes(List<String> mediaTypes, String pageSize, String pageToken) {
        return new Search().setFilters(new Filters().Build(mediaTypes))
                                        .setPageToken(pageToken).setPageSize(pageSize);
    }
}
