package com.nicksiepmann.albumtracker;

import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.resource.Emailv31;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

@Component
public class Emailer {

    public JSONArray SendInvite(String recipient, String sender) throws MailjetException, MailjetSocketTimeoutException {
        MailjetClient client;
        MailjetRequest request;
        MailjetResponse response;
        client = new MailjetClient("b889c863c2c4f80069b79c6a7ae4e9f4", "78d46af94de3b619eacca82bbc59b1ec", new ClientOptions("v3.1"));
        request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", "siepmann.n@gmail.com") //temporary email
                                        .put("Name", sender))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", recipient)
                                                .put("Name", recipient.split("@")[0])))
                                .put(Emailv31.Message.SUBJECT, sender + " has invited you to use Album Tracker")
                                .put(Emailv31.Message.TEXTPART, "You have been invited to collaborate on an album in Album Tracker!")
                                .put(Emailv31.Message.HTMLPART, "<h3>Check it out <a href='https://album-tracker-358914.nw.r.appspot.com/'>here</a>!</h3>")
                                .put(Emailv31.Message.CUSTOMID, "AlbumTrackerInvite")));
        response = client.post(request);
        return response.getData();
    }
}
