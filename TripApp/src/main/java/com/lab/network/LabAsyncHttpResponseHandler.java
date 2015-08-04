package com.lab.network;

import org.apache.http.Header;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.cuitrip.app.MainApplication;
import com.cuitrip.service.R;
import com.loopj.android.http.AsyncHttpResponseHandler;

public abstract class LabAsyncHttpResponseHandler extends AsyncHttpResponseHandler {

    public Class<?> clazz;

    public LabAsyncHttpResponseHandler() {
    }

    public LabAsyncHttpResponseHandler(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
        if (statusCode == 200) {
            try {
                LabResponse response = LabResponse.parseResponse(responseBody);
                if (response != null && response.code == 0) {
                    if (clazz != null && response.result != null
                            && response.result instanceof JSONObject) {
                        Object data = JSON.parseObject(response.result.toString(), clazz);
                        onSuccess(response, data);
                    } else {
                        onSuccess(response, response.result);
                    }
                } else if (response != null) {
                    onFailure(response, null);
                } else {
                    onDefaultError();
                }
            } catch (Throwable throwable) {
                throwable.printStackTrace();
                onDefaultError();
            }
        }
    }

    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        onDefaultError();
    }

    public void onDefaultError() {
        LabResponse response = new LabResponse();
        response.code = -1;
        response.result = null;
        response.msg = MainApplication.sContext.getString(R.string.load_error);
        onFailure(response, null);
    }


    public abstract void onSuccess(LabResponse response, Object data);

    public abstract void onFailure(LabResponse response, Object data);


}
