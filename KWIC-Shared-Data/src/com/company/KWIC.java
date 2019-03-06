package com.company;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.*;

public class KWIC {

    private List<Character> chars = new ArrayList<>();
    private List<Integer> lineIndexes = new ArrayList<>();
    private List<List<Integer>> wordIndexes= new ArrayList<>();
    private int wordCounter = 0;
    private int[][] alphabetizedIndexes;

    /** Input Component. */
    public void input(Reader reader) throws IOException {
        int inputChar;

        boolean newLine = true;

        while ((inputChar = reader.read()) != -1) {

            if (newLine) {
                lineIndexes.add( chars.size() );
                newLine = false;
            }

            if (inputChar == '\n') {
                newLine = true;
                inputChar = ' ';
            }

            if ( inputChar != '\t' && inputChar != '\r' )
                chars.add( (char) inputChar );
        }
    }

    /** Circular Shift Component. */
    public void circularShift() {

        for(int lineIterator = 0; lineIterator < lineIndexes.size(); lineIterator++ ){
            int lineStart = lineIndexes.get(lineIterator);
            int lineEnd = lineIndexes.size() > lineIterator+1 ? lineIndexes.get(lineIterator + 1) : chars.size();
            boolean newWord = true;

            wordIndexes.add(new ArrayList<>());

            for (int charIterator = lineStart; charIterator < lineEnd; charIterator++) {

                if (chars.get(charIterator) == ' ') {
                    newWord = true;
                    continue;
                }

                // Mark start of new word
                if (newWord) {
                    wordIndexes.get(lineIterator).add( charIterator );
                    wordCounter++;
                    newWord = false;
                }
            }
        }
    }

    /** Alphabetizer Component. */
    public void alphabetizer() {
        // Obtenemos la informacion de cada palabra
        alphabetizedIndexes = new int[wordCounter][3];
        int index = 0;
        for (int line = 0; line < lineIndexes.size(); line++) {
            for (int word = 0; word < wordIndexes.get(line).size(); word++, index++) {
                alphabetizedIndexes[index][0] = line;   // index de linea
                alphabetizedIndexes[index][1] = word;   // index de la palabra en la linea
                alphabetizedIndexes[index][2] = wordIndexes.get(line).get(word); // index de la palabra en caracteres
            }


        }

        // Ordenamos las palabras
        for (int burbujaIndex = 0; burbujaIndex < alphabetizedIndexes.length - 1; burbujaIndex++)

            for (index = 0; index < alphabetizedIndexes.length - burbujaIndex - 1; index++) {

                int[] word = alphabetizedIndexes[index];
                int[] nextWord = alphabetizedIndexes[index+1];

                int wordIndex = wordIndexes.get( word[0] ).get( word[1] );
                int nextWordIndex = wordIndexes.get( nextWord[0] ).get( nextWord[1] );

                char wordChar = (char) -1;
                char nextWordChar = (char) -1;

                for (; wordChar == nextWordChar && nextWordIndex < chars.size(); wordIndex++, nextWordIndex++) {
                    wordChar = chars.get(wordIndex);
                    nextWordChar = chars.get(nextWordIndex);
                }

                if (wordChar > nextWordChar) {
                    alphabetizedIndexes[index] = nextWord;
                    alphabetizedIndexes[index+1] = word;
                }
            }
    }

    /** Output Component. */
    public void output(Writer writer) throws IOException {
        for (int index = 0; index < alphabetizedIndexes.length; ++index) {
            int wordStart = alphabetizedIndexes[index][2];
            int lineStart = lineIndexes.get(alphabetizedIndexes[index][0]);
            int lineEnd = lineIndexes.size() > alphabetizedIndexes[index][0] + 1 ? lineIndexes.get(alphabetizedIndexes[index][0] + 1) : chars.size();

            // Desde la palabra hasta el final de la linea
            int wordEnd = wordStart;
            for (int charIndex = wordStart; charIndex < lineEnd; charIndex++) {
                writer.write(chars.get(charIndex));
                wordEnd++;
            }

            if( wordEnd == chars.size() ) {
                writer.write(" ");
            }

            // Desde el inicio de la linea hasta el inicio de la palabra
            for (int charIndex = lineStart; charIndex < wordStart; charIndex++) {
                writer.write(chars.get(charIndex));
            }
            writer.write('\n');
        }
        writer.flush();
    }

}
