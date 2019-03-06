package com.company;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        try {
            KWIC kwic = new KWIC();

            kwic.input(new FileReader(new File("input.txt")));
            kwic.circularShift();

            kwic.alphabetizer();
            kwic.output(new FileWriter(new File("output.txt")));
            /**/

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}


































