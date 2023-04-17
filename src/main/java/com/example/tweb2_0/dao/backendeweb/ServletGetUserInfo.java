package com.example.tweb2_0.dao.backendeweb;

import com.example.tweb2_0.dao.Dao;
import com.example.tweb2_0.dao.modules.User;
import com.google.gson.Gson;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;

@WebServlet(name = "ServletGetUserInfo", value = "/ServletGetUserInfo")
public class ServletGetUserInfo extends HttpServlet {


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
        rd.include(request,response);
        HttpSession sessionAvailable = (HttpSession) request.getAttribute("result");
        if(sessionAvailable != null){
            try {
                User user = new User((String) sessionAvailable.getAttribute("email"),(String) sessionAvailable.getAttribute("name"),(String) sessionAvailable.getAttribute("surname"),null,(String) sessionAvailable.getAttribute("birthday"),(String) sessionAvailable.getAttribute("profession"),(String) sessionAvailable.getAttribute("role"));
                Gson g = new Gson();
                String data = g.toJson(user);



                response.setStatus(HttpServletResponse.SC_OK);
                response.getWriter().println(data);
            }catch (Exception e){
                System.out.println(e.getMessage());
            }
        }else{
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
