package com.pdf;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import com.company.Main;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.commons.lang3.StringUtils;

public class PDF {
    public static ArrayList<String> stopWords = new ArrayList<>();
    public static String stopWordsPattern;
    public static LinkedHashMap<String, Integer> wordFrequencies = new LinkedHashMap<>();
    public static LinkedHashMap<String, Set<Integer>> wordReferences = new LinkedHashMap<>();

    public static void main(String[] args) {

        try {

            getStopWords("es");

            FileInputStream fis = new FileInputStream("src/files/principito.pdf");
            PDFParser parser = new PDFParser( fis );
            parser.parse();
            PDDocument pdfDocument = parser.getPDDocument();
            PDDocumentCatalog dc = pdfDocument.getDocumentCatalog();

            PDFTextStripper stripper = new PDFTextStripper();

            for (int i =1; i < 3/*dc.getAllPages().size()*/; i++){
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String content = stripper.getText(pdfDocument);

                // input y circularshift por pagina
                input(content);
                Main.circularShift(i);
            }

            Main.alphabetizing();
            scoreWords();
            output(new FileWriter(new File("src/files/output.txt")));

        } catch (IOException e) {
            System.out.println("No se puedo escribir el archivo");
        }
    }

    /**
     * Input Component.
     */
    public static void input(String texto){
        int inputChar;
        Main.lineIndexes.add(0);

        boolean newLine = true;
        for(int i=0;i<texto.length();i++){
            inputChar = texto.codePointAt(i);

            if (newLine) {
                Main.lineIndexes.add( Main.chars.size() );
                newLine = false;
            }

            if (inputChar == '\n') {
                newLine = true;
                inputChar = ' ';
            }

            if ( inputChar != '\t' && inputChar != '\r' )
                Main.chars.add((char) inputChar);
        }

        Main.sizeChars = Main.chars.size();
        Main.sizeLineIndexes = Main.lineIndexes.size();
    }

    public static void scoreWords(){
        for (int index = 0; index < Main.alphabetizedIndexes.size(); index++) {
            int page = Main.alphabetizedIndexes.get(index)[3];
            int wordStart = Main.alphabetizedIndexes.get(index)[2];
            int lineEnd = Main.sizeLineIndexes > Main.alphabetizedIndexes.get(index)[0] + 1 ? Main.lineIndexes.get(Main.alphabetizedIndexes.get(index)[0] + 1) : Main.sizeChars;

            String word = "";

            for (int charIndex = wordStart; charIndex < lineEnd; charIndex++) {
                if( Main.chars.get(charIndex) == ' ')
                    break;

                word += Main.chars.get(charIndex);
            }

            word = word.trim().replaceAll("[-+.^:,;!0123456789()?¿—¡\"]","");

            if(word.length() > 0  && !StringUtils.isNumeric(word) && !stopWords.contains(word.toLowerCase()) ){

                wordFrequencies.put(word, wordFrequencies.getOrDefault(word, 0) + 1);
                Set<Integer> pages = wordReferences.getOrDefault(word, new HashSet<>());
                pages.add(page);
                wordReferences.put(word, pages );
            }
        }
    }

    /**
     * Module output Component.
     */
    public static void output(Writer writer) throws IOException {

        for (String key : wordFrequencies.keySet()) {
            if (wordFrequencies.get(key) > 3) {

                System.out.println(key);
                System.out.println( wordFrequencies.get(key));
                System.out.println( wordReferences.get(key).toString() );

                String pages = wordReferences.get(key).toString();
                writer.write(key + " : " + pages);
                writer.write('\n');
            }
        }

        writer.flush();
    }

    public static void getStopWords(String language){
        InputStream stream;

        // Read the stop words file for the given language
        //InputStream stream = this.getClass().getResourceAsStream("stopwords-0.1/languages/" + language + ".txt");
        try{
            stream = new FileInputStream("stopwords-0.1/languages/" + language + ".txt");
        }catch (Exception e){
            throw new Error("No se encontraron las stopwords - " + language);
        }

        String line;

        if (stream != null) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));

                // Loop through each stop word and add it to the list
                while ((line = bufferedReader.readLine()) != null)
                    stopWords.add(line.trim());

                ArrayList<String> regexList = new ArrayList<>();

                // Turn the stop words into an array of regex
                for (String word : stopWords) {
                    String regex = "\\b" + word + "(?![\\w-])";
                    regexList.add(regex);
                }

                // Join all regexes into global pattern
                stopWordsPattern = String.join("|", regexList);
            } catch (Exception e) {
                throw new Error("Error al leer las stopwords - " + language);
            }
        } else throw new Error("No se encontraron las stopwords - " + language);

    }
}
