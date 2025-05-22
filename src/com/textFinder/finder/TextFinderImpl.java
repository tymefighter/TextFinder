package com.textFinder.finder;

import java.io.Closeable;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class TextFinderImpl implements TextFinder, Closeable {

    private final ExecutorService executor;

    public TextFinderImpl() {
        this.executor = Executors.newCachedThreadPool();
    }

    @Override
    public List<File> findText(File file, String searchText) {
        Map<String, Future<Boolean>> futureMap = new HashMap<>();
        searchFiles(file.getAbsoluteFile(), searchText, futureMap);

        List<File> matchedFiles = new ArrayList<>();
        for (Map.Entry<String, Future<Boolean>> entry : futureMap.entrySet()) {
            try {
                if (entry.getValue().get()) {
                    matchedFiles.add(new File(entry.getKey()));
                }
            } catch (Exception exception) {
                throw new RuntimeException("Error parsing matchedFiles", exception);
            }
        }

        return matchedFiles;
    }

    @Override
    public void close() {
        executor.shutdown();
    }

    private void searchFiles(File file, String searchText, Map<String, Future<Boolean>> futureMap) {
        if (file.isDirectory()) {
            File[] dirFiles = file.listFiles();
            if (dirFiles != null) {
                for (File dirFile : dirFiles) {
                    searchFiles(dirFile.getAbsoluteFile(), searchText, futureMap);
                }
            }
        } else if (file.getName().toLowerCase().endsWith(".txt")) {
            futureMap.put(file.getAbsolutePath(), executor.submit(new FileSearchCallable(file, searchText)));
        }
    }
}
