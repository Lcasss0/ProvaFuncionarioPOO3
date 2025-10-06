package org.example.provapoo3.utils;

import java.nio.file.Paths;

public class PathFXML {
    public static String pathBase(){
        return Paths.get("src/main/java/org/example/provapoo3/view").toAbsolutePath().toString();
    }
}
