package de.qabel.qabelbox.storage.server;

import java.io.InputStream;

import de.qabel.qabelbox.communication.callbacks.DownloadRequestCallback;
import de.qabel.qabelbox.communication.callbacks.JSONModelCallback;
import de.qabel.qabelbox.communication.callbacks.RequestCallback;
import de.qabel.qabelbox.communication.callbacks.UploadRequestCallback;
import de.qabel.qabelbox.storage.model.BoxQuota;

public interface BlockServer {

    String API_QUOTA = "/api/v0/quota/";

    void downloadFile(String prefix, String path, String ifModified, DownloadRequestCallback callback);

    void uploadFile(String prefix, String name, InputStream input, String eTag, UploadRequestCallback callback);

    void deleteFile(String prefix, String path, RequestCallback callback);

    void getQuota(JSONModelCallback<BoxQuota> callback);

    String urlForFile(String prefix, String path);
}
