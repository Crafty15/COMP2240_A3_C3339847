//File: Process.java
//Purpose:	Process object class for COMP2240 - operating systems assignment 3
//Programmer: Liam Craft - c3339847
//Date: 1/11/2020

import java.io.*;
import java.util.ArrayList;


public class Process {
    //Class variables
    private ArrayList<Integer> pages;               //List of pages for this process
    private int name;                               //The name of the process
    private ArrayList<Integer> faultTimes;          //List of fault times
    private int finishTime;                         //The time this process finishes executing
    private int pageWorkCount;                      //Keeps track of how many time increments have been applied to this process during execution
    private boolean isFinished;                     //Set to true when process is determined to be finished
    private ArrayList<Integer> marker;              //A parallel arraylist used to keep track of page markers for replacement policies
    //NOTE: marker ArrayList can be used as a use bit value for the clock policy,
    // or int value to store times for LRU policy
    private int currentPageIndex;                   //Index (of the pages list) of the current page being executed
    private int blockedTime;                        //Logs the most recent blocking time for this process
    private int nextFramePtr;                       //Keeps track of the next frame to be checked for replacement

    //Default constructor
    public Process(){
        name = -1;
        this.faultTimes = new ArrayList<Integer>();
        this.finishTime = -1;
        this.marker = new ArrayList<Integer>();
        this.currentPageIndex = -1;
        this.isFinished = false;
        this.nextFramePtr = 0;
    }

    //Constructor 1
    public Process(ArrayList<Integer> newPages){
        this.pages = newPages;
        name = -1;
        this.faultTimes = new ArrayList<Integer>();
        this.finishTime = -1;
        this.marker = new ArrayList<Integer>();
        this.currentPageIndex = -1;
        this.isFinished = false;
        this.nextFramePtr = 0;
    }

    //Constructor 2
    public Process(ArrayList<Integer> newPages, int newName){
        this.pages = newPages;
        name = newName;
        this.faultTimes = new ArrayList<Integer>();
        this.finishTime = -1;
        this.marker = new ArrayList<Integer>();
        this.currentPageIndex = 0;
        this.isFinished = false;
        this.nextFramePtr = 0;
    }

    //****Getters****

    //Returns the Arraylist of pages for this process
    public ArrayList<Integer> getPages(){
        return this.pages;
    }

    //Returns the name (number) of this process
    public int getName(){
        return this.name;
    }

    //Returns then number of pages belonging to this process
    public int getProcessCount(){
        return this.pages.size();
    }

    //Returns the instruction value at a given index of the pages list
    public int getInstruction(int i){
        return this.pages.get(i);
    }

    //Returns the ArrayList of fault times
    public ArrayList<Integer> getFaultTimes(){
        return this.faultTimes;
    }

    //Returns the total number of faults logged so far
    public int getFaultCount(){
        return this.faultTimes.size();
    }

    //Returns the page marker list
    public ArrayList<Integer> getMarker() {
        return this.marker;
    }

    //Returns the page marker at the given index of the page marker ArrayList
    public int getMarkerAtIndex(int index){
        return this.marker.get(index);
    }

    //Returns the index value (of the page list) of the page currently executing
    public int getCurrentPageIndex(){
        return this.currentPageIndex;
    }

    //Returns the instruction value of the page currently executing
    public int getCurrentPageValue(){
        return this.pages.get(this.currentPageIndex);
    }

    //Returns the value of the most recent blocking time
    public int getBlockedTime() {
        return blockedTime;
    }

    //Returns the total execution count for this process
    public int getPageWorkCount(){
        return this.pageWorkCount;
    }

    //Returns the finishing time of this process
    public int getFinishTime() {
        return finishTime;
    }

    //Returns a boolean representing if this process if finished executing.
    public boolean getIsFinished(){
        return this.isFinished;
    }

    //Returns the size of the page marker list
    public int getMarkerListSize(){
        return this.marker.size();
    }

    //Returns the value of the next frame index pointer
    public int getNextFramePtr(){
        return this.nextFramePtr;
    }

    //****Setters****
    //Set a new page list
    public void setPages(ArrayList<Integer> newPages){
        this.pages = newPages;
    }

