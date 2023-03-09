package com.example.tweb2_0.dao.backendeweb.calendario;

import com.example.tweb2_0.dao.Dao;
import com.example.tweb2_0.dao.modules.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "ServletGetAvBookings", value = "/ServletGetAvBookings")
public class ServletGetAvBookings extends HttpServlet {

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
        PrintWriter out = response.getWriter();
        String titoloCorso = request.getParameter("titoloCorso");
        String emailProfessore = request.getParameter("emailProfessore");
        String emailUtente = request.getParameter("emailUtente");

        if(titoloCorso != null && emailProfessore != null){
            try {
                List<AvBookings> list;
                if(emailUtente != null){  //client
                    list = dao.getOnlyAvailableBookingsForCourseAndProfessorPlusUser(16,1,new Course(titoloCorso),new Professor(emailProfessore),new User(emailUtente));
                }
                else{       //guest
                    list = dao.getOnlyAvailableBookingsForCourseAndProfessor(16,1,new Course(titoloCorso),new Professor(emailProfessore));
                }
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String res = gson.toJson(list);
                JsonElement je = JsonParser.parseString(res);
                res = gson.toJson(je);
                out.println(res);
                out.flush();
            } catch (DAOException e) {
                System.out.println(e.getMessage());
            }finally {
                out.close();
            }
        }
    }
}
