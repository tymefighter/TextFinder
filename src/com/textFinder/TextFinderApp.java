package com.textFinder;

import com.textFinder.exceptions.ConfigValidationError;
import com.textFinder.finder.TextFinder;
import com.textFinder.finder.TextFinderImpl;

import java.io.File;
import java.util.List;

public class TextFinderApp {

    public static void main(String[] args) {
        TextFinderConfig config;
        try {
            config = new TextFinderConfig(args);
            config.validate();
        } catch (ConfigValidationError error) {
            System.err.printf("Configuration Error: %s%n", error.getMessage());
            System.exit(1);
            return;
        }

        File file = new File(config.getPath()).getAbsoluteFile();
        String searchText = config.getSearchText();

        System.out.printf("Searching path: %s%nLooking for searchText: \"%s\"%n", file.getAbsolutePath(), searchText);

        try (TextFinder finder = new TextFinderImpl()) {
            List<File> matchedFiles = finder.findText(file, searchText);
            System.out.println("\nSearch complete. Found in:");
            if (matchedFiles.isEmpty()) {
                System.out.println("No files found containing the text.");
            } else {
                for (File matchedFile : matchedFiles) {
                    System.out.println(matchedFile.getAbsolutePath());
                }
            }
        } catch (Exception exception) {
            System.err.printf("Search failed: %s%n", exception.getMessage());
            System.exit(1);
        }
    }
}
