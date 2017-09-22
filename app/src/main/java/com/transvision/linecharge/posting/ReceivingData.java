package com.transvision.linecharge.posting;

import android.os.Handler;

import com.transvision.linecharge.values.GetSetValues;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import static com.transvision.linecharge.values.ConstantValues.DETAILS_UPDATE_FAILURE;
import static com.transvision.linecharge.values.ConstantValues.DETAILS_UPDATE_SUCCESS;
import static com.transvision.linecharge.values.ConstantValues.LOGIN_FAILURE;
import static com.transvision.linecharge.values.ConstantValues.LOGIN_SUCCESS;

public class ReceivingData {

    public String parseServerXML(String result) {
        String value="";
        XmlPullParserFactory pullParserFactory;
        InputStream res;
        try {
            res = new ByteArrayInputStream(result.getBytes());
            pullParserFactory = XmlPullParserFactory.newInstance();
            pullParserFactory.setNamespaceAware(true);
            XmlPullParser parser = pullParserFactory.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(res, null);
            int eventType = parser.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                String name = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:
                        switch (name) {
                            case "string":
                                value =  parser.nextText();
                                break;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                }
                eventType = parser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    public void login_status(String result, Handler handler, GetSetValues getset) {
        result = parseServerXML(result);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            String msg = jsonObject.getString("message");
            if (StringUtils.startsWithIgnoreCase(msg, "Success!")) {
                getset.setLinemen_name(jsonObject.getString("Name"));
                getset.setSubdiv_code(jsonObject.getString("Subdivcode"));
                handler.sendEmptyMessage(LOGIN_SUCCESS);
            } else handler.sendEmptyMessage(LOGIN_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(LOGIN_FAILURE);
        }
    }

    public void issue_details_status(String result, Handler handler, GetSetValues getset) {
        result = parseServerXML(result);
        JSONObject jsonObject;
        try {
            jsonObject = new JSONObject(result);
            String msg = jsonObject.getString("message");
            if (StringUtils.startsWithIgnoreCase(msg, "Success")) {
                handler.sendEmptyMessage(DETAILS_UPDATE_SUCCESS);
            } else handler.sendEmptyMessage(DETAILS_UPDATE_FAILURE);
        } catch (JSONException e) {
            e.printStackTrace();
            handler.sendEmptyMessage(DETAILS_UPDATE_FAILURE);
        }
    }
}
