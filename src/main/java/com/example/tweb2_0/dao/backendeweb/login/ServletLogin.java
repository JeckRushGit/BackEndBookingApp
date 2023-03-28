package com.example.tweb2_0.dao.backendeweb.login;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.tweb2_0.dao.Dao;
import com.example.tweb2_0.dao.modules.*;
import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Date;


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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        response.setContentType("application/jwt");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String action = request.getParameter("action");
        User user;
        HttpSession session;
        if (action != null && action.equals("logout")) {
            response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            session = request.getSession(false);
            if (session != null)
                session.invalidate();
        } else {
            if (email != null && password != null) {
                response.setContentType("application/json");
                if (action != null && action.equals("web")) { //web
                    response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
                    response.setHeader("Access-Control-Allow-Credentials", "true");
                    try {
                        user = dao.getUser(email);
                        if (user != null && checkPassword(user, password)) {
                            session = request.getSession(true);

                            session.setAttribute("email", user.getEmail());
                            session.setAttribute("name", user.getName());
                            session.setAttribute("surname", user.getSurname());
                            session.setAttribute("role", user.getRole());
                            session.setAttribute("birthday", user.getBirthday());
                            session.setAttribute("profession",user.getProfession());
                            response.setStatus(HttpServletResponse.SC_OK);
                            Gson gson = new Gson();
                            String token = gson.toJson(session);
                            response.getWriter().println(token);
                        } else {
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        }
                    } catch (DAOException e) {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        System.out.println(e.getMessage());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                } else {                    //mobile
                    try {
                        user = dao.getUser(email);
                        if (user != null) {
                            if (user.getPassword().equals(password)) {
                                String jwt = JWT.create()
                                        .withIssuer("myIssuer")
                                        .withClaim("email", email)
                                        .withClaim("name", user.getName())
                                        .withClaim("surname", user.getSurname())
                                        .withClaim("password", user.getPassword())
                                        .withClaim("role", user.getRole())
                                        .withClaim("birthday", user.getBirthday())
                                        .withClaim("profession", user.getProfession())
                                        .withExpiresAt(new Date(System.currentTimeMillis() + 6000000L))
                                        .sign(Algorithm.HMAC256("secret"));

                                response.getWriter().write(jwt);
                            } else {
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                                response.getWriter().println("Wrong password");
                            }
                        } else {
                            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            response.getWriter().println("User not found");
                        }
                    } catch (DAOException | IOException e) {
                        System.out.println(e.getMessage());
                    }
                }
            } else {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }


    private boolean checkPassword(User user, String passwordToCheck) {
        return user.getPassword().equals(passwordToCheck);
    }


}

