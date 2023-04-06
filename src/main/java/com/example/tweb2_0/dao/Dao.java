package com.example.tweb2_0.dao;

import com.example.tweb2_0.dao.modules.*;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Dao {

    private final String url;
    private final String user;
    private final String password;

    public Dao(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
        registerDriver();
    }

    public void insertUser(User new_user) throws DAOException {
        Connection con = null;
        try {
            con = connectToDB();
            String query = "SELECT * FROM utenti WHERE Email = '" + new_user.getEmail() + "'";
            checkForDeleteDate(query, con);
            PreparedStatement stmt = con.prepareStatement("INSERT INTO Utenti (Email,Nome,Cognome,Password,Data_di_nascita,Professione,Ruolo) VALUES (?,?,?,?,?,?,?)");
            stmt.setString(1, new_user.getEmail());
            stmt.setString(2, new_user.getName());
            stmt.setString(3, new_user.getSurname());
            stmt.setString(4, new_user.getPassword());
            stmt.setString(5, new_user.getBirthday());
            stmt.setString(6, new_user.getProfession());
            stmt.setString(7, new_user.getRole());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }

    public int removeUser(User user) throws DAOException {
        Connection con = null;
        try {
            con = connectToDB();
            removeBookings(null, null, user, 0, 0, null);
            PreparedStatement stmt = con.prepareStatement("UPDATE utenti SET Delete_date = ? WHERE Email = ? AND Delete_date = '' ");
            stmt.setString(1, CustomDate.getDate());
            stmt.setString(2, user.getEmail());
            int res = stmt.executeUpdate();
            return res;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }


    public User getUser(String email) throws DAOException{
        Connection con = null;
        PreparedStatement stmt = null;
        User u = null;
        try {
            con = connectToDB();
            stmt = con.prepareStatement("SELECT * FROM utenti u WHERE u.Email = ? AND Delete_date = '' ");
            stmt.setString(1,email);
            ResultSet res = stmt.executeQuery();
            while(res.next()){
                u = new User(email,res.getString("Nome"),res.getString("Cognome"),res.getString("Password"),res.getString("Data_di_nascita"),res.getString("Professione"),res.getString("Ruolo"));
            }
            return u;
        }catch (SQLException e){
            throw new DAOException(e);
        }finally {
            handleFinalBlock(con);
        }
    }

    public List<User> getUsers() throws DAOException {
        Connection con = null;
        PreparedStatement stmt = null;
        List<User> listOfUsers = new ArrayList<>();
        try {
            con = connectToDB();
            stmt = con.prepareStatement("SELECT DISTINCT Email,Nome,Cognome FROM utenti WHERE Delete_date = '' AND Ruolo = 'Client' ");
            ResultSet res = stmt.executeQuery();
            while (res.next()){
                User u = new User(res.getString("Email"),res.getString("Nome"),res.getString("Cognome"));
                listOfUsers.add(u);
            }
            return listOfUsers;
        }catch (SQLException e){
            throw new DAOException(e);
        }
    }

    public void insertProfessor(Professor new_professor) throws DAOException {
        Connection con = null;
        try {
            con = connectToDB();
            String query = "SELECT * FROM docenti WHERE Email = '" + new_professor.getEmail() + "'";
            checkForDeleteDate(query, con);
            PreparedStatement stmt = con.prepareStatement("INSERT INTO docenti (Email,Nome,Cognome) VALUES (?,?,?)");
            stmt.setString(1, new_professor.getEmail());
            stmt.setString(2, new_professor.getName());
            stmt.setString(3, new_professor.getSurname());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }

    public int removeProfessor(Professor professor) throws DAOException {
        Connection con = null;
        try {
            con = connectToDB();
            removeBookings(professor, null, null, 0, 0, null);
            removeTeachings(professor, null);
            PreparedStatement stmt = con.prepareStatement("UPDATE docenti SET Delete_date = ? WHERE Email = ? AND Delete_date = '' ");
            stmt.setString(1, CustomDate.getDate());
            stmt.setString(2, professor.getEmail());
            int res = stmt.executeUpdate();
            return res;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }

    public void insertCourse(Course new_course) throws DAOException {
        Connection con = null;
        try {
            con = connectToDB();
            String query = "SELECT * FROM corsi WHERE Titolo = '" + new_course.getCourse_titol() + "'";
            checkForDeleteDate(query, con);
            PreparedStatement stmt = con.prepareStatement("INSERT INTO corsi (Titolo) VALUES (?)");
            stmt.setString(1, new_course.getCourse_titol());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }

    public List<Professor> getProfessors() throws DAOException {
        Connection con = null;
        PreparedStatement stmt = null;
        List<Professor> listResult = new ArrayList<>();
        try {
            con = connectToDB();
            stmt = con.prepareStatement("SELECT * FROM docenti WHERE Delete_date = ''");
            ResultSet res = stmt.executeQuery();
            while (res.next()){
                listResult.add(new Professor(res.getString("Email"), res.getString("Nome"),res.getString("Cognome")));
            }
            return listResult;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }

    public int removeCourse(Course course) throws DAOException {
        Connection con = null;
        try {
            con = connectToDB();
            removeBookings(null, course, null, 0, 0, null);
            removeTeachings(null, course);
            PreparedStatement stmt = con.prepareStatement("UPDATE corsi SET Delete_date = ? WHERE Titolo = ? AND Delete_date = '' ");
            stmt.setString(1, CustomDate.getDate());
            stmt.setString(2, course.getCourse_titol());
            int res = stmt.executeUpdate();
            return res;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }

    public boolean insertTeaching(Professor professor, Course course) throws DAOException {
        Connection con = null;
        try {
            con = connectToDB();
            String query = "SELECT * FROM insegnamenti WHERE Email_docente = '" + professor.getEmail() + "' AND Titolo_corso = '" + course.getCourse_titol() + "'";
            checkForDeleteDate(query, con);
            PreparedStatement stmt = con.prepareStatement("IF (SELECT COUNT(*) FROM docenti WHERE Email = ? AND Delete_date <> '') = 0 AND (SELECT COUNT(*) FROM corsi WHERE Titolo = ? AND Delete_date <> '') = 0 THEN INSERT INTO insegnamenti (Email_docente,Titolo_corso) VALUES (?,?); END IF;");
            stmt.setString(1, professor.getEmail());
            stmt.setString(2, course.getCourse_titol());
            stmt.setString(3, professor.getEmail());
            stmt.setString(4, course.getCourse_titol());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }

    public int removeTeachings(Professor professor, Course course) throws DAOException {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = connectToDB();
            if (professor != null && course != null) {
                removeBookings(professor, course, null, 0, 0, null);
                stmt = con.prepareStatement("UPDATE insegnamenti SET Delete_date = ? WHERE Email_docente = ? AND Titolo_corso = ? AND Delete_date = '' ");
                stmt.setString(1, CustomDate.getDate());
                stmt.setString(2, professor.getEmail());
                stmt.setString(3, course.getCourse_titol());
            } else if (professor == null) {
                stmt = con.prepareStatement("UPDATE insegnamenti SET Delete_date = ? WHERE Titolo_corso = ? AND Delete_date = '' ");
                stmt.setString(1, CustomDate.getDate());
                stmt.setString(2, course.getCourse_titol());
            } else if (course == null) {
                stmt = con.prepareStatement("UPDATE insegnamenti SET Delete_date = ? WHERE Email_docente = ? AND Delete_date = '' ");
                stmt.setString(1, CustomDate.getDate());
                stmt.setString(2, professor.getEmail());
            }
            int res = stmt.executeUpdate();
            return res;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }

    public List<Teaching> getTeachings() throws DAOException {
        Connection con = null;
        try {
            List<Teaching> list = new ArrayList<>();
            con = connectToDB();
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM insegnamenti i JOIN docenti d ON i.Email_docente = d.Email WHERE i.Delete_date = '' ");  // OCCHIO !!! RIVEDERE SE Delete_date ci deve essere
            ResultSet res = stmt.executeQuery();
            while (res.next()) {
                Professor p = new Professor(res.getString("Email_docente"), res.getString("d.Nome"), res.getString("d.Cognome"));
                Course c = new Course(res.getString("Titolo_corso"));
                Teaching t = new Teaching(p, c);
                list.add(t);
            }
            return list;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }

    public boolean insertBooking(Professor professor, Course course, User user, int day, int month, String hour) throws DAOException {
        Connection con = null;
        try {
            con = connectToDB();
            String query = "SELECT * FROM prenotazioni WHERE Email_docente = '" + professor.getEmail() + "' AND Titolo_corso = '" + course.getCourse_titol() + "' AND Email_utente = '" + user.getEmail() + "' AND Giorno = '" + day + "' AND Mese = '" + month + "' AND Orario = '" + hour + "' AND Stato = 3";
            checkForDeleteDate(query, con);
//            if(checkForBookingAv(user,day,month,hour,con)){
//                throw new DAOException(DAOException._FAIL_TO_INSERT);
//            }
            PreparedStatement stmt = con.prepareStatement("IF (SELECT COUNT(*) FROM insegnamenti WHERE Email_docente = ? AND Titolo_corso = ? AND Delete_date <> '') = 0 AND (SELECT COUNT(*) FROM utenti WHERE Email = ? AND Delete_date <> '') = 0 THEN INSERT INTO prenotazioni (Email_docente,Titolo_corso,Email_utente,Giorno,Mese,Orario) VALUES (?,?,?,?,?,?); END IF;");
            stmt.setString(1, professor.getEmail());
            stmt.setString(2, course.getCourse_titol());
            stmt.setString(3, user.getEmail());
            stmt.setString(4, professor.getEmail());
            stmt.setString(5, course.getCourse_titol());
            stmt.setString(6, user.getEmail());
            stmt.setInt(7, day);
            stmt.setInt(8, month);
            stmt.setString(9, hour);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }

    /*Controlla che l'utente non abbia già prenotato una lezione per quell'ora e in quel giorno,se sì ritorna true,false altrimenti*/
    private boolean checkForBookingAv(User user,int day,int month,String hour,Connection con) throws SQLException{
        PreparedStatement stmt = con.prepareStatement("SELECT * FROM prenotazioni WHERE Email_utente = ? AND Giorno = ? AND Mese = ? AND Orario = ? AND Delete_date = '' ");
        stmt.setString(1,user.getEmail());
        stmt.setInt(2,day);
        stmt.setInt(3,month);
        stmt.setString(4,hour);
        ResultSet res = stmt.executeQuery();
        return res.isBeforeFirst();
    }



    public int removeBookings(Professor professor, Course course, User user, int day, int month, String hour) throws DAOException {
        Connection con = null;
        try {
            con = connectToDB();
            PreparedStatement stmt = null;
            if (professor != null && course != null && user != null) {
                stmt = con.prepareStatement("UPDATE prenotazioni SET Stato = ?,Delete_date = ? WHERE Email_docente = ? AND Titolo_corso = ? AND Email_utente = ? AND Giorno = ? AND Mese = ? AND Orario = ? AND Stato = 1 AND Delete_date = ''");
                stmt.setInt(1, 3);               // STATE 3 == Deleted
                stmt.setString(2,CustomDate.getDate());
                stmt.setString(3, professor.getEmail());
                stmt.setString(4, course.getCourse_titol());
                stmt.setString(5, user.getEmail());
                stmt.setInt(6, day);
                stmt.setInt(7, month);
                stmt.setString(8, hour);
            }
            if (professor != null && course != null && user == null) {
                stmt = con.prepareStatement("UPDATE prenotazioni SET Stato = ? WHERE Email_docente = ? AND Titolo_corso = ? AND Stato = 1 AND Delete_date = ''");
                stmt.setInt(1, 3);
                stmt.setString(2, professor.getEmail());
                stmt.setString(3, course.getCourse_titol());
            }
            if (professor == null && course != null && user == null) {
                stmt = con.prepareStatement("UPDATE prenotazioni SET Stato = ? WHERE Titolo_corso = ? AND Stato = 1 AND Delete_date = ''");
                stmt.setInt(1, 3);
                stmt.setString(2, course.getCourse_titol());
            }
            if (professor != null && course == null && user == null) {
                stmt = con.prepareStatement("UPDATE prenotazioni SET Stato = ? WHERE Email_docente = ? AND Stato = 1 AND Delete_date = ''");
                stmt.setInt(1, 3);
                stmt.setString(2, professor.getEmail());
            }
            if (professor == null && course == null && user != null) {
                stmt = con.prepareStatement("UPDATE prenotazioni SET Stato = ? WHERE Email_utente = ? AND Stato = 1 AND Delete_date = ''");
                stmt.setInt(1, 3);
                stmt.setString(2, user.getEmail());
            }
            int res = stmt.executeUpdate();
            return res;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }

    public List<AvBookings> getAllPossibleBookings(int startingDayOfWeek, int month) throws DAOException {
        List<AvBookings> mylist = new ArrayList<>();
        List<Teaching> list_of_teaching = getTeachings();
        List<Integer> days_of_week = DaysOfWeek.getDays(startingDayOfWeek);
        List<String> hours = Hours.getHours();
        for (Teaching t : list_of_teaching) {
            for (Integer d : days_of_week) {
                for (String h : hours) {
                    AvBookings a = new AvBookings(t.getProfessor(), t.getCourse(), d, month, h);
                    mylist.add(a);
                }
            }
        }
        return mylist;
    }

    public List<AvBookings2> getBookings() throws DAOException {
        Connection con = null;
        try {
            con = connectToDB();
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM prenotazioni p JOIN docenti d ON p.Email_docente = d.Email JOIN utenti u ON p.Email_utente = u.Email WHERE Stato != 2 && Stato != 3 AND p.Delete_date = '' AND p.Delete_date = '' ");
            ResultSet res = stmt.executeQuery();

            List<AvBookings2> list = new ArrayList<>();
            while (res.next()) {
                String professorEmail = res.getString("Email_docente");
                String professorName = res.getString("d.Nome");
                String professorSurname = res.getString("d.Cognome");
                String courseTitol = res.getString("Titolo_corso");
                String userEmail = res.getString("u.Email");
                String userName = res.getString("u.Nome");
                String userSurname = res.getString("u.Cognome");
                Integer day = res.getInt("Giorno");
                Integer month = res.getInt("Mese");
                String hour = res.getString("Orario");
                Integer state = res.getInt("Stato");
                AvBookings2 av = new AvBookings2(new Professor(professorEmail, professorName, professorSurname), new Course(courseTitol), day, hour, new User(userEmail, userName, userSurname), month, state);
                list.add(av);
            }
            return list;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }


    public List<AvBookings> getOnlyAvailableBookings(int startingDayOfWeek, int month) throws DAOException {
        Connection con = null;
        try {
            con = connectToDB();
            Set<AvBookings> set = new HashSet<>();
            List<AvBookings> list1 = getAllPossibleBookings(startingDayOfWeek, month);

            List<AvBookings2> list2 = getBookings();

            for (AvBookings a : list1) {
                if (!list2.contains(a)) {
                    set.add(a);
                }
            }

            List<AvBookings> list3 = new ArrayList<>();
            list3.addAll(set);
            return list3;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }

    public List<AvBookings> getOnlyAvailableBookingsForCourseAndProfessor(int startingDayOfWeek, int month,Course course,Professor professor) throws DAOException {
        List<AvBookings> list = getOnlyAvailableBookings(startingDayOfWeek,month);
        List<AvBookings> filteredList = new ArrayList<>();
        for(AvBookings b :list){
            if(b.getCourse().equals(course) && b.getProfessor().equals(professor)){
                filteredList.add(b);
            }
        }
        return filteredList;
    }

    /*OTTIENI LE LEZIONI DISPONIBILI DA PRENOTARE DI MODO CHE L'UTENTE NON VEDA LEZIONI GIA' PRENOTATE DA LUI PER QUELL'ORA */
    public List<AvBookings>    getOnlyAvailableBookingsForCourseAndProfessorPlusUser(int startingDayOfWeek, int month,Course course,Professor professor,User user) throws DAOException {
        List<AvBookings> filteredList = getOnlyAvailableBookingsForCourseAndProfessor(startingDayOfWeek,month,course,professor);



        List<AvBookings2> l = getBookingsForUserV2(user,null,professor,course);

        List<AvBookings> l2 = new ArrayList<>();


        boolean flag = false;






        for(AvBookings avb : filteredList){
            flag = false;
            for(AvBookings2 a : l){
                if(avb.getDay().equals(a.getDay()) && avb.getMonth().equals(a.getMonth()) && avb.getHour().equals(a.getHour())){
                    flag = true;
                }
            }
            if(!flag){
                l2.add(avb);
            }
        }



        return l2;
    }



    public void getAvailableBookings(Course course,Professor professor,Integer day,Integer month) throws DAOException {
        try {
            Connection con = connectToDB();
            PreparedStatement stmt = con.prepareStatement("WITH possible_combinations AS (\n" +
                    " WITH days AS ( SELECT 0 AS Giorno UNION SELECT 1 UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 ), \n" +
                    " times AS ( SELECT '15:00-16:00' AS Orario UNION SELECT '16:00-17:00' UNION SELECT '17:00-18:00' UNION SELECT '18:00-19:00' ),\n" +
                    " courses AS ( SELECT ? AS Titolo_corso ), \n" +
                    " teachers AS ( SELECT ? AS Email_docente ),\n" +
                    "    month AS (SELECT ? AS Mese)\n" +
                    " \n" +
                    " SELECT DISTINCT Giorno + ? AS Giorno,Mese, Orario, Titolo_corso, Email_docente FROM days \n" +
                    " CROSS JOIN times \n" +
                    " CROSS JOIN courses \n" +
                    " CROSS JOIN teachers\n" +
                    " CROSS JOIN month) \n" +
                    " \n" +
                    " \n" +
                    " \n" +
                    " SELECT pc.Email_docente,pc.Giorno,pc.Mese,pc.Orario,pc.Titolo_corso,p.Email_utente,p.Stato\n" +
                    " FROM possible_combinations as pc LEFT JOIN prenotazioni as P on pc.Giorno = p.Giorno AND pc.Orario = p.Orario and pc.Titolo_corso = p.Titolo_corso AND pc.Email_docente = p.Email_docente;\n" +
                    " ");
            stmt.setString(1,course.getCourse_titol());
            stmt.setString(2,professor.getEmail());
            stmt.setInt(3,month);
            stmt.setInt(4,day);

            ResultSet res = stmt.executeQuery();


        } catch (SQLException e) {
            throw new DAOException(e);
        }
    }

    public List<AvBookings2> getBookingsForUser(User user, Integer state) throws DAOException {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = connectToDB();
            if (state == null) {
                stmt = con.prepareStatement("SELECT * FROM prenotazioni p JOIN docenti d ON p.Email_docente = d.Email JOIN utenti u ON p.Email_utente = u.Email WHERE u.Email = ?");
                stmt.setString(1,user.getEmail());
            }
            else{
                stmt = con.prepareStatement("SELECT * FROM prenotazioni p JOIN docenti d ON p.Email_docente = d.Email JOIN utenti u ON p.Email_utente = u.Email WHERE u.Email = ? AND Stato = ? ");
                stmt.setString(1,user.getEmail());
                stmt.setInt(2,state);
            }
            ResultSet res = stmt.executeQuery();
            List<AvBookings2> list = new ArrayList<>();
            while (res.next()) {
                String professorEmail = res.getString("Email_docente");
                String professorName = res.getString("d.Nome");
                String professorSurname = res.getString("d.Cognome");
                String courseTitol = res.getString("Titolo_corso");
                String userEmail = res.getString("u.Email");
                String userName = res.getString("u.Nome");
                String userSurname = res.getString("u.Cognome");
                Integer day = res.getInt("Giorno");
                Integer month = res.getInt("Mese");
                String hour = res.getString("Orario");
                Integer state1 = res.getInt("Stato");
                AvBookings2 av = new AvBookings2(new Professor(professorEmail, professorName, professorSurname), new Course(courseTitol), day, hour, new User(userEmail, userName, userSurname), month, state1);
                list.add(av);
            }
            return list;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }

    public List<AvBookings2> getBookingsForUserV2(User user, Integer state,Professor professor,Course course) throws DAOException {
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = connectToDB();
            if (state == null) {
                stmt = con.prepareStatement("SELECT * FROM prenotazioni p JOIN docenti d ON p.Email_docente = d.Email JOIN utenti u ON p.Email_utente = u.Email WHERE u.Email = ? HAVING Stato != 3");
                stmt.setString(1,user.getEmail());
//                stmt.setString(2,professor.getEmail());
//                stmt.setString(3,course.getCourse_titol());
            }
            else{
                stmt = con.prepareStatement("SELECT * FROM prenotazioni p JOIN docenti d ON p.Email_docente = d.Email JOIN utenti u ON p.Email_utente = u.Email WHERE u.Email = ? AND Stato = ? ");
                stmt.setString(1,user.getEmail());
                stmt.setInt(2,state);
            }
            ResultSet res = stmt.executeQuery();
            List<AvBookings2> list = new ArrayList<>();
            while (res.next()) {
                String professorEmail = res.getString("Email_docente");
                String professorName = res.getString("d.Nome");
                String professorSurname = res.getString("d.Cognome");
                String courseTitol = res.getString("Titolo_corso");
                String userEmail = res.getString("u.Email");
                String userName = res.getString("u.Nome");
                String userSurname = res.getString("u.Cognome");
                Integer day = res.getInt("Giorno");
                Integer month = res.getInt("Mese");
                String hour = res.getString("Orario");
                Integer state1 = res.getInt("Stato");
                AvBookings2 av = new AvBookings2(new Professor(professorEmail, professorName, professorSurname), new Course(courseTitol), day, hour, new User(userEmail, userName, userSurname), month, state1);
                list.add(av);
            }
            return list;
        } catch (SQLException e) {
            throw new DAOException(e);
        } finally {
            handleFinalBlock(con);
        }
    }


    /*DA CANCELLARE E SOSTITUIRE CON QUELLA SOPRA UNA VOLTA AGGIUSTATO !!!!!!!!!!!!!!!!!!!!!!!!!!! */

    public int setBookingAsDone(Professor professor, Course course, User user, int day, int month, String hour) throws DAOException{
        Connection con = null;
        PreparedStatement stmt = null;
        try {
            con = connectToDB();
            stmt = con.prepareStatement("UPDATE prenotazioni SET stato = 2 WHERE Email_docente = ? AND Titolo_corso = ? AND Email_utente = ? AND Giorno = ? AND Mese = ? AND Orario = ? AND Stato = 1 AND Delete_date = '' ");
            stmt.setString(1,professor.getEmail());
            stmt.setString(2,course.getCourse_titol());
            stmt.setString(3,user.getEmail());
            stmt.setInt(4,day);
            stmt.setInt(5,month);
            stmt.setString(6,hour);
            int res = stmt.executeUpdate();

            return res;
        }catch (SQLException e){
            throw new DAOException(e);
        }finally {
            handleFinalBlock(con);
        }
    }

    public List<Course> getAvailableCourseForProfessor(Professor professor) throws DAOException {
        Connection con = null;
        PreparedStatement stmt = null;
        List<Course> listResult = new ArrayList<>();
        try {
            con = connectToDB();
            stmt = con.prepareStatement("SELECT c.Titolo\n" +
                    "FROM corsi c\n" +
                    "WHERE c.Delete_date = '' AND c.Titolo NOT in\n" +
                    "(SELECT i.Titolo_corso\n" +
                    "FROM insegnamenti i WHERE i.Email_docente = ? AND i.Delete_date = '')");
            stmt.setString(1,professor.getEmail());
            ResultSet res = stmt.executeQuery();
            while (res.next()){
                listResult.add(new Course(res.getString("Titolo")));
            }
            return listResult;
        }catch (SQLException e){
            throw new DAOException(e);
        }finally {
            handleFinalBlock(con);
        }
    }



    private void checkForDeleteDate(String QUERY, Connection con) throws DAOException, SQLException {

        PreparedStatement stmt = con.prepareStatement("" + QUERY + " AND Delete_date = ? ");
        stmt.setString(1, CustomDate.getDate());
        ResultSet rows = stmt.executeQuery();
        if (rows.isBeforeFirst()) {
            throw new DAOException(DAOException._WAIT_FOR_A_MINUTE);
        }
    }

    private void handleFinalBlock(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    private Connection connectToDB() throws SQLException {
        Connection con;
        con = DriverManager.getConnection(url, user, password);
        return con;
    }

    public void registerDriver() {
        try {
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            System.out.println("The drivers are correct");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    static class CustomDate {
        static String getDate() {
            LocalDateTime ldt = LocalDateTime.now();
            return DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm", Locale.ITALIAN).format(ldt);
        }
    }
    public static void main(String[] args) {
        Dao d = new Dao("jdbc:mysql://localhost:3306/ripetizioni","root","");
        try {
            System.out.println(d.getAvailableCourseForProfessor(new Professor("pippo@gmail.com")));
        } catch (DAOException e) {
            throw new RuntimeException(e);
        }

    }
}


