# TextFinder

1. com.textFinder.TextFinderApp: main class
    1. Should take input from named args (flags)
    2. Should output on standard output stream
2. com.textFinder.TextFinder:
    1. List<File> findText(File file, String text); // "file" can be directory or file
    2. Should manage multi-threading => One thread per file (actual file, not directory)
    3. Create interface TextFinder and implementation TextFinderImpl as there can be multiple ways to do this, and in the future a better way could be substituted in place of this one.
3. com.textFinder.FileSearch:
    1. public boolean containsText(File file, String text);
    2. Checks if "file" contains text, also should validate if the input "file" is an actual file and not a dir (Throw runtime exception).
