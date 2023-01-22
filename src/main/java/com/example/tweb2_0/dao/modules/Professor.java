package com.example.tweb2_0.dao.modules;

import java.util.Objects;

public class Professor {
    private String email;
    private String name;
    private String surname;

    public Professor(String email, String name, String surname) {
        this.email = email;
        this.name = name;
        this.surname = surname;
    }

    public Professor(String email) {
        this.email = email;
        this.name = "";
        this.surname = "";
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Professor professor = (Professor) o;
        return email.equals(professor.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email);
    }

    @Override
    public String toString() {
        return email+" "+name+" "+surname+" ";
    }
}
