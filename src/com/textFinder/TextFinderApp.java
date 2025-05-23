package com.textFinder;

import com.namedArgParser.NamedArgParser;

import java.util.Map;

public class TextFinderApp {
    public static void main(String[] args) {
        Map<String, String> namedArgs = NamedArgParser.parseArgs(args);
        if(!namedArgs.containsKey("dir")){
            System.err.println("Error: Directory not specified. Use flag --dir to specify directory");
            System.exit(1);
        }
        if(!namedArgs.containsKey("text")){
            System.err.println("Error: Search text not specified. Use flag --text to specify search text");
            System.exit(1);
        }
        String directory=namedArgs.get("dir");
        String searchText=namedArgs.get("text");
        System.out.printf("The directory is %s and the text to look for is %s%n",directory,searchText);
    }
}