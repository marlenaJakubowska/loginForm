package com.codecool.loginPage;

import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;

public class App 
{
    public static void main( String[] args ) throws IOException {
        LocalDB localDB = new LocalDB();
        HttpServer server = HttpServer.create(new InetSocketAddress(8001), 0);
        server.createContext("/login", new LogIn(localDB));
        server.createContext("/logout", new LogOut(localDB));
        server.createContext("/static", new Static());
        server.setExecutor(null);
        server.start();
    }
}
