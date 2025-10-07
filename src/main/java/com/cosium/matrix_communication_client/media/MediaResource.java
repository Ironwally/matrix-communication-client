package com.cosium.matrix_communication_client.media;

import java.io.File;

public interface MediaResource {

  // Must hold a reference to MatrixApi to perform requests

  // Upload media (files, images, etc) to mediaRepository
  String upload(File file, AttachmentConfig config);

  // List media in mediaRepository

  // Delete media from mediaRepository

  // Get uri from media in mediaRepository

  // Download media from mediaRepository

  byte[] download(String mxcUri);

}
