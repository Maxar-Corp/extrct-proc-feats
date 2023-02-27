import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvMappingReader {
    File csvFile = null;
    InputStream csvStream = null;
    static boolean featureMapSet = false;
    FeatureMap featureMap = null;

    public CsvMappingReader(FeatureMap featureMap, String csvFile) throws FileNotFoundException {
        this.featureMap = featureMap;
        csvStream = getInputStreamFromResources(csvFile);
    }

    public void setFeatureMap() {
        if (!featureMapSet) {
            readCsvFile();
        }
    }

    private void readCsvFile() {

        BufferedReader reader = null;

        List<String> lines = new ArrayList<>();
        if (csvStream != null) {

            InputStreamReader inputStream = null;

            try {
                inputStream = new InputStreamReader(csvStream);
                reader = new BufferedReader(inputStream);
                String line = "";
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }

            } catch (IOException e) {
                //log.error("Error reading CSV file", e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        // do nothing
                    }
                }
            }
        } else {
            FileReader inputReader = null;

            try {
                inputReader = new FileReader(csvFile);
                reader = new BufferedReader(inputReader);
                String line = "";
                while ((line = reader.readLine()) != null) {
                    lines.add(line);
                }
            } catch (IOException e) {
                //log.error("Error reading CSV file", e);
            } finally {
                try {
                    if (inputReader != null) {
                        inputReader.close();
                    }
                } catch (Exception e) {
                    // do nothing
                }
            }
        }

        String delimiter = ",";
        List<String> keys = Arrays.asList(lines.remove(0).split(delimiter));
        for (String line : lines) {
            if (StringUtils.isEmpty(line)) {
                continue;
            }
            featureMap.add(line.split(delimiter), keys);
        }

        featureMap.lock();

        if (csvStream != null) {
            try {
                csvStream.close();
            } catch (IOException e) {
                // ignore closing errors
            }
        }
    }

    private InputStream getInputStreamFromResources(String fileName) throws IllegalArgumentException, FileNotFoundException {

     File initialFile = new File("src/main/resources/" + fileName);
        InputStream resource = new FileInputStream(initialFile);

        if (resource == null) {
            throw new IllegalArgumentException("file is not found!");
        } else {
            return resource;
        }

    }
}