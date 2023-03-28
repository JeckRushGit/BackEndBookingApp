package com.example.tweb2_0.dao.modules;

public class User {
    private String email;
    private String name;
    private String surname;
    private String password;
    private String role;
    private String birthday;
    private String profession;

    public User(String email, String name, String surname, String password,String birthday,String profession, String role) throws IllegalArgumentException{
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.birthday = birthday;
        this.profession = profession;
        this.role = role;
    }

    public User(String email, String name, String surname, String password,String birthday,String profession){
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.password = password;
        this.birthday = birthday;
        this.profession = profession;
        this.role = "Cliente";
    }

    public User(String email, String name, String surname){
        this.email = email;
        this.name = name;
        this.surname = surname;
    }


    public User(String email){
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getPassword() {
        return password;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getProfession() {
        return profession;
    }

    public String getRole() {
        return role;
    }

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", surname='" + surname + '\'' +
                ", password='" + password + '\'' +
                ", role='" + role + '\'' +
                ", birthday='" + birthday + '\'' +
                ", profession='" + profession + '\'' +
                '}';
    }
}
