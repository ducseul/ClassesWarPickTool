/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classeswarpicktool.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ducnm62
 */
public class FileUtils {

    public static final String PROPERTIES_FILE = "config.properties";

    public static void writeToPropertiesFile(String key, String value) {
        Properties prop = new Properties();
        OutputStream output = null;

        try {
            File file = new File(PROPERTIES_FILE);
            if (!file.exists()) {
                file.createNewFile();
            }

            output = new FileOutputStream(PROPERTIES_FILE);
            try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
                prop.load(input);
            } catch (FileNotFoundException ignored) {

            }
            prop.setProperty(key, value);
            prop.store(output, null);
        } catch (IOException io) {
            io.printStackTrace();
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static String readFromPropertiesFile(String key, String defaultValue) {
        String value = readFromPropertiesFile(key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        return value;
    }

    public static String readFromPropertiesFile(String key) {
        Properties prop = new Properties();
        File file = new File(PROPERTIES_FILE);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException ex) {
                Logger.getLogger(FileUtils.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        try (InputStream input = new FileInputStream(PROPERTIES_FILE)) {
            prop.load(input);
            return prop.getProperty(key);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static boolean folderContainsFilesOrSubfolders(String folderPath) {
        File folder = new File(folderPath);
        if (folder.exists() && folder.isDirectory()) {
            File[] files = folder.listFiles();
            if (files != null && files.length > 0) {
                return true; // Folder contains files or subfolders
            }
        }
        return false; // Folder doesn't exist or is empty
    }
}
