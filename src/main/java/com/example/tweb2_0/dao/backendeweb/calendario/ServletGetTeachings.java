package com.example.tweb2_0.dao.backendeweb.calendario;


import com.example.tweb2_0.dao.Dao;
import com.example.tweb2_0.dao.modules.*;

import com.google.gson.Gson;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet(name = "ServletGetTeachings", value = "/ServletGetTeachings")
public class ServletGetTeachings extends HttpServlet {

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
        String action = request.getParameter("action");
        PrintWriter out = response.getWriter();
        if (action != null && action.equals("web")) {
            response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            RequestDispatcher rd = context.getNamedDispatcher("ServletSessionHandler");
            rd.include(request, response);
            HttpSession sessionAvailable = (HttpSession) request.getAttribute("result");
            try {
                List<Teaching> teachingList = dao.getTeachings();
                Gson gson = new Gson();
                String res = gson.toJson(teachingList);
                out.println(res);
                out.flush();
            } catch (DAOException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("Errore");
                out.flush();
                System.out.println(e.getMessage());
            }

        } else {
            try {
                List<Teaching> teachingList = dao.getTeachings();
                Gson gson = new Gson();
                String res = gson.toJson(teachingList);
                out.println(res);
                out.flush();
            } catch (DAOException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                out.write("Errore");
                out.flush();
                System.out.println(e.getMessage());
            } finally {
                out.close();
            }
        }
    }


    private Map<Professor, List<Course>> createMap(List<Teaching> list) {
        Map<Professor, List<Course>> map = new HashMap<>();
        for (Teaching t : list) {
            if (!map.containsKey(t.getProfessor())) {
                List<Course> listOfCourses = new ArrayList<>();
                listOfCourses.add(t.getCourse());
                map.put(t.getProfessor(), listOfCourses);
            } else {
                List<Course> listOfCourses = map.get(t.getProfessor());
                listOfCourses.add(t.getCourse());
            }
        }
        return map;
    }

}