    //Set a new process name
    public void setName(int newName){
        this.name = newName;
    }

    //Set a new fault time list
    public void setFaultTimes(ArrayList<Integer> newFaultTimes){
        this.faultTimes = newFaultTimes;
    }

    //Set a new page marker list
    public void setMarkerList(ArrayList<Integer> newMarkerList) {
        this.marker = newMarkerList;
    }

    //Add a new page marker to the end of the marker list
    public void setMarker(int newMarker){
        this.marker.add(newMarker);
    }

    //Set a page marker at a certain index
    public void setMarker(int index, int newMarker) {
        if(this.marker.size() <= index){
            this.marker.add(newMarker);
        }
        else{
            this.marker.set(index, newMarker);
        }

    }

    //Set a new page work count
    public void setPageWorkCount(int newCount){
        this.pageWorkCount= newCount;
    }

    //Increment the page work count by 1
    public void incPageWorkCount(){
        this.pageWorkCount++;
    }

    //Add a fault time to the end of the faultTimes list
    public void logFault(int newFaultTime){
        this.faultTimes.add(newFaultTime);
    }

    //Set the page index execution "pointer"
    public void setCurrentPageIndex(int newIndex){
        this.currentPageIndex = newIndex;
    }

    //Increment the page index pointer by one
    public void incPageIndex(){
        if((this.currentPageIndex) == pages.size()){
            this.currentPageIndex = 0;
        }
        else{
            this.currentPageIndex++;
        }
    }
    //Increment the frame pointer: This will wrap back to the start if it reaches the end
    //of the arraylist. Used to iterate over the marker list in a circular manner.
    public void incFramePointer(){
        if(this.nextFramePtr == this.marker.size() - 1){
            this.nextFramePtr = 0;
        }
        else{
            this.nextFramePtr++;
        }
    }

    //Set a new value for the next frame pointer
    public void setNextFramePtr(int newPtr){
        this.nextFramePtr = newPtr;
    }

    //Get the current frame ptr value then increment by one
    public int getAndIncNextFramePtr(){
        int result = this.nextFramePtr;
        this.incFramePointer();
        return result;
    }

    //Set the time that this process finished executing
    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    //Mark this process as finished
    public void setFinished(){
        this.isFinished = true;
    }

    //Set the most recent time this process was blocked
    public void setBlockedTime(int blockedTime) {
        this.blockedTime = blockedTime;
    }

    //****Utility methods****
    public static ArrayList<Integer> getInstructionList(String filePath){
        ArrayList<Integer> result = new ArrayList<Integer>();
        try{
            FileReader fRead = new FileReader(filePath);
            BufferedReader bRead = new BufferedReader(fRead);
            //assign each line of the input file to an arraylist index
            //Input files start with "begin", end with "end"
            String input = bRead.readLine();
            while(!input.equalsIgnoreCase("end")){
                if(isNumeric(input)){
                    result.add(Integer.parseInt(input));
                }
                input = bRead.readLine();
            }
        }
        catch(IOException e){
            System.out.println("IOException in Process.createProcessFromFile: " + e.getMessage());
        }
        catch(Exception e){
            System.out.println("Exception in Process.createProcessFromFile: " + e.getMessage());
        }
        return result;
    }

    //Check if a string value is a number
    public static boolean isNumeric(String arg){
        try{
            Integer.parseInt(arg);
            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }

    //Return a string representation of this processes fault times
    public String getFaultString(){
        String msg = "";
        for(int i = 0; i < this.faultTimes.size(); i++){
            if(i == this.faultTimes.size() - 1){
                msg += this.faultTimes.get(i);
            }
            else{
                msg += this.faultTimes.get(i) + ", ";
            }
        }
        return msg;
    }

    //Create a deep copy of a process list
    public static ArrayList<Process> pListDeepCopy(ArrayList<Process> pList){
        ArrayList<Process> deepCopy = new ArrayList<Process>();
        for(int i = 0; i < pList.size(); i++){
            deepCopy.add(new Process(pList.get(i).getPages(), pList.get(i).getName()));
        }
        return deepCopy;
    }
}
