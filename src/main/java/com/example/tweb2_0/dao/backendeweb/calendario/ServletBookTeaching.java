package com.example.tweb2_0.dao.backendeweb.calendario;

import com.example.tweb2_0.dao.Dao;
import com.example.tweb2_0.dao.modules.*;

import com.google.gson.Gson;

import com.google.gson.reflect.TypeToken;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.List;
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
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        String action = request.getParameter("action");

        if(action != null && action.equals("web")){
            response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            PrintWriter out = response.getWriter();
            RequestDispatcher rd = context.getNamedDispatcher("ServletSessionHandler");
            rd.include(request, response);
            HttpSession sessionAvailable = (HttpSession) request.getAttribute("result");
            if (sessionAvailable != null) {
                String userEmail = (String) sessionAvailable.getAttribute("email");
                String professorEmail = request.getParameter("professorEmail");
                String courseTitol = request.getParameter("courseTitol");
                String day = request.getParameter("day");
                String month = request.getParameter("month");
                String hour = request.getParameter("hour");
                if(userEmail != null && professorEmail != null && courseTitol != null && day != null && month != null && hour != null){
                    Integer dayAsInt = Integer.valueOf(day);
                    Integer monthAsInt = Integer.valueOf(month);
                    try {
                        boolean flag = dao.insertBooking(new Professor(professorEmail),new Course(courseTitol),new User(userEmail),dayAsInt,monthAsInt,hour);
                        if(flag){

                            response.setStatus(HttpServletResponse.SC_OK);
                        }else{
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            String resp = "operazione fallita";
                            String json = new Gson().toJson(resp);
                            out.println(json);
                        }
                    }catch (DAOException e){
                        if(e.getErrorCode() == DAOException._WAIT_FOR_A_MINUTE){
                            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                            String resp = "aspetta un minuto";
                            String json = new Gson().toJson(resp);
                            out.println(json);
                        }else if(e.getErrorCode() == DAOException._FAIL_TO_INSERT){
                            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                            String resp = "lezione non più disponibile";
                            String json = new Gson().toJson(resp);
                            out.println(json);
                        }else{
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            String resp = "qualcosa è andato storto con il server/database";
                            String json = new Gson().toJson(resp);
                            out.println(json);
                        }
                    }
                }else{
                    String resp = "dati mancanti";
                    String json = new Gson().toJson(resp);
                    out.println(json);
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }
        else{
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
                    boolean flag = dao.insertBooking(professor,course,user,day,month,hour);
                    if(flag){
                        response.setStatus(HttpServletResponse.SC_OK);
                        out.write("{errore : nessun errore}");
                        out.flush();

                    }else{
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.write("Erroreeeeeeeee");
                        out.flush();
                    }
                } catch (DAOException e) {
                    if(e.getErrorCode() == 1){
                        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        out.println(g.toJson("lezione non disponibile"));
                        out.flush();
                    }
                    else{
                        if(e.getErrorCode() == 5){
                            response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
                        }
                        else{
                            System.out.println(e.getMessage());
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        }
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
}
