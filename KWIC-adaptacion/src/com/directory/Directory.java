package com.directory;

import com.company.Main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Directory {
    public static List<String> files;
    public static List<String> filesKeyword;

    public static void main(String[] args) {
        try{
            input("/Users/efracuadras/Desktop/Maestria");
            keyword("software");
            output(new FileWriter(new File("src/files/output.txt")));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Input Component.
     */
    public static void input(String path) throws IOException {

        files = Files.walk(Paths.get(path))
                .filter(Files::isRegularFile)
                .map(Path::toFile)
                .map(file->file.getName())
                .collect(Collectors.toList());
    }

    public static void keyword(String word){
        String regex = ".*"+ word.toLowerCase() +".*";
        filesKeyword = files.stream().filter(name->{ return name.toLowerCase().matches(regex); })
            .collect(Collectors.toList());
    }

    public static void output(Writer writer) throws IOException {
        for (String name : filesKeyword) {
            writer.write(name);
            writer.write('\n');
        }

        writer.flush();
    }
}
