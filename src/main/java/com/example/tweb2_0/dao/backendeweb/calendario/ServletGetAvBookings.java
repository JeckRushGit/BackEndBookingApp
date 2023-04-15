package com.example.tweb2_0.dao.backendeweb.calendario;

import com.example.tweb2_0.dao.Dao;
import com.example.tweb2_0.dao.modules.*;
import com.example.tweb2_0.dao.serverdata.ServerData;
import com.google.gson.*;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;


import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "ServletGetAvBookings", value = "/ServletGetAvBookings")
public class ServletGetAvBookings extends HttpServlet {

    class MyTmpClass {
        final Integer month;
        final List<Integer> days;

        MyTmpClass(Integer month, List<Integer> days) {
            this.month = month;
            this.days = days;
        }
    }

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
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        String titoloCorso = request.getParameter("titoloCorso");
        String emailProfessore = request.getParameter("emailProfessore");

        PrintWriter out = response.getWriter();
        RequestDispatcher rd = context.getNamedDispatcher("ServletSessionHandler");
        rd.include(request, response);
        HttpSession sessionAvailable = (HttpSession) request.getAttribute("result");
        String action = request.getParameter("action");

        if (action != null && action.equals("web-getdaysandmonth")) {
            Integer month = ServerData.month;
            List<Integer> days = new ArrayList<>();
            Integer startingDay = ServerData.startingDayOfWeek;
            for (int i = 0; i < 5; i++) {
                days.add(startingDay);
                startingDay++;
            }
            MyTmpClass tmp = new MyTmpClass(month, days);
            String json = new Gson().toJson(tmp);
            out.println(json);
            out.flush();
        } else if (sessionAvailable != null && action.equals("web-getbookings-auth")) {
            try {
                String emailUtente = (String) sessionAvailable.getAttribute("email");
                handleGetBookings(titoloCorso, emailProfessore, out, emailUtente);
            } catch (Exception e) {
                System.out.println(e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } else if(sessionAvailable == null  && action!= null && action.equals("web-getbookings-auth")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else if(action != null && action.equals("guest")){
            try{
                List<AvBookings> list = null;
                list = dao.getOnlyAvailableBookingsForCourseAndProfessor(ServerData.startingDayOfWeek, ServerData.month, new Course(titoloCorso), new Professor(emailProfessore));
                System.out.println(list);
                if (list != null) {
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String res = gson.toJson(list);
                    JsonElement je = JsonParser.parseString(res);
                    res = gson.toJson(je);
                    out.println(res);
                    out.flush();
                }
            }catch (Exception e){
                System.out.println(e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }else if(action != null && action.equals("mobile")){
            String emailUtente = request.getParameter("emailUtente");
            if(emailUtente != null && emailProfessore != null && titoloCorso != null){
                try {
                    handleGetBookings(titoloCorso,emailProfessore,out,emailUtente);
                } catch (DAOException e) {
                    System.out.println(e.getMessage());
                }
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }
    }

    private void handleGetBookings(String titoloCorso, String emailProfessore, PrintWriter out, String emailUtente) throws DAOException {
        List<AvBookings> list = null;
        list = dao.getOnlyAvailableBookingsForCourseAndProfessorPlusUser(ServerData.startingDayOfWeek, ServerData.month, new Course(titoloCorso), new Professor(emailProfessore), new User(emailUtente));

        if (list != null) {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String res = gson.toJson(list);
            JsonElement je = JsonParser.parseString(res);
            res = gson.toJson(je);
            out.println(res);
            out.flush();
        }
    }


}
