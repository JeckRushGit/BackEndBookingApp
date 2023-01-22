package com.example.tweb2_0.dao.backendeweb;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.tweb2_0.dao.Dao;
import com.example.tweb2_0.dao.modules.*;
import com.google.gson.Gson;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.List;


@WebServlet(name = "ServletLogin", value = "/ServletLogin")
public class ServletLogin extends HttpServlet {

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
        response.setContentType("application/json");
        RequestDispatcher requestDispatcher = context.getNamedDispatcher("ServletGetTeachings");
        requestDispatcher.include(request,response);
        List<Teaching> list = (List<Teaching>) request.getAttribute("teachingList");
        Gson g = new Gson();
        String res = g.toJson(list);
        PrintWriter out = response.getWriter();
        out.println(res);
        out.flush();
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Richiesta");
        response.setContentType("application/jwt");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        try {
            User u = dao.getUser(email);
            if (u != null) {
                if (u.getPassword().equals(password)) {
                    String jwt = JWT.create()
                            .withIssuer("myIssuer")
                            .withClaim("email",email)
                            .withClaim("name",u.getName())
                            .withClaim("surname",u.getSurname())
                            .withClaim("password",u.getPassword())
                            .withClaim("role",u.getRole())
                            .withClaim("birthday",u.getBirthday())
                            .withClaim("profession",u.getProfession())
                            .withExpiresAt(new Date(System.currentTimeMillis() + 6000000L))
                            .sign(Algorithm.HMAC256("secret"));

                    response.getWriter().write(jwt);
                }
                else{
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().println("Wrong password");
                }
            }
            else{
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().println("User not found");
            }
        } catch (DAOException | IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

