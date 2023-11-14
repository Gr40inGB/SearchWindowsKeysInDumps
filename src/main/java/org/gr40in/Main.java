package org.gr40in;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        Set<String> allKeys = new HashSet<>();

        try {
            List<File> pathList = Files.walk((Paths.get("D:\\YandexDisk\\---WORK---"))
                    ).filter(Files::isRegularFile)
                    .filter(f -> f.getFileName().toString().toLowerCase().endsWith(".bin"))
                    .filter(f->sizeOk(f))
                    .map((Path::toFile))
                    .toList();

            for (File file : pathList) {
                System.out.println(file);
                String temp = getKey(file);
                if (temp.length() > 5) allKeys.add(temp);
//                System.out.println(getKey(file));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        writeToFile(allKeys);

    }


    public static String getKey(File file) {

        StringBuilder result = new StringBuilder();
        try (FileInputStream inputStream = new FileInputStream(file)) {
            StringBuilder stringBuilder = new StringBuilder();
            byte[] buffer = new byte[32768];
            while (inputStream.available() > 0) {
                inputStream.read(buffer);
                for (byte b : buffer) {
                    stringBuilder.append(String.format("%02X", b));
                }
            }

            int index = stringBuilder.indexOf("010000000000000001000000000000001D000000");
            System.out.println(index);
            if (index > 0) {
                for (int i = index + 40; i < index + 98; i += 2) {
                    String buf = stringBuilder.substring(i, i + 2);
                    result.append((char) Integer.parseInt(buf, 16));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return result.toString();
    }

    public static void writeToFile(Set<String> dataSet) {
        try (FileWriter fileWriter = new FileWriter("D:\\keys.txt")) {
            for (String key : dataSet) {
                fileWriter.write(key + "\n");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean sizeOk(Path file) {
        int eight = 1024 * 1024 * 8;
        int sixteen = 1024 * 1024 * 16;
        try {
            long size = Files.size(file);
            return size == eight || size == sixteen;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


