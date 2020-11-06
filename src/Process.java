//File: Process.java
//Purpose:	Process object class for COMP2240 - operating systems assignment 3
//Programmer: Liam Craft - c3339847
//Date: 1/11/2020

import java.io.*;
import java.util.ArrayList;


public class Process {

    //Class variables
    private ArrayList<Integer> pages;
    private int name;
    private ArrayList<Integer> faultTimes;
//    private int tATime;
    private int finishTime;
    private int pageWorkCount;
    private boolean isFinished;
    private ArrayList<Integer> marker;  //A parallel arraylist used to keep track of page markers for replacement policies
    //NOTE: this can be used as a use bit value for the clock policy,
    // or int value to store times for LRU policy
    private int currentPageIndex; //index of the current page
    private int blockedTime;

    //default
    public Process(){
        name = -1;
        this.faultTimes = new ArrayList<Integer>();
//        this.tATime = 0;
        this.finishTime = -1;
        this.marker = new ArrayList<Integer>();
        this.currentPageIndex = -1;
        this.isFinished = false;
    }

    //constructor 1
    public Process(ArrayList<Integer> newPages){
        this.pages = newPages;
        name = -1;
        this.faultTimes = new ArrayList<Integer>();
//        this.tATime = 0;
        this.finishTime = -1;
        this.marker = new ArrayList<Integer>();
        this.currentPageIndex = -1;
        this.isFinished = false;
    }
    //constructor 2
    public Process(ArrayList<Integer> newPages, String newName){
        this.pages = newPages;
        name = Integer.parseInt(newName);
        this.faultTimes = new ArrayList<Integer>();
//        this.tATime = 0;
        this.finishTime = -1;
        this.marker = new ArrayList<Integer>();
        this.currentPageIndex = 0;
        this.isFinished = false;
    }

    //****Getters****
    public ArrayList<Integer> getPages(){
        return this.pages;
    }
    public int getName(){
        return this.name;
    }
    public int getProcessCount(){
        return this.pages.size();
    }
    public int getInstruction(int i){
        return this.pages.get(i);
    }
    public ArrayList<Integer> getFaultTimes(){
        return this.faultTimes;
    }
    public int getFaultCount(){
        return this.faultTimes.size();
    }
    public ArrayList<Integer> getMarker() {
        return this.marker;
    }
    public int getMarkerAtIndex(int index){
        return this.marker.get(index);
    }
    public int getCurrentPageIndex(){
        return this.currentPageIndex;
    }

    public int getCurrentPageValue(){
        return this.pages.get(this.currentPageIndex);
    }

    public int getBlockedTime() {
        return blockedTime;
    }

    public int getPageWorkCount(){
        return this.pageWorkCount;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public boolean getIsFinished(){
        return this.isFinished;
    }


    //****Setters****
    public void setPages(ArrayList<Integer> newPages){
        this.pages = newPages;
    }

    public void setName(int newName){
        this.name = newName;
    }

    public void setFaultTimes(ArrayList<Integer> newFaultTimes){
        this.faultTimes = newFaultTimes;
    }
    public void setMarkerList(ArrayList<Integer> newMarkerList) {
        this.marker = newMarkerList;
    }
    public void setMarker(int index, int newMarker) {
        this.marker.add(index, newMarker);
    }

    public void setPageWorkCount(int newCount){
        this.pageWorkCount= newCount;
    }

    public void incPageWorkCount(){
        this.pageWorkCount++;
    }

    public void logFault(int newFaultTime){
        this.faultTimes.add(newFaultTime);
    }

    public void setCurrentPageIndex(int newIndex){
        this.currentPageIndex = newIndex;
    }

    public void incPageIndex(){
        //NOTE: keep an eye on this part - index could go over
        if((this.currentPageIndex) == pages.size()){
            this.currentPageIndex = 0;
        }
        else{
            this.currentPageIndex++;
        }
    }

    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    public void setFinished(){
        this.isFinished = true;
    }

    public void setBlockedTime(int blockedTime) {
        this.blockedTime = blockedTime;
    }
    //utility methods
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

    //
    public static boolean isNumeric(String arg){
        try{
            Integer.parseInt(arg);
            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }
    public String getFaultString(){
        String msg = "{";
        for(int i = 0; i < this.faultTimes.size(); i++){
            if(i == this.faultTimes.size() - 1){
                msg += this.faultTimes.get(i) + "}";
            }
            else{
                msg += this.faultTimes.get(i) + ", ";
            }

        }
        return msg;
    }
    //Process log
//    public String getLog(){
//
//    }


}
