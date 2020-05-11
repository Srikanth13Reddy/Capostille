package com.capostille.android.interfaces;

import org.json.JSONObject;

public interface SavePresenter {
    void handleSave(JSONObject jsonObject, String connectionID, String type, String token, String authToken);
}
