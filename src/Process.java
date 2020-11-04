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
    private int tATime;
    private int marker; //NOTE: this can be used as a bit value for the clock policy,
                        // or int value to store times for LRU policy
    private int currentPageIndex; //index of the current page
    private int blockedTime;

    //default
    public Process(){
        name = -1;
        this.faultTimes = new ArrayList<Integer>();
        this.tATime = 0;
        this.marker = -1;
        this.currentPageIndex = -1;
    }

    //constructor 1
    public Process(ArrayList<Integer> newPages){
        this.pages = newPages;
        name = -1;
        this.faultTimes = new ArrayList<Integer>();
        this.tATime = 0;
        this.marker = -1;
        this.currentPageIndex = -1;
    }
    //constructor 2
    public Process(ArrayList<Integer> newPages, String newName){
        this.pages = newPages;
        name = Integer.parseInt(newName);
        this.faultTimes = new ArrayList<Integer>();
        this.tATime = 0;
        this.marker = -1;
        this.currentPageIndex = 0;
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
    public int getMarker() {
        return marker;
    }
    public int getCurrentPageIndex(){
        return this.currentPageIndex;
    }

    public int getCurrentPageValue(){
        return this.pages.get(this.currentPageIndex);
    }

    public int gettATime() {
        return tATime;
    }

    public int getBlockedTime() {
        return blockedTime;
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
    public void setMarker(int newMarker) {
        this.marker = newMarker;
    }

    public void logFault(int newFaultTime){
        this.faultTimes.add(newFaultTime);
    }

    public void setCurrentPageIndex(int newIndex){
        this.currentPageIndex = newIndex;
    }

    public void incPageIndex(){
        if(this.currentPageIndex == pages.size()){
            this.currentPageIndex = 0;
        }
        else{
            this.currentPageIndex += 1;
        }
    }
    public void settATime(int tATime) {
        this.tATime = tATime;
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


}
