//File: LRU.java
//Purpose:	LRU (Least recently used) algorithm simulator class for COMP2240 - operating systems assignment 3
//Programmer: Liam Craft - c3339847
//Date: 3/11/2020
import java.util.ArrayList;

public class LRU extends Scheduler {

    //Default
    public LRU() {
        super();
    }

    //Constructor
    public LRU(ArrayList<Process> newReadyQ, int newMaxPages, int newQuantum) {
        super(newReadyQ, newMaxPages, newQuantum);
    }

    @Override
    void run() {

    }
}
