package com.company;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.*;
import java.util.*;

public class Main {

    public static ArrayList<Character> chars = new ArrayList();
    public static ArrayList<Integer> lineIndexes = new ArrayList();
    public static int sizeLineIndexes, sizeChars;
    public static List<List<Integer>> wordIndexes = new ArrayList<>();
    public static int wordCounter = 0;
    public static ArrayList<int[]> alphabetizedIndexes = new ArrayList<>();

    public static void main(String[] args) {

        try {
            input(new FileReader(new File("src/files/input.txt")));
            circularShift(0);
            alphabetizing();
            output(new FileWriter(new File("src/files/output.txt")));

        } catch (IOException e) {
            System.out.println("No se puedo escribir el archivo");
        }
    }

    /**
     * Input Component.
     */
    public static void input(FileReader fileReader) throws IOException {
        int inputChar;
        lineIndexes.add(0);
        // Bandera fin
        while ((inputChar = fileReader.read()) != -1) {
            System.out.println(inputChar);
            if (inputChar == '\n') {
                inputChar = ' ';
                lineIndexes.add(chars.size() + 1);
            }
            chars.add((char) inputChar);
        }
        sizeChars = chars.size();
        lineIndexes.add(sizeChars - 1);
        sizeLineIndexes = lineIndexes.size() - 1;
    }

    /**
     * Circular Shift Component.
     */
    public static void circularShift(int page) {
        char charIterator;
        for (int l = 0; l < sizeLineIndexes; l++) {
            int lineStart = lineIndexes.get(l);
            int lineEnd = sizeLineIndexes > l+1 ? lineIndexes.get(l+ 1) : sizeChars;
            boolean newWord = true;

            wordIndexes.add(new ArrayList<>());

            for (int i = lineStart; i < lineEnd; i++) {

                if (chars.get(i) == ' ') {
                    newWord = true;
                    continue;
                }

                // Mark start of new word
                if (newWord) {
                    wordIndexes.get(l).add( i );
                    wordCounter++;
                    newWord = false;
                }
            }

            if( wordIndexes.get(l).size() == 0  )
                wordIndexes.get(l).add((int) ' ');
        }

        /*
        [][0] = numero de linea
        [][1] = numero de palabra
        [][2] = numero de wordIndexes
        [][3] = numero de pagina
        */

        int index = alphabetizedIndexes.size();
        for (int line = 0; line < sizeLineIndexes; line++) {
            for (int word = 0; word < wordIndexes.get(line).size(); word++, index++) {
                alphabetizedIndexes.add(new int[4]);
                alphabetizedIndexes.get(index)[0] = line;
                alphabetizedIndexes.get(index)[1] = word;
                alphabetizedIndexes.get(index)[2] = wordIndexes.get(line).get(word);
                alphabetizedIndexes.get(index)[3] = page;
            }
        }

    }
    /**
     * Alphabetizer Component.
     */
    public static void alphabetizing() {
        // Obtenemos la informacion de cada palabra
        //alphabetizedIndexes = new int[wordCounter][3];
        int index = 0;
        // Ordenamos las palabras
        for (int burbujaIndex = 0; burbujaIndex < alphabetizedIndexes.size() - 1; burbujaIndex++)

            for (index = 0; index < alphabetizedIndexes.size() - burbujaIndex - 1; index++) {
                int[] word = alphabetizedIndexes.get(index);
                int[] nextWord = alphabetizedIndexes.get(index+1);

                int wordIndex = wordIndexes.get(word[0]).get(word[1]);
                int nextWordIndex = wordIndexes.get(nextWord[0]).get(nextWord[1]);

                char wordChar = (char) -1;
                char nextWordChar = (char) -1;

                for (; wordChar == nextWordChar && wordIndex < sizeChars && nextWordIndex < sizeChars; wordIndex++, nextWordIndex++) {
                    wordChar = chars.get(wordIndex);
                    nextWordChar = chars.get(nextWordIndex);
                }

                if (wordChar > nextWordChar) {
                    alphabetizedIndexes.set(index, nextWord);
                    alphabetizedIndexes.set(index+1, word);
                }
            }
    }


    /**
     * ModuleOutput Component.
     */
    public static void output(Writer writer) throws IOException {
        for (int index = 0; index < alphabetizedIndexes.size(); ++index) {
            int wordStart = alphabetizedIndexes.get(index)[2];
            int lineStart = lineIndexes.get(alphabetizedIndexes.get(index)[0]);
            int lineEnd = sizeLineIndexes > alphabetizedIndexes.get(index)[0] + 1 ? lineIndexes.get(alphabetizedIndexes.get(index)[0] + 1) : sizeChars;

            // Desde la palabra hasta el final de la linea
            int wordEnd = wordStart;
            for (int charIndex = wordStart; charIndex < lineEnd; charIndex++) {
                writer.write(chars.get(charIndex));
                wordEnd++;
            }

            if (wordEnd == sizeChars) {
                writer.write(" ");
            }

            // Desde el inicio de la linea hasta el inicio de la palabra
            for (int charIndex = lineStart; charIndex < wordStart; charIndex++) {
                writer.write(chars.get(charIndex));
            }

            if( index + 1 < alphabetizedIndexes.size() )
                writer.write('\n');
        }
        writer.flush();
    }
}
