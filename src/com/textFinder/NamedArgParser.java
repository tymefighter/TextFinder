package com.textFinder;

import java.util.HashMap;
import java.util.Map;

public class NamedArgParser {

    /**
     * Parses command-line arguments in the form of --key value or --flag
     *
     * @param args the command-line arguments
     * @return a map of argument names to their values
     */
    public static Map<String, String> parseArgs(String[] args) {
        Map<String, String> namedArgs = new HashMap<>();

        int argIndex = 0;
        while (argIndex < args.length) {
            if (isNotFlag(args[argIndex])) {
                continue;
            }

            String key = args[argIndex].substring(2);
            String value;
            if (argIndex + 1 < args.length && isNotFlag(args[argIndex + 1])) {
                value = args[argIndex + 1];
                argIndex++;
                namedArgs.put(key, value);
            }
            argIndex++;
        }

        return namedArgs;
    }

    private static boolean isNotFlag(String key) {
        return !key.startsWith("--");
    }
}

