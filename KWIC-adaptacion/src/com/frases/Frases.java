package com.frases;

import com.company.Main;
import com.pdf.PDF;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.util.PDFTextStripper;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Frases {

    public static void main(String[] args) {

        try {

            PDF.getStopWords("frases.txt");

            FileInputStream fis = new FileInputStream("src/files/principito.pdf");
            PDFParser parser = new PDFParser( fis );
            parser.parse();
            PDDocument pdfDocument = parser.getPDDocument();
            PDDocumentCatalog dc = pdfDocument.getDocumentCatalog();

            PDFTextStripper stripper = new PDFTextStripper();

            for (int i =1; i < 5/*dc.getAllPages().size()*/; i++){
                stripper.setStartPage(i);
                stripper.setEndPage(i);
                String content = stripper.getText(pdfDocument);

                // input y circularshift por pagina
                PDF.input(content);
                Main.circularShift(i);
            }

            Main.alphabetizing();
            scoreWords();
            PDF.output(new FileWriter(new File("src/files/output.txt")), 0);

        } catch (IOException e) {
            System.out.println("No se puedo escribir el archivo");
        }
    }

    public static void scoreWords(){

        ArrayList<String> regexList = new ArrayList<>();
        for (String word : PDF.stopWords) {
            String regex = ".*" + word + ".*";
            regexList.add(regex);
        }

        String regex = String.join("|", regexList);

        int index = 0;
        for (Integer linea : Main.lineIndexes) {
            int lineStart = linea;
            int lineEnd = Main.sizeLineIndexes > index + 1 ? Main.lineIndexes.get(index + 1) : Main.sizeChars;
            int finalIndex = index;
            int page = Main.alphabetizedIndexes.stream().filter(word->word[0] == finalIndex)
                    .mapToInt(word-> word[3])
                    .findFirst()
                    .getAsInt();

            String sentence = "";

            for (int charIndex = lineStart; charIndex < lineEnd; charIndex++) {
                sentence += Main.chars.get(charIndex);
            }

            sentence = sentence.trim().replaceAll("[-+.^:,;!0123456789()?¿—¡\"]","");

            if(sentence.length() > 0  && sentence.matches(regex) ){
                PDF.wordFrequencies.put(sentence, PDF.wordFrequencies.getOrDefault(sentence, 0) + 1);
                Set<Integer> pages = PDF.wordReferences.getOrDefault(sentence, new HashSet<>());
                pages.add(page);
                PDF.wordReferences.put(sentence, pages );
            }

            index++;
        }
    }
}
