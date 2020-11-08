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
    private final int SWITCHINGTIME = 6;        //Used to simulate IO time
    private ArrayList<Process> readyQ;          //List of ready processes
    private ArrayList<Process> blockedQ;        //List of blocked processes
    private ArrayList<Process> finishedQ;       //List of finished processes
    private Process current;                    //The process currently executing
    private int globalTime;                     //Total elapsed time for this run
    private int maxPages;                       //The total memory space available to divide amongst processes
    private int quantum;                        //Max time a process can execute in one go/time slice
    private int memAlloc;                       //The memory allocated to each process
    private int[][] mainMem;                    //Represents main memory - First index is the processName - 1,
                                                //      -Second is the main memory allocated to that process

    //Default
    public ClockScheduler(){
        this.readyQ = new ArrayList<Process>();
        this.blockedQ = new ArrayList<Process>();
        this.finishedQ = new ArrayList<Process>();
        this.current = new Process();
        this.globalTime = 0;
        this.maxPages = 0;
        this.quantum = 0;
        this.mainMem = new int[1][1];
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
    }

    //Run method
    public void runClock(){
        //Loop while there are still processes in either ready or blocked queues
        while(!this.readyQ.isEmpty() || !this.blockedQ.isEmpty()){
            //While there are still processes in the readyQ
            while(!readyQ.isEmpty()){
                //get the first process
                this.current = getNextInput();
                //check if the current page is in mainmem
                int localTime = 0;
                while(!current.getIsFinished() && checkPageInMem() && (localTime < this.getQuantum())){
                    //execute page, inc current page, set page marker (use bit for clock)
                    this.globalTime++;
                    //set marker to indicate when this page was LAST used - this statement ensures the
                    //marker list runs parallel with the memory array...
                    if(this.current.getMarkerListSize() < this.mainMem[this.current.getName()-1].length){
                        this.current.setMarker(1);
                    }
                    else{
                        int index = this.getMemIndexOf(current.getCurrentPageValue());
                        this.current.setMarker(index ,1);
                    }
                    this.current.incPageIndex(); //NOTE: check this method works correctly
                    //check to see if any processes are unblocked
                    this.checkForReadyProcesses();
                    localTime++;
                    this.current.incPageWorkCount();
                    //finish the process
                    if(this.current.getPageWorkCount() == (this.current.getProcessCount())){
                        current.setFinished();;
                        this.current.setFinishTime(globalTime);
                    }
                }
                //add finished process to the finishedQ
                if(current.getIsFinished()){
                    this.finishProcess();
                }
                //if page isn't in main mem - block, else place back into readyQ()
                else if(localTime < this.getQuantum()){
                    //block and page fault
                    this.current.logFault(this.globalTime);
                    this.block();
                }
                //Process has ran out it's time quantum
                else{
                    this.readyQ.add(this.current);
                }
            }
            //inc global time
            this.globalTime++;
            //check to see if any processes are unblocked
            this.checkForReadyProcesses();
        }
    }

    //****Getters****

    //Return list of ready processes
    public ArrayList<Process> getReadyQ(){
        return this.readyQ;
    }

    //Return the list of blocked processes
    public ArrayList<Process> getBlockedQ(){
        return this.blockedQ;
    }

    //Return the process currently executing
    public Process getCurrent(){
        return this.current;
    }

    //Get the next ready process
    public Process getNextInput(){
        return this.readyQ.remove(0);
    }

    //Get the time quantum
    public int getQuantum() {
        return quantum;
    }

    //Get the global time value
    public int getGlobalTime() {
        return globalTime;
    }

    //Get the maximum memory space available
    public int getMaxPages() {
        return maxPages;
    }

    //Get the mainMemory Array
    public int[][] getMainMem() {
        return mainMem;
    }

    //Get the total memory allocated per process
    public int getMemAlloc(){
        return this.memAlloc;
    }

    //Returns the main memory index of the parameter value if it exists. -1 if not found.
    public int getMemIndexOf(int toFind){
        int[] mem = mainMem[this.current.getName()-1];
        for(int i = 0; i < mem.length; i++){
            if(mem[i] == toFind){
                return i;
            }
        }
        return -1;
    }

    //Get the total memory allocated per process
    public ArrayList<Process> getFinishedQ() {
        return finishedQ;
    }

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
            //int mListSize = p.getMarker().size();
            int toReplace = 0;
            int markerVal = 0;
            int nextFramePtr = 0;
            //loop until a 0 bit is found
            boolean found = false;
            while(!found){
                nextFramePtr = p.getAndIncNextFramePtr();
                markerVal = p.getMarkerAtIndex(nextFramePtr);
                if(markerVal == 0){
                    toReplace = nextFramePtr;
                    found = true;
                }
                else{
                    p.setMarker(nextFramePtr, 0);
                }
            }
            //
            //assign the waiting page to the memory location
            mainMem[pIndex][toReplace] = waitingPage;
            //set the use bit to 0
            p.setMarker(nextFramePtr, 0);
        }
    }

    //****Setters****
    //Set the list of ready processes
    public void setReadyQ(ArrayList<Process> newReadyQ){
        this.readyQ = newReadyQ;
    }

    //Set the list of blocked processes
    public void setBlockedQ(ArrayList<Process> newBlockedQ){
        this.blockedQ = newBlockedQ;
    }

    //Set the current running process
    public void setCurrent(Process newCurrent){
        this.current = newCurrent;
    }

    //Set the time quantum
    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    //Set the global time counter
    public void setGlobalTime(int globalTime) {
        this.globalTime = globalTime;
    }

    //Set the maximum page variable
    public void setMaxPages(int maxPages) {
        this.maxPages = maxPages;
    }

    //Set the a new main memory 2d array
    public void setMainMem(int[][] mainMem) {
        this.mainMem = mainMem;
    }

    //Set the list of finished processes
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

    //Check if any processes have become unblocked, add them to the readyQ
    //if process has waited for the SWITCHINGTIME, load it's next page into memory
    public void checkForReadyProcesses(){
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

    //Returns a string representation of the Process run
    public String getRunLog(){
        Collections.sort(this.finishedQ, new NameSort());
        String msg = "Clock - Fixed: \n" ;
        msg += "PID  Process Name       Turnaround Time  # Faults  Fault Times\n";
        for(int i = 0; i < this.finishedQ.size(); i++){
            Process p = this.finishedQ.get(i);
            //msg += p.getName() + "  Process" + p.getName() + ".txt\t\t\t" + p.getFinishTime() + "\t\t" + p.getFaultCount() + "\t\t" +  p.getFaultString() + "\n";
            msg += String.format("%-2s  %-10s      %-16s  %-6s   %-10s", p.getName(), "  Process" + p.getName() + ".txt", p.getFinishTime(), p.getFaultCount(), "{" + p.getFaultString() + "}\n");
        }
        return msg;
    }
}
