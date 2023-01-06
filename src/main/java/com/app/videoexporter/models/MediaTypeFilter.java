package com.app.videoexporter.models;

import java.util.ArrayList;
import java.util.List;

public class MediaTypeFilter {
    private List<String> mediaTypes = new ArrayList <String> ();

    public List<String> getMediaTypes() {
        return mediaTypes;
    }
    public MediaTypeFilter setMediaTypes(List<String> mediaTypes) {
        this.mediaTypes = mediaTypes;
        return this;
    }
}
