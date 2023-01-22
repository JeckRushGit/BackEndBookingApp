package com.example.tweb2_0.dao.modules;

import java.util.ArrayList;
import java.util.List;

public class DaysOfWeek{
    public static List<Integer> getDays(int startingDay){
       List<Integer> daysOfWeek;
        daysOfWeek = new ArrayList<>(5);
        for(int i = 0; i < 5; i++){
            daysOfWeek.add(startingDay);
            startingDay++;
        }
        return daysOfWeek;
    }

}