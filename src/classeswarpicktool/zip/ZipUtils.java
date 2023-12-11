/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package classeswarpicktool.zip;

import classeswarpicktool.DeploymentGUI;
import static classeswarpicktool.DeploymentGUI.newFile;
import classeswarpicktool.utils.LogUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import javax.swing.JLabel;

/**
 *
 * @author Ducnm62
 */
public class ZipUtils {

    private javax.swing.JLabel txtStatus;

    public ZipUtils(JLabel txtStatus) {
        this.txtStatus = txtStatus;
    }

    public static void doUnzip(Set<String> lstChange, String warPath, String outputPath) throws FileNotFoundException, IOException {
        System.out.println("LIST CHANGE:");
        lstChange.stream().forEach(System.out::println);
        System.out.println("______________");
        LogUtils.setStatus("Start create building folder.");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ex) {
            Logger.getLogger(DeploymentGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        String fileZip = warPath;
        if ((new File(fileZip)).exists()) {
            File destDir = new File(outputPath);
            byte[] buffer = new byte[1024];
            ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
            ZipEntry zipEntry = zis.getNextEntry();
            LogUtils.setStatus("Copying builed file.");
            System.out.println("Total extract file");
            while (zipEntry != null) {
                File newFile = newFile(destDir, zipEntry);
                LogUtils.setStatus("(Scan file) " + getDisplayLogFile(newFile.getAbsolutePath(), outputPath));
                if (lstChange.stream().anyMatch(p -> newFile.getAbsolutePath().contains(p))) {
                    if (zipEntry.isDirectory()) {
                        if (!newFile.isDirectory() && !newFile.mkdirs()) {
                            throw new IOException("Failed to create directory " + newFile);
                        }
                    } else {
                        File parent = newFile.getParentFile();
                        if (!parent.isDirectory() && !parent.mkdirs()) {
                            throw new IOException("Failed to create directory " + parent);
                        }
                        FileOutputStream fos = new FileOutputStream(newFile);
                        int len;
                        while ((len = zis.read(buffer)) > 0) {
                            fos.write(buffer, 0, len);
                        }
                        fos.close();
                    }
                }
                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
            zis.close();
        }
        LogUtils.setStatus("Create built folder success!");
    }
    
    public static void zipFolder(String srcFolderPath, String zipFolderPath) throws IOException {
        FileOutputStream fos = new FileOutputStream(zipFolderPath);
        ZipOutputStream zipOut = new ZipOutputStream(fos);
        File srcFolder = new File(srcFolderPath);

        zipFile(srcFolder, srcFolder.getName(), zipOut);
        zipOut.close();
        fos.close();
    }

    private static void zipFile(File fileToZip, String fileName, ZipOutputStream zipOut) throws IOException {
        if (fileToZip.isHidden()) {
            return;
        }

        if (fileToZip.isDirectory()) {
            if (fileName.endsWith("/")) {
                zipOut.putNextEntry(new ZipEntry(fileName));
            } else {
                zipOut.putNextEntry(new ZipEntry(fileName + "/"));
            }
            zipOut.closeEntry();

            File[] children = fileToZip.listFiles();
            if (children != null) {
                for (File childFile : children) {
                    zipFile(childFile, fileName + "/" + childFile.getName(), zipOut);
                }
            }
            return;
        }

        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileName);
        zipOut.putNextEntry(zipEntry);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = fis.read(buffer)) != -1) {
            zipOut.write(buffer, 0, bytesRead);
        }

        fis.close();
        zipOut.closeEntry();
    }

    private static String getDisplayLogFile(String absoluteFilePaht, String outputPath) {
        String parent = new File(outputPath).getParent();
        String replaceAll = absoluteFilePaht.replace(outputPath, "");
        if(replaceAll.length() >= 80){
            replaceAll = "..."+ replaceAll.substring(replaceAll.length() - 80, replaceAll.length());
        }
        return replaceAll;
    }
}
