package com.example.tweb2_0.dao.modules;

import java.util.ArrayList;
import java.util.List;

public class Hours {
    public static List<String> getHours(){
        List<String> hours = new ArrayList<>(4);
        hours.add("15:00-16:00");
        hours.add("16:00-17:00");
        hours.add("17:00-18:00");
        hours.add("18:00-19:00");
        return hours;
    }

}
