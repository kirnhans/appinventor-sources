// -*- mode: java; c-basic-offset: 2; -*-
// Copyright 2009-2011 Google, All Rights reserved
// Copyright 2011-2012 MIT, All rights reserved
// Released under the MIT License https://raw.github.com/mit-cml/app-inventor/master/mitlicense.txt

package com.google.appinventor.client;

import static com.google.appinventor.client.Ode.MESSAGES;

import java.util.ArrayList;
import java.util.List;

import com.google.appinventor.client.explorer.project.Project;
import com.google.appinventor.client.explorer.youngandroid.GalleryList;
import com.google.appinventor.client.youngandroid.TextValidators;
import com.google.appinventor.shared.rpc.project.GalleryApp;
import com.google.appinventor.shared.rpc.project.GalleryAppListResult;
import com.google.appinventor.shared.rpc.project.GalleryComment;
import com.google.appinventor.shared.rpc.project.GallerySettings;
import com.google.appinventor.shared.rpc.project.UserProject;
import com.google.appinventor.shared.rpc.user.Config;


/**
 * Gallery Client is a facade for the ui to talk to the gallery server side.
 * It is a Singleton and has a list of listeners (GalleryPage, GalleryList)
 *
 * @author wolberd@gmail.com (David Wolber)
 * @author vincentaths@gmail.com (Vincent Zhang)
 */
public class GalleryClient {

  private List<GalleryRequestListener> listeners;
  //private GallerySettings settings;

  public static final int REQUEST_FEATURED = 1;
  public static final int REQUEST_RECENT = 2;
  public static final int REQUEST_SEARCH = 3;
  public static final int REQUEST_MOSTLIKED = 4;
  public static final int REQUEST_MOSTDOWNLOADED = 5;
  public static final int REQUEST_MOSTVIEWED = 6;
  public static final int REQUEST_BYDEVELOPER = 7;
  public static final int REQUEST_BYTAG = 8;
  public static final int REQUEST_ALL = 9;
  public static final int REQUEST_REMIXED_TO = 10;

  private static volatile GalleryClient  instance= null;

  public String ENVIRONMENT = null;

  /**
   * constructor
   */
  private GalleryClient () {
    listeners = new ArrayList<GalleryRequestListener>();
  }

  /**
   * @return the instance of GalleryClient
   */
  public static GalleryClient getInstance () {
    if (instance == null) {
      synchronized (GalleryClient.class) {
        instance = new GalleryClient();
      }
    }
    return instance;
  }

  /**
   * add listener to listners list
   * @param listener gallery request listener
   */
  public void addListener(GalleryRequestListener listener) {
    listeners.add(listener);
  }

  /**
   * Returns the gallery settings.
   *
   * @return  gallery settings
   */
  public GallerySettings getGallerySettings() {
    final Ode ode = Ode.getInstance();
    return ode.getGallerySettings();
  }

