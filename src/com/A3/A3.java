//File: A3.java
//Purpose:	Main class for COMP2240 - operating systems assignment 3
//Programmer: Liam Craft - c3339847
//Date: 1/11/2020
package com.A3;

public class A3 {

    public static void main(String[] args) {
        System.out.println("TESTING intellij");
        //Check args exists, exit if not
        if(args.length == 0){
            System.out.println("No input arguments found. Please try again.");
        }
        else{
            for(int i = 0; i < args.length; i++) {
                //check frame number input
                if (i == 0) {
                    if (isNumeric(args[i]) && Integer.parseInt(args[i]) > 0) {
                        //frame number ok, do whatever...
                        //TEST OUTPUT
                        System.out.println("Frame numbers are ok.");
                    } else {
                        System.out.println("Error parsing first argument.");
                        System.out.println("First argument should be a positive integer representing the total frames.");
                    }
                }
                //check quantum size input
                else if (i == 1) {
                    if (isNumeric(args[1]) && Integer.parseInt(args[1]) > 0) {
                        //quantum size input is ok, do whatever.....
                        //TEST OUTPUT
                        System.out.println("Quantum size is ok.");
                    } else {
                        System.out.println("Error parsing second argument.");
                        System.out.println("Second argument should be a positive integer representing the time quantum.");
                    }
                }
                //check the remaining input is correct format (name.txt)
                else {
                    //check for correct file extension
                    String check = args[i];
                    //TEST OUTPUT
                    //System.out.println("Check substring: " + check.substring(check.lastIndexOf("."), check.length()));
                    if (check.substring(check.lastIndexOf("."), check.length()).equals(".txt")) {
                        //TEST OUTPUT
                        System.out.println("file extension ok.");
                    } else {
                        System.out.println("Error parsing argument " + (i + 1) + ".");
                        System.out.println("Argument should be a .txt file.");
                    }
                }
            }
        }
    }
    //utils
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


