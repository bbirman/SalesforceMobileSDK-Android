package com.salesforce.androidsdk.rest;

import android.net.Uri;

import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.salesforce.androidsdk.rest.RestRequest.RestMethod;
import com.salesforce.androidsdk.rest.RestRequest.RestAction;
import com.salesforce.androidsdk.rest.files.ConnectUriBuilder;

public class NotificationRequest { //extends APIRequests {//RestRequest {
    // Stole this from MobileSync, what's a central place for it?
    public static final DateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ", Locale.US);


//    public NotificationRequest(RestRequest.RestMethod method, String path) {
//        super(method, path);
//    }

    public static RestRequest getRequestForNotificationsStatus(String apiVersion) {
        return new RestRequest(RestMethod.GET, RestAction.NOTIFICATIONS.getPath(apiVersion, "status"));
    }

    public static RestRequest getRequestForNotifications(String notificationId, String apiVersion) {
        return new RestRequest(RestMethod.GET, RestAction.NOTIFICATIONS.getPath(apiVersion, notificationId));
    }

    public static class UpdateNotificationsRequestBuilder {
        private Map<String, Object> fields = new HashMap<>();
        private String notificationId;

        public UpdateNotificationsRequestBuilder setNotificationId(String notificationId) {
            this.notificationId = notificationId;
            return this;
        }

        public UpdateNotificationsRequestBuilder setNotificationIds(List<String> notificationIds) { // Is List<String> right?
            fields.put("notificationIds", notificationIds);
            return this;
        }

        public UpdateNotificationsRequestBuilder setBefore(Date date) {
            fields.put("before", TIMESTAMP_FORMAT.format(date));
            return this;
        }

        public UpdateNotificationsRequestBuilder setSeen(Boolean seen) {
            fields.put("seen", seen); // Does this need to be a string? Is it taken care of automatically?
            return this;
        }

        public UpdateNotificationsRequestBuilder setRead(Boolean read) {
            fields.put("read", read);
            return this;
        }

        public RestRequest build(String apiVersion) {
            String path;
            if (notificationId != null) {
                path = RestAction.NOTIFICATIONS.getPath(apiVersion, notificationId);
            } else {
                path = RestAction.NOTIFICATIONS.getPath(apiVersion, ""); // Change to having a no argument version?
            }
            return new RestRequest(RestMethod.PATCH, path, new JSONObject(fields));
        }
    }

    public static class FetchNotificationsRequestBuilder {
        private Map<String, String> parameters = new HashMap<>();

        public FetchNotificationsRequestBuilder setSize(Integer size) {
            parameters.put("size", size.toString());
            return this;
        }

        public FetchNotificationsRequestBuilder setBefore(Date date) {
            String beforeDate = TIMESTAMP_FORMAT.format(date);
            parameters.put("before", beforeDate);
            return this;
        }

        public FetchNotificationsRequestBuilder setAfter(Date date) {
            String afterDate = TIMESTAMP_FORMAT.format(date);
            parameters.put("after", afterDate);
            return this;
        }

        public RestRequest build(String apiVersion) {
            ConnectUriBuilder builder = new ConnectUriBuilder(Uri.parse(RestAction.NOTIFICATIONS.getPath(apiVersion, "")).buildUpon());
            for (Map.Entry<String, String> parameter : parameters.entrySet()) {
                builder.appendQueryParam(parameter.getKey(), parameter.getValue());
            }
            return new RestRequest(RestMethod.GET, builder.toString());
        }
    }
}
