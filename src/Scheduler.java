//File: Scheduler.java
//Purpose:	Scheduler base class for COMP2240 - operating systems assignment 3
//Programmer: Liam Craft - c3339847
//Date: 3/11/2020

//NOTE: Scheduler should implement the RR process switching and time quantum stuff

import java.util.ArrayList;

public abstract class Scheduler {
    //Class variables
    private ArrayList<Process> readyQ;
    private ArrayList<Process> blockedQ;
    private Process current;
    private int globalTime;
    private int maxPages;
    private int quantum;
    //Represent main memory
    private int[][] mainMem;

    //Default - NOTE: Might need to get rid of the default constructor,
    // or at least make sure to reinitialise the memory array
    public Scheduler(){
        this.readyQ = new ArrayList<Process>();
        this.blockedQ = new ArrayList<Process>();
        this.current = new Process();
        this.globalTime = 0;
        this.maxPages = 0;
        this.quantum = 0;
        this.mainMem = new int[1][1];
    }

    //Constructor
    public Scheduler(ArrayList<Process> newReadyQ, int newMaxPages, int newQuantum){
        this.readyQ = newReadyQ;
        this.blockedQ = new ArrayList<Process>();
        this.current = new Process();
        this.globalTime = newMaxPages;
        this.quantum = newQuantum;
        this.mainMem = new int[1][1];
    }
    //Abstract methods
    abstract void run();

    //Getters
    public ArrayList<Process> getReadyQ(){
        return this.readyQ;
    }

    public ArrayList<Process> getBlockedQ(){
        return this.blockedQ;
    }

    public Process getCurrent(){
        return this.current;
    }

    public Process getNextReady(){
        return getReadyQ().remove(0);
    }

    public Process peekNextReady(){
        return getReadyQ().get(0);
    }

    //

    //Setters
    public void setReadyQ(ArrayList<Process> newReadyQ){
        this.readyQ = newReadyQ;
    }

    public void setBlockedQ(ArrayList<Process> newBlockedQ){
        this.blockedQ = newBlockedQ;
    }

    public void setCurrent(Process newCurrent){
        this.current = newCurrent;
    }


    //some kind of event log
}
