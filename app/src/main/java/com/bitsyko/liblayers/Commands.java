package com.bitsyko.liblayers;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class Commands {

    public static InputStream fileFromZip(File zip, String file) throws IOException {

        ZipFile zipFile = new ZipFile(zip);
        ZipEntry ze;

        for (Enumeration<? extends ZipEntry> zes = zipFile.entries(); zes.hasMoreElements(); ) {
            ze = zes.nextElement();

            if (ze.getName().equalsIgnoreCase(file.replaceAll(" ", ""))) {
                return zipFile.getInputStream(ze);
            }
        }


        throw new NoFileInZipException("No " + file + " in " + zip.getAbsolutePath());
    }

    public static String getSimilarFileFromZip(ZipFile zip, String fileName) throws IOException {

        ZipEntry ze;

        for (Enumeration<? extends ZipEntry> zes = zip.entries(); zes.hasMoreElements(); ) {
            ze = zes.nextElement();

            if (ze.getName().equalsIgnoreCase(fileName.replaceAll(" ", ""))) {
                return ze.getName();
            }
        }


        return null;
    }
}
