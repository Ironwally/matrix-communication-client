package com.cosium.matrix_communication_client.media;

import java.io.File;

public interface MediaResource {

  // Must hold a reference to MatrixApi to perform requests

  // Upload media (files, images, etc) to mediaRepository
  String upload(File file, String filename, String contentType);

  // List media in mediaRepository

  // Delete media from mediaRepository

  // Download media from mediaRepository
  byte[] download(String mxcUri);

}
