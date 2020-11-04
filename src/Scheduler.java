//File: Scheduler.java
//Purpose:	Scheduler base class for COMP2240 - operating systems assignment 3
//Programmer: Liam Craft - c3339847
//Date: 3/11/2020

//NOTE: Scheduler should implement the RR process switching and time quantum stuff

import java.util.ArrayList;

public class Scheduler {
    //Class variables
    //private ArrayList<Process> inputQ;
    private final int SWITCHINGTIME = 8;
    private ArrayList<Process> readyQ;
    private ArrayList<Process> blockedQ;
    private ArrayList<Process> finishedQ;
    private Process current;
    private int globalTime;
    private int maxPages;
    private int quantum;
    //Represents main memory -
    private int[][] mainMem;

    //Default - NOTE: Might need to get rid of the default constructor,
    // or at least make sure to reinitialise the memory array
    public Scheduler(){
        //this.inputQ = new ArrayList<Process>();
        this.readyQ = new ArrayList<Process>();
        this.blockedQ = new ArrayList<Process>();
        this.current = new Process();
        this.globalTime = 0;
        this.maxPages = 0;
        this.quantum = 0;
        this.mainMem = new int[1][1];
    }

    //Constructor
    public Scheduler(ArrayList<Process> newInputQ, int newMaxPages, int newQuantum){
        //this.inputQ = newInputQ;
        this.readyQ = newInputQ;
        this.blockedQ = new ArrayList<Process>();
        this.current = new Process();
        this.maxPages = newMaxPages;
        this.quantum = newQuantum;
        this.mainMem = new int[newInputQ.size()][this.calcFrames()];
    }
    //run methods
    public void runLRU(){
        while(this.globalTime < 40){

            //while there are still processes in the readyQ (what about the blocked Queue?)
            //NOTE: should probably add a check/wait for any processes that are blocked
            while(!readyQ.isEmpty()){
                //get the first process
                this.current = getNextInput();
                //check if the current page is in mainmem
                while(checkPageInMem()){
                    //execute page, inc current page, set page marker (global time for LRU)
                    this.globalTime++;
                    this.current.incPageIndex(); //NOTE: check this method works correctly
                    this.current.setMarker(this.globalTime);
                    //check to see if any processes are unblocked
                    this.checkForReadyProcesses();
                }
                //block and page fault
                this.current.logFault(this.globalTime);
                this.block();
//                this.globalTime++;
            }
            this.globalTime++;
            //UPTO HERE. Need to step through and check if working ok so far. Set up logging etc
        }
    };
    public void runClock(){

    }

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

    public Process getNextInput(){
        return this.readyQ.remove(0);
    }

    public Process getNextReady(){
        return this.readyQ.remove(0);
    }

    public Process peekNextReady(){
        return this.readyQ.get(0);
    }

    public int getQuantum() {
        return quantum;
    }

    public int getGlobalTime() {
        return globalTime;
    }

    public int getMaxPages() {
        return maxPages;
    }

    public int[][] getMainMem() {
        return mainMem;
    }

//    public ArrayList<Process> getInputQ() {
//        return inputQ;
//    }

    public ArrayList<Process> getFinishedQ() {
        return finishedQ;
    }

    //loop through the 2d array at this processes memory index, and check if the page is there
    public Boolean checkPageInMem(){
        //TEST VAR
        int pIndex = current.getName() - 1;
        int possibleMemSize = current.getPages().size();
        for(int i = 0; i < possibleMemSize; i++){
            if(mainMem[pIndex][i] == current.getPages().get(i)) {
                return true;
            }
        }
        return false;
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

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    public void setGlobalTime(int globalTime) {
        this.globalTime = globalTime;
    }

    public void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
    }

    public void setMainMem(int[][] mainMem) {
        this.mainMem = mainMem;
    }

//    public void setInputQ(ArrayList<Process> inputQ) {
//        this.inputQ = inputQ;
//    }

    public void setFinishedQ(ArrayList<Process> finishedQ) {
        this.finishedQ = finishedQ;
    }

    //calc and allocate the total frames per process
    public int calcFrames(){
        return (maxPages/this.readyQ.size());
    }

    //block a process and log it's blocking time (this needs to be logged each time this particular page faults)
    public void block(){
        this.current.setBlockedTime(this.globalTime);
        this.blockedQ.add(this.current);
    }
    //check if any processes have become unblocked, add them to the readyQ
    public void checkForReadyProcesses(){
        for(int i = 0; i < blockedQ.size(); i++){
            Process p = blockedQ.get(i);
            if((this.globalTime - p.getBlockedTime()) == SWITCHINGTIME){
                this.readyQ.add(p);
            }
        }
    }




    //some kind of event log
}
