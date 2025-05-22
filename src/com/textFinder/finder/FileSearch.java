package com.textFinder.finder;

import com.textFinder.exceptions.FailedSearchException;
import com.textFinder.exceptions.InvalidFileExtensionException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileSearch {

    /**
     * Checks if the given file contains the specified text.
     * Throws a RuntimeException if the file is not a valid file or is a directory.
     *
     * @param file       the file to search
     * @param searchText the text to search for
     * @return true if the text is found, false otherwise
     */
    public boolean containsText(File file, String searchText) {
        if (file == null || !file.exists() || !file.isFile()) {
            throw new InvalidFileExtensionException(String.format("Invalid file: %s", (file != null ? file.getAbsoluteFile() : "null")));
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(searchText)) {
                    return true;
                }
            }
        } catch (IOException exception) {
            throw new FailedSearchException(String.format("Error reading file: %s", file.getAbsolutePath()), exception);
        }

        return false;
    }
}
