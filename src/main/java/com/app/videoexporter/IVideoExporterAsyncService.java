package com.app.videoexporter;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface IVideoExporterAsyncService {
    void export(String code, String appToken) throws InterruptedException, IOException, GeneralSecurityException;
}
