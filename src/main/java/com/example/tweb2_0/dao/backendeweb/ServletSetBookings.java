package com.example.tweb2_0.dao.backendeweb;

import com.example.tweb2_0.dao.Dao;
import com.example.tweb2_0.dao.modules.Course;
import com.example.tweb2_0.dao.modules.DAOException;
import com.example.tweb2_0.dao.modules.Professor;
import com.example.tweb2_0.dao.modules.User;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;

@WebServlet(name = "ServletSetBookings", value = "/ServletSetBookings")
public class ServletSetBookings extends HttpServlet {

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
        String professorEmail = request.getParameter("email_professore");
        String userEmail = request.getParameter("email_utente");
        String course = request.getParameter("corso");
        String day = request.getParameter("giorno");
        String month = request.getParameter("mese");
        String hour = request.getParameter("orario");
        String state = request.getParameter("stato");

        int res = 0;
        PrintWriter out = response.getWriter();

        try {
            if (professorEmail != null && userEmail != null && day != null && month != null && hour != null && state != null && course != null) {
                System.out.println("qua dentro!!!");
                if (state.equals("2")) {        //setta la lezione come effettuata
                    res = dao.setBookingAsDone(new Professor(professorEmail), new Course(course), new User(userEmail), Integer.valueOf(day), Integer.valueOf(month), hour);
                    response.setStatus(200);
                }
                if (state.equals("3")) {       // annulla la lezione
                    res = dao.removeBookings(new Professor(professorEmail), new Course(course), new User(userEmail), Integer.valueOf(day), Integer.valueOf(month), hour);
                    response.setStatus(200);
                }
            }
            else{ //se uno o più parametri sono null
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }catch (DAOException e){
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  //se è stata lanciata un eccezione
            throw new RuntimeException(e);
        }
        out.write("");
    }
}
