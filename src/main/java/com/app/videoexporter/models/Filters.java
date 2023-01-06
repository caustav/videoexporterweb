package com.app.videoexporter.models;

import java.util.List;

public class Filters {
    MediaTypeFilter mediaTypeFilter;

    public MediaTypeFilter getMediaTypeFilter() {
        return mediaTypeFilter;
    }

    public void setMediaTypeFilter(MediaTypeFilter mediaTypeFilter) {
        this.mediaTypeFilter = mediaTypeFilter;
    }

    public Filters Build(List<String> mediaTypes) {
        this.mediaTypeFilter = new MediaTypeFilter().setMediaTypes(mediaTypes);
        return this;
    }
}