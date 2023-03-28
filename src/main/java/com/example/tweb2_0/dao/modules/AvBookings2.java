package com.example.tweb2_0.dao.modules;

public class AvBookings2 extends AvBookings
{

    private final User user;

    private final Integer state;

    public AvBookings2(Professor professor, Course course, Integer day, String hour, User user, Integer month, Integer state) {
        super(professor,course,day,month,hour);
        this.user = user;
        this.state = state;
    }

    public User getUser() {
        return user;
    }


    public Integer getState() {
        return state;
    }

    @Override
    public String toString() {
        return "AvBookings2{" +
                "professor=" + professor +
                ", course=" + course +
                ", day=" + day +
                ", hour='" + hour +
                " user=" + user +
                ", month=" + month +
                ", state=" + state +
                '}';
    }


}
