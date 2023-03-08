package com.example.tweb2_0.dao.backendeweb;

import com.example.tweb2_0.dao.Dao;
import com.example.tweb2_0.dao.modules.*;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Map;

@WebServlet(name = "ServletBookTeaching", value = "/ServletBookTeaching")
public class ServletBookTeaching extends HttpServlet {

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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException{
        response.setContentType("application/json");
        StringBuffer jb = new StringBuffer();
        String line = null;
        PrintWriter out = null;
        try {
            out = response.getWriter();
            BufferedReader reader = request.getReader();
            while ((line = reader.readLine()) != null) {
                jb.append(line);
            }
            String json = jb.toString();
            Gson g = new Gson();
            Type mapType = new TypeToken<Map<String,String>>(){}.getType();
            Map<String,String> map = g.fromJson(json,mapType);
            Course course = new Course(map.get("course_titol"));
            Professor professor = new Professor(map.get("professor_email"));
            User user = new User(map.get("user_email"));
            int day = Integer.valueOf(map.get("day"));
            int month = Integer.valueOf(map.get("month"));
            String hour = map.get("hour");
            try {
                dao.insertBooking(professor,course,user,day,month,hour);
                out.write("{errore : nessun errore}");
                out.flush();
            } catch (DAOException e) {
                if(e.getErrorCode() == 1){
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println(g.toJson("lezione non disponibile"));
                    out.flush();
                }
                else{
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    out.println("errore con il server");
                    out.flush();
                }
            }
        }catch (IOException e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("{errore: richiesta non valida}");
            out.flush();
        }
        finally {
            out.close();
        }
    }
}
