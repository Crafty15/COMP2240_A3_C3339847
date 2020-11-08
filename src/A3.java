//File: A3.java
//Purpose:	Main class for COMP2240 - operating systems assignment 3
//Programmer: Liam Craft - c3339847
//Date: 1/11/2020

import java.util.ArrayList;

public class A3 {

    public static void main(String[] args) {
        //Process list
        ArrayList<Process> processList = new ArrayList<Process>();
        //input variables
        int numFrames = 0;
        int timeQuantum = 0;
        boolean inputOk = false;
        //algorithm objects
        LRUScheduler lruPolicy;
        ClockScheduler clockPolicy;
        //Check args exists, message if not
        if(args.length == 0) {
            System.out.println("No input arguments found.");
        }
        else{
            for(int i = 0; i < args.length; i++) {
                //check frame number input
                if (i == 0) {
                    if (Process.isNumeric(args[i]) && Integer.parseInt(args[i]) > 0) {
                        numFrames = Integer.parseInt(args[i]);
                        inputOk = true;
                    } else {
                        System.out.println("Error parsing first argument.");
                        System.out.println("First argument should be a positive integer representing the total frames.");
                        inputOk = false;

                    }
                }
                //check quantum size input
                else if (i == 1) {
                    if (Process.isNumeric(args[1]) && Integer.parseInt(args[1]) > 0) {
                        timeQuantum = Integer.parseInt(args[1]);
                        inputOk = true;
                    } else {
                        System.out.println("Error parsing second argument.");
                        System.out.println("Second argument should be a positive integer representing the time quantum.");
                        inputOk = false;

                    }
                }
                //check the remaining input is correct format (name.txt)
                else {
                    //check for correct file extension
                    String filePath = args[i];
                    if (filePath.substring(filePath.lastIndexOf("."), filePath.length()).equals(".txt")) {
                        //Use file to build process objects - add them to the process list
                        //make a process name
                        String pName = filePath.substring((filePath.lastIndexOf(".") - 1),   filePath.lastIndexOf("."));
                        //create a new process object
                        Process p = new Process(Process.getInstructionList(filePath), Integer.parseInt(pName));
                        //add to the list
                        //check objects are not null
                        if(p != null){
                            processList.add(p);
                            inputOk = true;
                        }
                        else{
                            inputOk = false;
                        }
                    } else {
                        System.out.println("Error parsing argument " + (i + 1) + ".");
                        System.out.println("Argument should be a .txt file.");
                        inputOk = false;

                    }
                }
            }
        }
        //build process objects if input is ok - run the algorithms in this if statement
        if(inputOk){
            lruPolicy = new LRUScheduler(processList, numFrames, timeQuantum);
            //deep copy of the list
            ArrayList<Process> processListCopy = Process.pListDeepCopy(processList);
            clockPolicy = new ClockScheduler(processListCopy, numFrames, timeQuantum);
            //run the algorithms
            lruPolicy.runLRU();
            clockPolicy.runClock();;
            //print the output
            System.out.println(lruPolicy.getRunLog());
            System.out.println("------------------------------------------------------------");
            System.out.println(clockPolicy.getRunLog());
//
        }
        else{
            System.out.println("Please check command line args are correct and try again.");
            System.out.println("Exiting.....");
        }


    }
}


