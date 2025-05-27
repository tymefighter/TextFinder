package com.textFinder.finder;

import java.io.Closeable;
import java.io.File;
import java.util.List;

public interface TextFinder extends Closeable {

    List<File> findText(File file, String searchText);
}