 /**
  * FindApps calls search and then tells listeners when done
  * @param keywords search keywords
  * @param start staring index for search
  * @param count number of results
  * @param sortOrder currently unused,
  */
  public void FindApps(String keywords, int start, int count, int sortOrder, final boolean refreshable) {
     // Callback for when the server returns us the apps
    final Ode ode = Ode.getInstance();
    final OdeAsyncCallback<GalleryAppListResult> callback = new OdeAsyncCallback<GalleryAppListResult>(
    // failure message
    MESSAGES.gallerySearchError()) {
      @Override
      public void onSuccess(GalleryAppListResult appsResult) {
        // the server has returned us something
        for (GalleryRequestListener listener:listeners) {
          listener.onAppListRequestCompleted(appsResult, REQUEST_SEARCH, refreshable);
        }
      }
    };

    //this is below the call back, but of course it is done first
    ode.getGalleryService().findApps(keywords, start,count,callback);
  }
 /**
  * GetAppsByDeveloper gets apps by developer and then tells listeners when done
  * @param start staring index for search
  * @param count number of results
  * @param developerId id of developer
  */
  public void GetAppsByDeveloper(int start, int count, String developerId) {
    // Callback for when the server returns us the apps
    final Ode ode = Ode.getInstance();
    final OdeAsyncCallback<GalleryAppListResult> callback = new OdeAsyncCallback<GalleryAppListResult>(
    // failure message
    MESSAGES.galleryDeveloperAppError()) {
      @Override
      public void onSuccess(GalleryAppListResult appsResult) {
        // the server has returned us something
        for (GalleryRequestListener listener:listeners) {
          listener.onAppListRequestCompleted(appsResult, REQUEST_BYDEVELOPER, false);
        }
      }
    };
    // This is below the call back, but of course it is done first
    ode.getGalleryService().getDeveloperApps(developerId, start,count,callback);
  }
 /**
  * GetFeatured gets featured apps, currently unimplemented
  * @param start staring index
  * @param count number of results
  * @param sortOrder unused sort order
  */
  public void GetFeatured(int start, int count, int sortOrder, final boolean refreshable) {
    // Callback for when the server returns us the apps
    final Ode ode = Ode.getInstance();
    final OdeAsyncCallback<GalleryAppListResult> callback = new OdeAsyncCallback<GalleryAppListResult>(
    // failure message
    MESSAGES.galleryRecentAppsError()) {
      @Override
      public void onSuccess(GalleryAppListResult appsResult) {
        // the server has returned us something
        for (GalleryRequestListener listener:listeners) {
          listener.onAppListRequestCompleted(appsResult, REQUEST_FEATURED, refreshable);
        }
      }
    };
    // This is below the call back, but of course it is done first
    ode.getGalleryService().getFeaturedApp(start, count, callback);
  }
 /**
  * GetMostRecent gets most recently updated apps then tells listeners
  * @param start staring index
  * @param count number of results
  */
  public void GetMostRecent(int start, int count, final boolean refreshable) {
    // Callback for when the server returns us the apps
    final Ode ode = Ode.getInstance();
    final OdeAsyncCallback<GalleryAppListResult> callback = new OdeAsyncCallback<GalleryAppListResult>(
    // failure message
    MESSAGES.galleryRecentAppsError()) {
      @Override
      public void onSuccess(GalleryAppListResult appsResult) {
        // the server has returned us something
        for (GalleryRequestListener listener:listeners) {
          listener.onAppListRequestCompleted(appsResult, REQUEST_RECENT, refreshable);
        }
      }
    };
    // This is below the call back, but of course it is done first
    ode.getGalleryService().getRecentApps(start, count, callback);
  }
  /**
  * GetMostDownloaded gets the most downloaded apps then tells listeners
  * @param start staring index
  * @param count number of results
  */
  public void GetMostDownloaded(int start, int count, final boolean refreshable) {
    // Callback for when the server returns us the apps
    final Ode ode = Ode.getInstance();
    final OdeAsyncCallback<GalleryAppListResult> callback = new OdeAsyncCallback<GalleryAppListResult>(
    // failure message
    MESSAGES.galleryDownloadedAppsError()) {
      @Override
      public void onSuccess(GalleryAppListResult appsResult) {
        // the server has returned us something
        for (GalleryRequestListener listener:listeners) {
          listener.onAppListRequestCompleted(appsResult, REQUEST_MOSTDOWNLOADED, refreshable);
        }
      }
    };

    // ok, this is below the call back, but of course it is done first
    ode.getGalleryService().getMostDownloadedApps(start,count,callback);
  }
  /**
   * GetRemixedToList gets children list that apps remixed to then tells listeners
   */
  public void GetRemixedToList(GalleryAppListResult appsResult) {
    for (GalleryRequestListener listener:listeners) {
      listener.onAppListRequestCompleted(appsResult, REQUEST_REMIXED_TO, true);
    }
  }

  public void GetMostViewed(int start, int count) {

  }

  public void GetMostLiked(int start, int count) {

  }
  /**
  * GetComments gets comments for an app then tells listeners
  * @param appId app id
  * @param start staring index
  * @param count number of results
  */
  public void GetComments(long appId,int start,int count) {
    final Ode ode = Ode.getInstance();
    final OdeAsyncCallback<List<GalleryComment>> galleryCallback = new OdeAsyncCallback<List<GalleryComment>>(
    // failure message
    MESSAGES.galleryCommentError()) {
      @Override
      public void onSuccess(List<GalleryComment> comments) {
        // now relay the result back to UI client
        for (GalleryRequestListener listener:listeners) {
          listener.onCommentsRequestCompleted(comments);
        }
      }
    };
    ode.getGalleryService().getComments(appId,galleryCallback);

  }
  // the following two methods are not implemented. we just publish/update directly
  // from the view classes (and not using client facade)
  public void Publish(GalleryApp app) {
    // TODO Auto-generated method stub
  }

