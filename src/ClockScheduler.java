//File: ClockScheduler.java
//Purpose:	ClockScheduler class for COMP2240 - operating systems assignment 3
//Programmer: Liam Craft - c3339847
//Date: 3/11/2020

//NOTE: Scheduler should implement the RR process switching and time quantum stuff

import java.util.ArrayList;
import java.util.Collections;
//import java.util.Formatter;

public class ClockScheduler {
    //Class variables
    //private ArrayList<Process> inputQ;
    private final int SWITCHINGTIME = 6;
    private ArrayList<Process> readyQ;
    private ArrayList<Process> blockedQ;
    private ArrayList<Process> finishedQ;
    private Process current;
    private int globalTime;
    private int maxPages;
    private int quantum;
    private int nextFramePtr;
    private int memAlloc;
    //public String eventLog;
    //Represents main memory -
    private int[][] mainMem;

    //Default - NOTE: Might need to get rid of the default constructor,
    // or at least make sure to reinitialise the memory array
    public ClockScheduler(){
        //this.inputQ = new ArrayList<Process>();
        this.readyQ = new ArrayList<Process>();
        this.blockedQ = new ArrayList<Process>();
        this.finishedQ = new ArrayList<Process>();
        this.current = new Process();
        this.globalTime = 0;
        this.maxPages = 0;
        this.quantum = 0;
        this.mainMem = new int[1][1];
        this.nextFramePtr = 0;
    }

    //Constructor
    public ClockScheduler(ArrayList<Process> newInputQ, int newMaxPages, int newQuantum){
        //this.inputQ = newInputQ;
        this.readyQ = newInputQ;
        this.blockedQ = new ArrayList<Process>();
        this.finishedQ = new ArrayList<Process>();
        this.current = new Process();
        this.maxPages = newMaxPages;
        this.quantum = newQuantum;
        this.memAlloc = this.calcFrames();
        this.mainMem = new int[newInputQ.size()][memAlloc];
        this.nextFramePtr = 0;
    }
    //run methods
    public void runClock(){
        while(this.globalTime < 40){

            //while there are still processes in the readyQ (what about the blocked Queue?)
            //NOTE: should probably add a check/wait for any processes that are blocked
            while(!readyQ.isEmpty()){
                //get the first process
                this.current = getNextInput();
                //check if the current page is in mainmem
                int localTime = 0;
                while(!current.getIsFinished() && checkPageInMem() && (localTime < this.getQuantum())){
                    //execute page, inc current page, set page marker (global time for LRU)
                    this.globalTime++;

                    //TEST OUTPUT
                    System.out.println("Global Time: " + this.globalTime);
                    //set marker to indicate when this page was LAST used
                    //NOTE: Keep an eye on setMarker
                    this.current.setMarker(this.current.getCurrentPageIndex() , 1);
                    this.current.incPageIndex(); //NOTE: check this method works correctly
                    //check to see if any processes are unblocked
                    this.checkForReadyProcessesLRU();
                    localTime++;
                    this.current.incPageWorkCount();
                    //finish the process
                    if(this.current.getPageWorkCount() == (this.current.getProcessCount())){
                        current.setFinished();;
                        this.current.setFinishTime(globalTime);
                    }
                }
                if(current.getIsFinished()){
                    //this.current.setFinishTime(globalTime);
                    this.finishProcess();
                }
                //SHOULD BE, IF page isn't in main mem - block, else place back into readyQ()
                else if(localTime < this.getQuantum()){
                    //block and page fault
                    this.current.logFault(this.globalTime);
                    this.block();
                }
                else{
                    this.readyQ.add(this.current);
                }
//                this.globalTime++;
            }
            //inc global time
            this.globalTime++;
            //TEST OUTPUT
            System.out.println("Global Time: " + this.globalTime);
            //check to see if any processes are unblocked
            this.checkForReadyProcessesLRU();
        }
    };

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

    public int getMemAlloc(){
        return this.memAlloc;
    }

//    public ArrayList<Process> getInputQ() {
//        return inputQ;
//    }

    public ArrayList<Process> getFinishedQ() {
        return finishedQ;
    }

