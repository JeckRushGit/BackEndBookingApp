package com.example.tweb2_0.dao.backendeweb.admin;

import com.example.tweb2_0.dao.Dao;
import com.example.tweb2_0.dao.modules.AvBookings2;
import com.example.tweb2_0.dao.modules.DAOException;
import com.example.tweb2_0.dao.modules.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet(name = "ServletAdminGetBookings", value = "/ServletAdminGetBookings")
public class ServletAdminGetBookings extends HttpServlet {


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
        response.addHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json");
        String action = request.getParameter("action");
        PrintWriter out = response.getWriter();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        if (action != null) {
            switch (action) {
                case ("getListOfUsers"):
                    try {
                        List<User> listOfUsers = dao.getUsers();
                        String res = gson.toJson(listOfUsers);
                        JsonElement je = JsonParser.parseString(res);
                        res = gson.toJson(je);

                        out.println(res);
                    } catch (DAOException e) {
                        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                        out.println("Qualcosa è andato storto con il server");
                        System.out.println(e.getMessage());
                    }
                    break;
                case ("getBookingsForUser"):
                    String userEmail = request.getParameter("userEmail");
                    if(userEmail != null){
                        try {
                            List<AvBookings2> listOfBookings = dao.getBookingsForUser(new User(userEmail),1);
                            String res = gson.toJson(listOfBookings);
                            JsonElement je = JsonParser.parseString(res);
                            res = gson.toJson(je);

                            out.println(res);
                        } catch (DAOException e) {
                            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            out.println("Qualcosa è andato storto con il server");
                            System.out.println(e.getMessage());
                        }
                    }
                    break;
                default:
                    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    out.println("Wrong action");
            }
        }else{
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.println("Missing action");
        }
        out.flush();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
