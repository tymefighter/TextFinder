package com.textFinder.finder;

import java.io.File;
import java.util.concurrent.Callable;

public class FileSearchCallable implements Callable<Boolean> {

    private final File file;
    private final String searchText;

    public FileSearchCallable(File file, String searchText) {
        this.file = file;
        this.searchText = searchText;
    }

    @Override
    public Boolean call() {
        FileSearch searcher = new FileSearch();
        return searcher.containsText(file, searchText);
    }
}
