//File: Process.java
//Purpose:	Process object class for COMP2240 - operating systems assignment 3
//Programmer: Liam Craft - c3339847
//Date: 1/11/2020

import java.io.*;
import java.util.ArrayList;


public class Process {
    //Static variables
    private static ArrayList<Process> readyQ;
    private static ArrayList<Process> blockedQ;
    private static Process current;
    //Member variables
    ArrayList<Integer> pages;
    String name;

    //default
    public Process(){
        this.pages = new ArrayList<Integer>();
        this.readyQ = new ArrayList<Process>();
        name = "default";
    }

    //constructor 1
    public Process(ArrayList<Integer> newPages){
        this.pages = newPages;
        name = "default1";
    }
    //constructor 2
    public Process(ArrayList<Integer> newPages, String newName){
        this.pages = newPages;
        name = newName;
    }

    //****Getters****
    public ArrayList<Integer> getPages(){
        return this.pages;
    }
    public String getName(){
        return this.name;
    }
    public int getProcessCount(){
        return this.pages.size();
    }
    public int getInstruction(int i){
        return this.pages.get(i);
    }

    //****Setters****
    public void setPages(ArrayList<Integer> newPages){
        this.pages = newPages;
    }

    public void setName(String newName){
        this.name = newName;
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