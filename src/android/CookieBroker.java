package ch.wavein.plugin.cookie;

import android.util.Log;

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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CookieBroker extends CordovaPlugin {
    static final String COOKIES_HEADER = "Set-Cookie";
    static final String CLASS_TAG = "CookieBroker";

    @Override
    public boolean execute(String action, JSONArray data, final CallbackContext callbackContext) throws JSONException {

        if (action.equals("getCookies")) {
            final String url = data.getString(0);
            final JSONObject opts = data.optJSONObject(1);

            cordova.getThreadPool().execute(new Runnable() {
                public void run() {
                    try {
                        JSONArray cookies = getCookies(url, opts);
                        callbackContext.success(cookies);
                    } catch (IOException e) {
                        e.printStackTrace();
                        callbackContext.error("Can't reach the server");
                    } catch (JSONException e) {
                        e.printStackTrace();
                        callbackContext.error("Can't parse the JSON");
                    }
                }
            });

            return true;

        } else {

            return false;

        }
    }

    private JSONArray getCookies(String url, JSONObject opts) throws IOException, JSONException {
        if (opts == null) {
            opts = new JSONObject();
        }
        String cookieString = opts.optString("cookieHeader", "");
        //Log.d(CLASS_TAG, "cookieString -> " + cookieString);

        HttpURLConnection ucon = (HttpURLConnection) new URL(url).openConnection();
        ucon.setInstanceFollowRedirects(false);
        ucon.setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; U; Android-phone)");

        if (!cookieString.equals("")) {
            ucon.setRequestProperty("Cookie", cookieString);
        }

        Map<String, List<String>> headerFields = ucon.getHeaderFields();
        List<String> cookiesHeader = headerFields.get(COOKIES_HEADER);
        List<HttpCookie> allCookies = new ArrayList<HttpCookie>();

        for (String rawCookie : cookiesHeader) {
            allCookies.addAll(HttpCookie.parse(rawCookie));
        }

        JSONArray cookiesJson = new JSONArray();

        for (HttpCookie cookie : allCookies) {
            JSONObject cookieJson = new JSONObject();
            cookieJson.put("name", cookie.getName());
            cookieJson.put("value", cookie.getValue());
            cookieJson.put("domain", cookie.getDomain());
            cookieJson.put("discard", cookie.getDiscard());
            cookieJson.put("maxAge", cookie.getMaxAge());
            cookieJson.put("path", cookie.getPath());
            cookiesJson.put(cookieJson);
        }

        return cookiesJson;
    }
}
