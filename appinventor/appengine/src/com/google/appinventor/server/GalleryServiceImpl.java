// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.server;

import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileReadChannel;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.files.GSFileOptions.GSFileOptionsBuilder;

import com.google.appinventor.server.project.CommonProjectService;
import com.google.appinventor.server.project.youngandroid.YoungAndroidProjectService;
import com.google.appinventor.server.storage.StorageIo;
import com.google.appinventor.server.storage.GalleryStorageIoInstanceHolder;
import com.google.appinventor.shared.rpc.RpcResult;
import com.google.appinventor.shared.rpc.project.FileDescriptor;
import com.google.appinventor.shared.rpc.project.FileDescriptorWithContent;
import com.google.appinventor.shared.rpc.project.NewProjectParameters;
import com.google.appinventor.shared.rpc.project.ProjectRootNode;
import com.google.appinventor.shared.rpc.project.ProjectService;
import com.google.appinventor.shared.rpc.project.GalleryService;

import com.google.appinventor.shared.rpc.project.UserProject;
import com.google.appinventor.shared.rpc.project.youngandroid.YoungAndroidProjectNode;
import com.google.common.collect.Lists;

import com.google.appinventor.server.storage.GalleryStorageIo;
import com.google.appinventor.shared.rpc.project.GalleryApp;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The implementation of the RPC service which runs on the server.
 *
 * <p>Note that this service must be state-less so that it can be run on
 * multiple servers.
 *
 */
public class GalleryServiceImpl extends OdeRemoteServiceServlet implements GalleryService {

  private static final Logger LOG = Logger.getLogger(ProjectServiceImpl.class.getName());

  private static final long serialVersionUID = -8316312003804169166L;

  private final transient GalleryStorageIo galleryStorageIo = GalleryStorageIoInstanceHolder.INSTANCE;

  /**
   * Creates a new gallery app
   * UserProject is the AI Project
   * @param title gallery title of app
   * @param description description of app
   * NEED TO DEAL WITH IMAGE
   *
   * @return a {@link GalleryApp} for new galleryApp
   */
  @Override
  public long publishApp(long projectId, String title, String description) {
    // use UserProject id to create .aia file and store to gcs
    // ...
    return galleryStorageIo.createGalleryApp(title,description, projectId);
  }
   /**
   * Returns an array of gallery Apps
   *
   * @return gallery apps found by the back-end
   */
  @Override
  public List<GalleryApp> getRecentApps(int start,int count) {
    return galleryStorageIo.getRecentGalleryApps(start,count);
 
  }
  @Override
  public GalleryApp getApp(long galleryId) {
    return galleryStorageIo.getGalleryApp(galleryId);
  }

  @Override
  public List<GalleryApp> findApps(String keywords, int start, int count) {
    return null;
  }
  @Override
  public List<GalleryApp> getMostDownloadedApps(int start, int count) {
    return null;
  }
  @Override
  public void deleteApp(long galleryId) {
  }
  @Override
  public Boolean storeAIAtoCloud(long projectId) {
    FileService fileService = FileServiceFactory.getFileService();
    GSFileOptionsBuilder optionsBuilder = new GSFileOptionsBuilder()
    .setBucket("galleryai2")
    .setKey("my_object_within_ai2")
    .setAcl("public-read")
    .setMimeType("text/html")
    .addUserMetadata("myfield1", "my field value");

    // Create your object
    try {
      AppEngineFile writableFile = fileService.createNewGSFile(optionsBuilder.build());
      // Open a channel to write to it
      boolean lock = false;
      FileWriteChannel writeChannel =
          fileService.openWriteChannel(writableFile, lock);
      // Different standard Java ways of writing to the channel
      // are possible. Here we use a PrintWriter:
      PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
      out.println("The woods are lovely dark and deep.");
      out.println("But I have promises to keep.");
      // Close without finalizing and save the file path for writing later
      out.close();
      String path = writableFile.getFullPath();
      // Write more to the file in a separate request:
      writableFile = new AppEngineFile(path);
      // Lock the file because we intend to finalize it and
      // no one else should be able to edit it
      lock = true;
      writeChannel = fileService.openWriteChannel(writableFile, lock);
      // This time we write to the channel directly
      writeChannel.write(ByteBuffer.wrap
                ("And miles to go before I sleep.".getBytes()));

      // Now finalize
      writeChannel.closeFinally();
      
    } catch (IOException e) {
      // TODO Auto-generated catch block
      LOG.log(Level.INFO, "FAILED GCS");
      e.printStackTrace();
    }
    LOG.log(Level.INFO, "LEAVING GCS");
    return true;
  }

}
