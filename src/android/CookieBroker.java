package ch.wavein.plugin.cookie;

import org.apache.cordova.*;
import org.chromium.mojom.mojo.HttpResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CookieBroker extends CordovaPlugin {
    static final String COOKIES_HEADER = "Set-Cookie";

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("getCookies")) {

            String url = data.getString(0);

            HttpURLConnection ucon = null;
            try {
                ucon = (HttpURLConnection) new URL(url).openConnection();
                ucon.setInstanceFollowRedirects(false);
                ucon.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; U; Android-phone)");

                Map<String, List<String>> headerFields = ucon.getHeaderFields();
                List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
                List<HttpCookie> allCookies = new ArrayList<HttpCookie>();

                for (String rawCookie : cookiesHeader) {
                    allCookies.addAll(HttpCookie.parse(rawCookie));
                }

                JSONArray cookiesJson = new JSONArray();

                for (HttpCookie cookie:allCookies) {
                    JSONObject cookieJson = new JSONObject();
                    cookieJson.put("name", cookie.getName());
                    cookieJson.put("value", cookie.getValue());
                    cookieJson.put("domain", cookie.getDomain());
                    cookieJson.put("discard", cookie.getDiscard());
                    cookieJson.put("maxAge", cookie.getMaxAge());
                    cookieJson.put("path", cookie.getPath());
                    cookiesJson.put(cookieJson);
                }
                callbackContext.success(cookiesJson);
            } catch (IOException e) {
                e.printStackTrace();
                callbackContext.error("Can't reach the server");
            }
            
            return true;

        } else {
            
            return false;

        }
    }
}