  public void Update(GalleryApp app) {
    // TODO Auto-generated method stub
  }
 /**
  * loadSourceFile opens the app as a new app inventor project
  * @param gApp the app to open
  * @return True if success, otherwise false
  */
  public boolean loadSourceFile(GalleryApp gApp, String newProjectName) {
    final String projectName = newProjectName;
    final String sourceURL = getGallerySettings().getSourceURL(gApp.getGalleryAppId());
    final long galleryId = gApp.getGalleryAppId();

    // first check name to see if valid and unique...
    if (!TextValidators.checkNewProjectName(projectName))
      return false;  // the above function takes care of error messages
    // Callback for updating the project explorer after the project is created on the back-end
    final Ode ode = Ode.getInstance();

    final OdeAsyncCallback<Void> galleryCallback = new OdeAsyncCallback<Void>(
    // failure message
    MESSAGES.createProjectError()) {
      @Override
      public void onSuccess(Void arg2) {
      }
    };

    final OdeAsyncCallback<UserProject> callback = new OdeAsyncCallback<UserProject>(
    // failure message
    MESSAGES.createProjectError()) {
      @Override
      public void onSuccess(UserProject projectInfo) {
        Project project = ode.getProjectManager().addProject(projectInfo);
        Ode.getInstance().openYoungAndroidProjectInDesigner(project);
      }
    };
    // this is really what's happening here, we call server to load project
    ode.getProjectService().newProjectFromGallery(projectName, sourceURL, galleryId, callback);
    return true;
  }

 /**
  * appWasChanged called to tell galleryList (and possibly others) that app is modified
  */
  public void appWasChanged() {
    // for now, let's update the recent list and the popular list (in case one was deleted)
    GetMostRecent(0,GalleryList.NUMAPPSTOSHOW, true);
    GetMostDownloaded(0,GalleryList.NUMAPPSTOSHOW, true);
  }

 /**
  * appWasDownloaded called to tell backend that app is downloaded
  */
  public void appWasDownloaded(final long galleryId) {
    // Inform the GalleryService (which eventually goes to ObjectifyGalleryStorageIo)
    final Ode ode = Ode.getInstance();
    final OdeAsyncCallback<Void> callback = new OdeAsyncCallback<Void>(
      MESSAGES.galleryDownloadedAppsError()) {
      @Override
      public void onSuccess(Void result) {
        // If app was successfully downloaded, get another async call going
        // This call we increment the download count of this app
        final OdeAsyncCallback<GalleryApp> appCallback = new OdeAsyncCallback<GalleryApp>(
        MESSAGES.galleryError()) {
          @Override
          public void onSuccess(GalleryApp app) {
            app.incrementDownloads();
          }
        };
        Ode.getInstance().getGalleryService().getApp(galleryId, appCallback);

      }
    };
    // ok, this is below the call back, but of course it is done first
    ode.getGalleryService().appWasDownloaded(galleryId, callback);


  }

  public static final String DEFAULTGALLERYIMAGE="images/genericApp.png";
  public static final String DEFAULTUSERIMAGE="images/android_icon_.png";

  /**
   * URL is in GCS, of form: /gs/<bucket>/gallery/apps/6046115656892416/aia
   * @return gallery bucket
   */
  public String getBucket() {
    return getGallerySettings().getBucket();
  }

  /**
   * get cloud image url
   * @param galleryId gallery id
   * @return url of cloud image
   */
  public String getCloudImageURL(long galleryId) {
    if(getSystemEnvironmet() != null &&
        getSystemEnvironmet().toString().equals("Production")){
      return getGallerySettings().getCloudImageURL(galleryId);
    }else {
      return getGallerySettings().getCloudImageLocation(galleryId);
    }
  }

  /**
   * get project image url
   * @param projectId project id
   * @return url of project image
   */
  public String getProjectImageURL(long projectId) {
    if(getSystemEnvironmet() != null &&
        getSystemEnvironmet().toString().equals("Production")){
      return getGallerySettings().getProjectImageURL(projectId);
    }else {
      return getGallerySettings().getProjectImageLocation(projectId);
    }
  }

  /**
   * get user image url
   * @param userId user id
   * @return url of user image
   */
  public String getUserImageURL(String userId) {
    if(getSystemEnvironmet() != null &&
        getSystemEnvironmet().toString().equals("Production")){
      return getGallerySettings().getUserImageURL(userId);
    }else {
      return getGallerySettings().getUserImageLocation(userId);
    }
  }

  public void setSystemEnvironmet(String value) {
    ENVIRONMENT = value;
  }
  public String getSystemEnvironmet() {
    return this.ENVIRONMENT;
  }

}
