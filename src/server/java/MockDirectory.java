package server.java;

import java.io.File;
import java.util.*;

public class MockDirectory extends File {
    private List<File> files = new ArrayList<File>();

    public MockDirectory() {
        super("dir");
    }

    public void addMockFile(String fileName) {
        files.add(new MockFile(fileName));
    }

    public File[] listFiles() {
        return files.toArray(new File[files.size()]);
    }

    public boolean exists() {
        return true;
    }

    public boolean isDirectory() {
        return true;
    }
}
