package server.java;

import java.io.File;
import java.util.*;

public class MockFile extends File {
    private String name;

    public MockFile(String name) {
        super(name);
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public boolean exists() {
        return true;
    }

    public boolean isDirectory() {
        return false;
    }
}
