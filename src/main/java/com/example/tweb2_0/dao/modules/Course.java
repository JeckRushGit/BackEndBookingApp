package com.example.tweb2_0.dao.modules;

import java.util.Objects;

public class Course {
    private String course_titol;

    public Course(String course_titol) {
        this.course_titol = course_titol;
    }

    public String getCourse_titol() {
        return course_titol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Course course = (Course) o;
        return course_titol.equals(course.course_titol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(course_titol);
    }

    @Override
    public String toString() {
        return course_titol;
    }
}
