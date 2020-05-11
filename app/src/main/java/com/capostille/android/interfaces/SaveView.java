package com.capostille.android.interfaces;

public interface SaveView {

    void onSaveSucess(String code, String response, String type);
    void onSaveFailure(String error);
}
