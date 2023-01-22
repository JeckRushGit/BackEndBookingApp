package com.example.tweb2_0.dao.backendeweb;


import com.google.gson.Gson;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    public void init() {
        message = "Hello World!";
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("richiesta");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        Gson gson = new Gson();
        Integer x = 5;
        String p = "{\"balance\": 1000.21, \"num\":100, \"is_vip\":true, \"name\":\"foo\"}  ";
        PrintWriter out = response.getWriter();
        out.write(p);
        out.flush();
        out.close();
    }


    public void destroy() {
    }
}