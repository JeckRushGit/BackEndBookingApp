package com.example.tweb2_0.dao.modules;


import java.util.Objects;

public class AvBookings {


    protected Professor professor;

    protected Course course;

    protected final Integer day;

    protected final Integer month;
    protected final String hour;

    public AvBookings(Professor professor, Course course, Integer day, Integer month, String hour) {
        this.professor = professor;
        this.course = course;
        this.day = day;
        this.month = month;
        this.hour = hour;
    }

    public Professor getProfessor() {
        return professor;
    }

    public Course getCourse() {
        return course;
    }

    public Integer getDay() {
        return day;
    }

    public String getHour() {
        return hour;
    }

    public Integer getMonth() {
        return month;
    }

    @Override
    public String toString() {
        return "AvBookings{" +
                "professor=" + professor +
                ", course=" + course +
                ", day=" + day +
                ", month=" + month +
                ", hour='" + hour + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if(o == null || o.getClass() != AvBookings.class && o.getClass() != AvBookings2.class){
            return false;
        }
        AvBookings that = (AvBookings) o;
        return professor.equals(that.professor) && day.equals(that.day) && hour.equals(that.hour) && month.equals(that.month);
    }



    @Override
    public int hashCode() {
        return Objects.hash(professor, course, day, month,hour);
    }
}
