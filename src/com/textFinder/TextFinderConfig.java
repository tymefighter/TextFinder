package com.textFinder;

import com.textFinder.exceptions.ConfigValidationError;

import java.io.File;
import java.util.Map;

public class TextFinderConfig {

    private static final String PATH_FIELD = "path";
    private static final String SEARCH_TEXT_FIELD = "searchText";

    private final String path;
    private final String searchText;

    public TextFinderConfig(String[] args) {
        Map<String, String> namedArgs = NamedArgParser.parseArgs(args);
        path = namedArgs.get(PATH_FIELD);
        searchText = namedArgs.get(SEARCH_TEXT_FIELD);
    }

    public String getPath() {
        return path;
    }

    public String getSearchText() {
        return searchText;
    }

    public void validate() throws ConfigValidationError {
        if (path == null || path.isEmpty()) {
            throw new ConfigValidationError("Path cannot be null or empty.");
        }

        File file = new File(path);
        if (!file.exists()) {
            throw new ConfigValidationError("File does not exist: " + path);
        }
    }
}
