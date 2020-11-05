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
        Scheduler lruPolicy;
        Scheduler clockPolicy;

        System.out.println("TESTING input....");
        //Check args exists, exit if not
        if(args.length == 0) {
            System.out.println("No input arguments found.");
        }
        else{
            for(int i = 0; i < args.length; i++) {
                //check frame number input
                if (i == 0) {
                    if (Process.isNumeric(args[i]) && Integer.parseInt(args[i]) > 0) {
                        numFrames = Integer.parseInt(args[i]);
                        //frame number ok, do whatever...
                        //TEST OUTPUT
                        System.out.println("Frame number format ok.");
                        System.out.println("Frame numbers: " + numFrames);
                        inputOk = true;
                        //
                    } else {
                        System.out.println("Error parsing first argument.");
                        System.out.println("First argument should be a positive integer representing the total frames.");
                        inputOk = false;

                    }
                }
                //check quantum size input
                else if (i == 1) {
                    if (Process.isNumeric(args[1]) && Integer.parseInt(args[1]) > 0) {
                        //quantum size input is ok, do whatever.....
                        timeQuantum = Integer.parseInt(args[1]);
                        //TEST OUTPUT
                        System.out.println("Quantum size is ok.");
                        inputOk = true;
                        //
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
                    //TEST OUTPUT
                    //System.out.println("Check substring: " + check.substring(check.lastIndexOf("."), check.length()));
                    if (filePath.substring(filePath.lastIndexOf("."), filePath.length()).equals(".txt")) {
                        //TEST OUTPUT
                        System.out.println(filePath);
                        System.out.println("file extension ok.");
                        //

                        //Use file to build process objects - add them to the process list
                        //make a process name
                        String pName = filePath.substring((filePath.lastIndexOf(".") - 1),   filePath.lastIndexOf("."));
                        //create a new process object
                        Process p = new Process(Process.getInstructionList(filePath), pName);
                        //add to the list
                        processList.add(p);
                        inputOk = true;

                        //TEST OUTPUT OF PROCESS OBJECTS
                        int pCount = p.getProcessCount();
                        for(int f = 0; f < pCount; f++){
                            System.out.println(p.getName() + ": " + p.getInstruction(f));
                        }
                        //
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
            lruPolicy = new Scheduler(processList, numFrames, timeQuantum);
            clockPolicy = new Scheduler(processList, numFrames, timeQuantum);

            //TEST FRAME CALC
            System.out.println("calc frame test: " + lruPolicy.calcFrames());
            //run the algorithms
            lruPolicy.runLRU();
            //print the output

        }
        else{
            System.out.println("Please check command line args are correct and try again.");
            System.out.println("Exiting.....");
        }


    }
    //utils

}


