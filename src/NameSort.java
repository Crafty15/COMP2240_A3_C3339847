//File: NameSort.java
//Purpose:	Comparator class for sorting process objects by name COMP2240 - operating systems assignment 3
//Programmer: Liam Craft - c3339847
//Date: 6/11/2020

import java.util.Comparator;

public class NameSort implements Comparator<Process> {
    @Override
    public int compare(Process o1, Process o2) {
        if(o1.getName() == o2.getName()){
            return 0;
        }
        else if(o1.getName() < o2.getName()){
            return -1;
        }
        return 1;
    }
}
