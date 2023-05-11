package com.example.gamegenerator.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

    /**
     * Writes the code contents to temporary files with the given file extension and file names.
     * @param fileExtension
     * @param fileNames
     * @param codeContents
     * @return a list of temporary files
     */
    public static List<File> writeCodeToTempFiles(String fileExtension, List<String> fileNames, List<String> codeContents) {
        List<File> tempFiles = new ArrayList<>();
        try {
            for (int i = 0; i < fileNames.size(); i++) {
                String fileName = fileNames.get(i);
                String content = codeContents.get(i);

                // Filenames must be at least 3 characters long
                if (fileName.length() < 3) {
                    fileName = "___" + fileName;
                }

                File tempFile = File.createTempFile(fileName, fileExtension);
                // Mark the file to be deleted on JVM exit
                //tempFile.deleteOnExit();
                Files.write(tempFile.toPath(), content.getBytes(), StandardOpenOption.WRITE);

                tempFiles.add(tempFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFiles;
    }

    /**
     * Writes a list of files to a zip file.
     * 
     * @param zipFileName
     * @param files
     * @return the zip file
     */
    public static File writeFilesToZip(String zipFileName, List<File> files) {
        // Remove colon from zip file name
        zipFileName = zipFileName.replace(":", "");

        try {
            // Create a ZipOutputStream to write the zip file
            FileOutputStream fos = new FileOutputStream(zipFileName);
            ZipOutputStream zos = new ZipOutputStream(fos);
    
            byte[] buffer = new byte[1024];
    
            for (File file : files) {
                // Create a FileInputStream to read the file
                FileInputStream fis = new FileInputStream(file);
    
                // Create a ZipEntry and add it to the ZipOutputStream
                ZipEntry ze = new ZipEntry(file.getName());
                zos.putNextEntry(ze);
    
                // Read the file into the buffer and write the buffer to the zip file
                int len;
                while ((len = fis.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
    
                // Close the FileInputStream
                fis.close();
            }
    
            // Close the ZipOutputStream
            zos.closeEntry();
            zos.close();
            
            // Return the zip file
            File file = new File(zipFileName);
            file.deleteOnExit();
            return file;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
