package com.example.tweb2_0.dao.backendeweb.agenda;

import com.example.tweb2_0.dao.Dao;
import com.example.tweb2_0.dao.modules.AvBookings2;
import com.example.tweb2_0.dao.modules.DAOException;
import com.example.tweb2_0.dao.modules.User;
import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;


@WebServlet(name = "ServletGetBookingsForUser", value = "/ServletGetBookingsForUser")
public class ServletGetBookingsForUser extends HttpServlet {


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
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String email = request.getParameter("email");
        String statoString = request.getParameter("stato");
        String action = request.getParameter("action");
        PrintWriter out = response.getWriter();

        if (action != null && action.equals("web")) {  //sito web
            response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            RequestDispatcher rd = context.getNamedDispatcher("ServletSessionHandler");
            rd.include(request, response);
            HttpSession sessionAvailable = (HttpSession) request.getAttribute("result");
            if (sessionAvailable != null) {
                String userEmail = (String) sessionAvailable.getAttribute("email");
                if (statoString != null) {
                    try {
                        handleRequest(new User(userEmail), Integer.parseInt(statoString), out);
                    } catch (DAOException e) {
                        System.out.println(e.getMessage());
                    }
                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }


        } else {   //mobile
            try {

                if (statoString != null) {
                    handleRequest(new User(email), Integer.parseInt(statoString), out);
                }
            } catch (DAOException e) {
                System.out.println(e.getMessage());
            }

        }


    }



    void handleRequest(User user, int stato, PrintWriter out) throws DAOException {
        List<AvBookings2> list = dao.getBookingsForUser(user, stato);
        Gson g = new Gson();
        String json = g.toJson(list);
        out.println(json);
        out.flush();
    }
}

