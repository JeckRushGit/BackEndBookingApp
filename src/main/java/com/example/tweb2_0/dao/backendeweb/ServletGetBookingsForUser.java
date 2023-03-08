package com.example.tweb2_0.dao.backendeweb;
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
        Integer stato = null;
        try{
            if(statoString != null){
                stato = Integer.valueOf(statoString);
                List<AvBookings2> list = dao.getBookingsForUser(new User(email),stato);
                Gson g = new Gson();
                String json = g.toJson(list);
                PrintWriter out = response.getWriter();
                out.println(json);
                out.flush();
            }
            else{
                List<AvBookings2> list = dao.getBookingsForUser(new User(email),null);
            }
        }catch (DAOException e){
            System.out.println(e.getMessage());
        }
    }


    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String email = request.getParameter("email");
        String statoString = request.getParameter("stato");
        Integer stato = null;
        try{
            if(statoString != null){
                stato = Integer.valueOf(statoString);
                List<AvBookings2> list = dao.getBookingsForUser(new User(email),stato);
                Gson g = new Gson();
                String json = g.toJson(list);
                PrintWriter out = response.getWriter();
                out.println(json);
                out.flush();
            }
            else{
                List<AvBookings2> list = dao.getBookingsForUser(new User(email),null);
            }
        }catch (DAOException e){
            System.out.println(e.getMessage());
        }
    }
}

