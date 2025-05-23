package com.namedArgParser;

import java.util.HashMap;
import java.util.Map;

public class NamedArgParser {

    /**
     * Parses command-line arguments in the form of --key value or --flag
     * @param args the command-line arguments
     * @return a map of argument names to their values
     */
    public static Map<String, String> parseArgs(String[] args) {
        Map<String, String> namedArgs = new HashMap<>();

        for (int i = 0; i < args.length; i++) {
            if (args[i].startsWith("--")) {
                String key = args[i].substring(2);
                String value = (i + 1 < args.length && !args[i + 1].startsWith("--")) ? args[++i] : "true";
                namedArgs.put(key, value);
            }
        }

        return namedArgs;
    }
}