    //NOTE:In checkPageInMem and loadPageToMem*****, possibleMemSize variable must be used so
    // that loop doesn't iterate over entire frame numbers
    // EDIT: WRONG!, There could be repeats in the page list
    //meaning the actual page list size is big, but actual instruction list, not so big.
    //
    // pIndex represents the current page's place in the 1st indices of mainMem 2d Array

    //loop through the 2d array at this processes memory index, and check if the page is there
    public Boolean checkPageInMem(){
        //loop over currents memory stack, and check for the page value that is at the current page index
        int toFind = current.getCurrentPageValue();
        int pIndex = current.getName() - 1;
        int memAlloc = this.getMemAlloc();
        for(int i = 0; i < memAlloc; i++){
            if(toFind == mainMem[pIndex][i]){
                return true;
            }
        }
        return false;
    }
    //Check a certain process - loop through the 2d array at this processes memory index, and check if the page is there
    public Boolean checkPageInMem(Process p){
        //loop over currents memory stack, and check for the page value that is at the current page index
        int toFind = p.getCurrentPageValue();
        int pIndex = p.getName() - 1;
        int memAlloc = this.getMemAlloc();
        for(int i = 0; i < memAlloc; i++){
            if(toFind == mainMem[pIndex][i]){
                return true;
            }
        }
        return false;
    }

    //load a page into mainMem, iterate over frames to find a spot
    //if no room, switch according to the policy
    public void loadPageToMemClock(Process p){
        boolean isPlaced = false;
        int pIndex = p.getName() - 1;
        //possible mem size should be...
        int possibleMemSize = this.getMemAlloc();
        int waitingPage = p.getCurrentPageValue();
        for(int i = 0; i < possibleMemSize; i++){
            //check mem space is free
            if(mainMem[pIndex][i] == 0){
                //next page
                mainMem[pIndex][i] = waitingPage;
                isPlaced = true;
                break;
            }
        }
        if(!isPlaced){
            //Clock switch - iterate over process page markers, place current.getPageValue into mainMem in place
            //of the first page with a use bit (marker) of 0.
            //each time marker = 1, set to 0
            //if all frame markers = 1 after one cycle, replace the first one
            //int indexVal = p.getMarkerAtIndex(this.nextFramePtr);
//            for(int i = 0; i < p.getMarker().size(); i++){
//                int useBit = p.getMarkerAtIndex(i);
//                if(useBit == 0){
//
//                }
//                else{
//                    p.setMarker(this.nextFramePtr, 0);
//                }
//            }
            //assign the waiting page to the memory location
//            mainMem[pIndex][index] = waitingPage;
        }
    }

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
    //set the current process to finished
    public void finishProcess(){
        this.finishedQ.add(this.current);
    }

    //check if any processes have become unblocked, add them to the readyQ
    //if process has waited for the SWITCHINGTIME, load it's next page into memory
    public void checkForReadyProcessesLRU(){
        //iterate over blockedQ and make list of unblocked processes
        for(int i = 0; i < blockedQ.size(); i++){
            Process p = blockedQ.get(i);
            if((this.globalTime - p.getBlockedTime()) >= SWITCHINGTIME) {
                this.readyQ.add(p);
                if(!checkPageInMem(p)){
                    loadPageToMemClock(p);
                }
            }
        }
        //iterate over readyQ, remove anything that matches from the blocked list
        for(int j = 0; j < readyQ.size(); j++){
            Process rq = this.readyQ.get(j);
            for(int k = 0; k < this.blockedQ.size(); k++){
                if(rq.equals(this.blockedQ.get(k))){
                    this.blockedQ.remove(k);
                }
            }
        }
    }

    //some kind of event log
    public String getRunLog(){
        Collections.sort(this.finishedQ, new NameSort());
        String msg = "LRU - Fixed: \n" ;
        msg += "PID  Process Name       Turnaround Time  # Faults  Fault Times\n";
        for(int i = 0; i < this.finishedQ.size(); i++){
            Process p = this.finishedQ.get(i);
            msg += p.getName() + "  Process" + p.getName() + ".txt\t\t\t" + p.getFinishTime() + "\t\t" + p.getFaultCount() + "\t\t" +  p.getFaultString() + "\n";
        }
        return msg;
    }
}
