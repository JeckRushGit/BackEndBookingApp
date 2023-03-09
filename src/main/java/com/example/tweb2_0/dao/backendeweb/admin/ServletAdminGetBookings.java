package com.example.tweb2_0.dao.backendeweb.admin;

import com.example.tweb2_0.dao.Dao;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "ServletAdminGetBookings", value = "/ServletAdminGetBookings")
public class ServletAdminGetBookings extends HttpServlet {


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

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String permission = request.getParameter("permission");
        String userEmail = request.getParameter("userEmail");
    }
}
