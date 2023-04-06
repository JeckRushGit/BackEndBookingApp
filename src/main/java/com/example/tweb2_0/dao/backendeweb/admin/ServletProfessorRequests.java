package com.example.tweb2_0.dao.backendeweb.admin;

import com.example.tweb2_0.dao.Dao;
import com.example.tweb2_0.dao.modules.Course;
import com.example.tweb2_0.dao.modules.DAOException;
import com.example.tweb2_0.dao.modules.Professor;
import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "ServletProfessorRequests", value = "/ServletProfessorRequests")
public class ServletProfessorRequests extends HttpServlet {


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
        RequestDispatcher rd = context.getNamedDispatcher("ServletSessionHandler");
        rd.include(request, response);
        HttpSession sessionAvailable = (HttpSession) request.getAttribute("result");
        if(sessionAvailable != null && sessionAvailable.getAttribute("role").equals("Administrator")){
            String action = request.getParameter("action");
            if(action != null && action.equals("getProfessors")){
                getProfessors(response);
            }else if(action != null && action.equals("getCourses")){
                String professorEmail = request.getParameter("professorEmail");
                if(professorEmail != null){
                    try {
                        getCourses(response, professorEmail);
                    } catch (DAOException e) {
                        System.out.println(e.getMessage());
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    }
                }
                else{
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                }
            } else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void getCourses(HttpServletResponse response, String professorEmail) throws DAOException, IOException {
        List<Course> res = dao.getAvailableCourseForProfessor(new Professor(professorEmail));
        Gson g = new Gson();
        String json = g.toJson(res);
        response.getWriter().println(json);
    }

    private void getProfessors(HttpServletResponse response) throws IOException {
        try {
            List<Professor> res = dao.getProfessors();
            Gson g = new Gson();
            String json = g.toJson(res);
            response.getWriter().println(json);
        } catch (DAOException e) {
            System.out.println(e.getMessage());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        response.addHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Credentials", "true");
        RequestDispatcher rd = context.getNamedDispatcher("ServletSessionHandler");
        rd.include(request, response);
        HttpSession sessionAvailable = (HttpSession) request.getAttribute("result");
        if(sessionAvailable != null && sessionAvailable.getAttribute("role").equals("Administrator")){
            String professorEmail = request.getParameter("professorEmail");
            String courseTitol = request.getParameter("courseTitol");
            if(professorEmail != null && courseTitol != null){
                try {
                    if(dao.insertTeaching(new Professor(professorEmail),new Course(courseTitol))){
                        response.setStatus(HttpServletResponse.SC_OK);
                    }
                    else{
                        response.setStatus(HttpServletResponse.SC_CONFLICT);
                    }
                } catch (DAOException e) {
                    System.out.println(e.getMessage());
                    response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                }
            }else{
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            }
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
