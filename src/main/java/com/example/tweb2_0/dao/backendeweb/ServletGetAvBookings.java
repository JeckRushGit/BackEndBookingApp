package com.example.tweb2_0.dao.backendeweb;

import com.example.tweb2_0.dao.Dao;
import com.example.tweb2_0.dao.modules.*;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String titoloCorso = request.getParameter("titoloCorso");
        String emailProfessore = request.getParameter("emailProfessore");
        if(titoloCorso != null && emailProfessore != null){
            try {
                List<AvBookings> list = dao.getOnlyAvailableBookingsForCourseAndProfessor(16,1,new Course(titoloCorso),new Professor(emailProfessore));
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String res = gson.toJson(list);
                out.println(res);
                out.flush();
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }finally {
                out.close();
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String titoloCorso = request.getParameter("titoloCorso");
        String emailProfessore = request.getParameter("emailProfessore");
        if(titoloCorso != null && emailProfessore != null){
            try {
                List<AvBookings> list = dao.getOnlyAvailableBookingsForCourseAndProfessor(16,1,new Course(titoloCorso),new Professor(emailProfessore));
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String res = gson.toJson(list);
                JsonElement je = JsonParser.parseString(res);
                res = gson.toJson(je);
                out.println(res);
                out.flush();
            } catch (DAOException e) {
                throw new RuntimeException(e);
            }finally {
                out.close();
            }
        }
    }
}
