package com.example.tweb2_0.dao.backendeweb.agenda;

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
        String action = request.getParameter("action");
        System.out.println("email professore : "+professorEmail);
        System.out.println("titolo corso : "+course);
        System.out.println("giorno : "+day);
        System.out.println("mese : "+month);
        System.out.println("orario : "+hour);
        System.out.println("stato : "+state);


        if (action != null && action.equals("web")) {  //sito web
            response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            RequestDispatcher rd = context.getNamedDispatcher("ServletSessionHandler");
            rd.include(request, response);
            HttpSession sessionAvailable = (HttpSession) request.getAttribute("result");
            if (sessionAvailable != null) {
                userEmail = (String) sessionAvailable.getAttribute("email");
                if (userEmail != null && state != null && professorEmail != null && course != null && day != null && month != null && hour != null) {
                    try {
                        handleSetState(state,professorEmail,course,userEmail,day,month,hour,response);
                    } catch (DAOException e) {
                        System.out.println(e.getMessage());
                    }

                } else {
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }


        } else {
            try {
                if (professorEmail != null && userEmail != null && day != null && month != null && hour != null && state != null && course != null) {
                    handleSetState(state,professorEmail,course,userEmail,day,month,hour,response);
                } else { //se uno o più parametri sono null
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } catch (DAOException e) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);  //se è stata lanciata un eccezione
                throw new RuntimeException(e);
            }

        }
    }


    void handleSetState(String state,String professorEmail,String course,String userEmail,String day,String month,String hour,HttpServletResponse response) throws DAOException {
        if (state.equals("2")) {        //setta la lezione come effettuata
            int res = dao.setBookingAsDone(new Professor(professorEmail), new Course(course), new User(userEmail), Integer.valueOf(day), Integer.valueOf(month), hour);
            response.setStatus(200);
        }
        if (state.equals("3")) {       // annulla la lezione
            int res = dao.removeBookings(new Professor(professorEmail), new Course(course), new User(userEmail), Integer.valueOf(day), Integer.valueOf(month), hour);
            response.setStatus(200);
        }
    }
}
