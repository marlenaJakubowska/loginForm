package com.codecool.loginPage;

import com.codecool.loginPage.helpers.CookieHelper;
import com.codecool.loginPage.helpers.LoginHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;


public class LogOut implements HttpHandler {

    private final CookieHelper cookieHelper;
    private final LocalDB localDB;
    private final LoginHelper loginHelper;


    public LogOut(LocalDB localDB) {
        this.localDB = localDB;
        this.cookieHelper = new CookieHelper();
        this.loginHelper = new LoginHelper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String requestURI = exchange.getRequestURI().toString();
        System.out.println(requestURI);
        String sessionId = exchange.getRequestHeaders().getFirst("Cookie")
                .replace("\"", "")
                .replace("sessionId=", "");
        int newSessionId = Integer.parseInt(sessionId);
        localDB.getSessionUserMap().remove(newSessionId);
        System.out.println("sessionid removed");
        removeCookie(exchange);
        loginHelper.redirectToLogin(exchange);
    }

    private void removeCookie(HttpExchange exchange) {
        String cookie = exchange.getRequestHeaders().getFirst("Cookie") + ";Max-age=0";
        exchange.getResponseHeaders().set("Set-Cookie", cookie);
    }
}
