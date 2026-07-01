package adsen.helium.utils;

import java.io.FileReader;
import java.io.IOException;

public class FileLoader {
    private final String fileName;
    private final FileReader reader;
    private String fileContents = null;

    public FileLoader(String fileName) {
        this.fileName = fileName;

        try {
            reader = new FileReader(fileName);

        } catch (IOException e) {
            throw new RuntimeException("Could not open file '" + fileName + "'", e);
        }
    }

    public String fileName() {
        return fileName;
    }

    public void readInput() {
        try {
            if (reader.ready()) {
                fileContents = reader.readAllAsString();

                reader.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not read '" + fileName + "'", e);
        }
    }

    public String getFileContents() {
        return fileContents;
    }
}
