package com.example.b07demosummer2024;

import java.util.concurrent.CompletableFuture;

public class LoginActivityPresenter {
    LoginActivityModel model;
    LoginActivityView view;
    public LoginActivityPresenter(LoginActivityView view, LoginActivityModel model){
        this.model = model;
        this.view = view;
    }

    public CompletableFuture<Boolean> checkDB(String username, String pass){
        return model.queryDB(this, username, pass);
    }
}
