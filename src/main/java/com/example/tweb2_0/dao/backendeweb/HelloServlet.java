package com.example.tweb2_0.dao.backendeweb;


import com.example.tweb2_0.dao.Dao;
import com.example.tweb2_0.dao.modules.DAOException;
import com.google.gson.Gson;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    private String message;

    private Dao dao;
    ServletContext context;

    @Override
    public void init(ServletConfig config) {
        context = config.getServletContext();
        dao = (Dao) context.getAttribute("dao");
        if (dao == null) {
            String url = context.getInitParameter("url");
            String user = context.getInitParameter("user");
            String password = context.getInitParameter("password");
            dao = new Dao(url, user, password);
            context.setAttribute("dao", dao);
        }
    }

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
       /* System.out.println("richiesta");
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        Gson gson = new Gson();
        Integer x = 5;
        String p = "{\"balance\": 1000.21, \"num\":100, \"is_vip\":true, \"name\":\"foo\"}  ";
        PrintWriter out = response.getWriter();
        out.write(p);
        out.flush();
        out.close();*/
        try {
            dao.getTeachings();
        } catch (DAOException e) {
            System.out.println(e.getMessage());
        }
    }


    public void destroy() {
    }
}