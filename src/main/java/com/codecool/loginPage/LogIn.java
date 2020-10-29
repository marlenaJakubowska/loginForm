package com.codecool.loginPage;

import com.codecool.loginPage.helpers.CookieHelper;
import com.codecool.loginPage.helpers.LoginHelper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;
import java.io.IOException;
import java.net.HttpCookie;
import java.util.Map;
import java.util.Optional;

public class LogIn implements HttpHandler {

    private final LocalDB localDB;
    private final CookieHelper cookieHelper;
    private final LoginHelper loginHelper;
    private static int counter = 0;


    public LogIn(LocalDB localDB) {
        this.localDB = localDB;
        this.cookieHelper = new CookieHelper();
        this.loginHelper = new LoginHelper();
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {

        String method = exchange.getRequestMethod();
        if (method.equals("POST")) {
            handlePost(exchange);
        } else if (method.equals("GET")) {
            handleGet(exchange);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {

        Map<String, String> inputs = loginHelper.getInputs(exchange);
        if (!loginHelper.isPasswordCorrect(inputs, localDB)) {
            loginHelper.invalidCredentialsAlert(exchange);
            return;
        }
        String sessionId = String.valueOf(counter);
        cookieHelper.createCookie(exchange, "sessionId", sessionId);
        User user = localDB.getUserByName(inputs.get("username"));
        localDB.getSessionUserMap().put(counter, user);
        modelWithUserName(exchange, user);
        counter++;
    }

    private void handleGet(HttpExchange httpExchange) throws IOException {
        Optional<HttpCookie> optionalCookie = cookieHelper.getSessionIdCookie(httpExchange, "sessionId");
        if (optionalCookie.isPresent()) {
            int sessionId = cookieHelper.getSessionIdFromCookie(optionalCookie.get());
            welcomeUser(httpExchange, sessionId);
        } else {
            getForm(httpExchange);
        }
    }

    private void welcomeUser(HttpExchange exchange, int sessionId) throws IOException {
        User user = localDB.getUserBySessionId(sessionId);
        modelWithUserName(exchange, user);
    }

    private void modelWithUserName(HttpExchange exchange, User user) throws IOException {
        String response;
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/logged.twig");
        JtwigModel model = JtwigModel.newModel();
        model.with("username", user.getUserName());
        response = template.render(model);
        loginHelper.send200(exchange, response);
    }

    private void getForm(HttpExchange exchange) throws IOException {
        String response;
        JtwigTemplate template = JtwigTemplate.classpathTemplate("templates/login-page.twig");
        JtwigModel model = JtwigModel.newModel();
        response = template.render(model);
        loginHelper.send200(exchange, response);
    }
}

