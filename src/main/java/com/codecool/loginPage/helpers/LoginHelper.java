package com.codecool.loginPage.helpers;

import com.codecool.loginPage.LocalDB;
import com.codecool.loginPage.User;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LoginHelper {

    public void send200(HttpExchange exchange, String response) throws IOException {
        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String getFormData(HttpExchange httpExchange) throws IOException {
        InputStreamReader isr = new InputStreamReader(httpExchange.getRequestBody(), StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        return br.readLine();
    }

    public Map<String, String> getInputs(HttpExchange httpExchange) throws IOException {
        String formData = getFormData(httpExchange);
        return parseFormData(formData);
    }

    private static Map<String, String> parseFormData(String formData) throws UnsupportedEncodingException {
        Map<String, String> map = new HashMap<>();
        String[] pairs = formData.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            String key = URLDecoder.decode(keyValue[0], "UTF-8");
            String value = URLDecoder.decode(keyValue[1], "UTF-8");
            map.put(key, value);
        }
        return map;
    }

    public boolean isPasswordCorrect(Map<String, String> inputs, LocalDB localDB) {
        String providedName = inputs.get("username");
        String providedPassword = inputs.get("password");
        User user = localDB.getUserByName(providedName);
        return (user != null) && user.getPassword().equals(providedPassword);
    }

    public void invalidCredentialsAlert(HttpExchange exchange) throws IOException {
        String response = "Incorrect username or password";
        send200(exchange, response);
    }

    public void redirectToLogin(HttpExchange exchange) throws IOException {
        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.set("Location", "/login");
        exchange.sendResponseHeaders(302, -1);
        exchange.close();
    }
}
