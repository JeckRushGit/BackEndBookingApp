package com.example.tweb2_0.dao.modules;

public class Teaching {

    private final Professor professor;
    private final Course course;

    public Teaching(Professor professor, Course course) {
        this.professor = professor;
        this.course = course;
    }

    public Professor getProfessor() {
        return professor;
    }

    public Course getCourse() {
        return course;
    }

    @Override
    public String toString() {
        return "Teaching{" +
                "professor=" + professor +
                ", course=" + course +
                '}';
    }
}
