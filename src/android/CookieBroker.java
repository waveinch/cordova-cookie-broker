package ch.wavein.plugin.cookie;

import org.apache.cordova.*;
import org.json.JSONArray;
import org.json.JSONException;

public class CookieBroker extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray data, CallbackContext callbackContext) throws JSONException {

        if (action.equals("getCookies")) {

            String url = data.getString(0);
            callbackContext.success("CALLED!");
          
            return true;

        } else {
            
            return false;

        }
    }
}
