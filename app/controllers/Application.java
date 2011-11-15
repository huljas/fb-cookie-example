package controllers;

import play.*;
import play.libs.WS;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
        render();
    }

    public static void authenticate() {
        String cookieName = "fbsr_" + Play.configuration.get("fb.appId");
        Http.Cookie cookie = request.cookies.get(cookieName);
        String jsonData = null;
        String result = null;
        if (cookie != null) {
            FacebookCookie fbCookie = new FacebookCookie(cookie.value);
            if (fbCookie.validate(Play.configuration.getProperty("fb.secret"))) {
                jsonData = fbCookie.jsonData();
                FacebookCookie.Data data = fbCookie.data();
                WS.HttpResponse response = WS.url("https://graph.facebook.com/oauth/access_token?"
                        + "client_id=" + Play.configuration.get("fb.appId")
                        + "&redirect_uri="
                        + "&client_secret=" + Play.configuration.get("fb.secret")
                        + "&code=" + data.code).get();
                result = response.getString();
            }
        }
        render(jsonData, result);
    }

}