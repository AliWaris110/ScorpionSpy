package io.socket.hasbinary;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HasBinary {
    private static final Logger logger = Logger.getLogger(HasBinary.class.getName());

    private HasBinary() {
    }

    public static boolean hasBinary(Object obj) {
        return _hasBinary(obj);
    }

    private static boolean _hasBinary(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj instanceof byte[]) {
            return true;
        }
        if (obj instanceof JSONArray) {
            JSONArray jSONArray = (JSONArray) obj;
            int length = jSONArray.length();
            for (int i = 0; i < length; i++) {
                try {
                    if (_hasBinary(jSONArray.isNull(i) ? null : jSONArray.get(i))) {
                        return true;
                    }
                } catch (JSONException e) {
                    logger.log(Level.WARNING, "An error occured while retrieving data from JSONArray", (Throwable) e);
                    return false;
                }
            }
        } else if (obj instanceof JSONObject) {
            JSONObject jSONObject = (JSONObject) obj;
            Iterator<String> keys = jSONObject.keys();
            while (keys.hasNext()) {
                try {
                    if (_hasBinary(jSONObject.get(keys.next()))) {
                        return true;
                    }
                } catch (JSONException e2) {
                    logger.log(Level.WARNING, "An error occured while retrieving data from JSONObject", (Throwable) e2);
                }
            }
        }
        return false;
    }
}
