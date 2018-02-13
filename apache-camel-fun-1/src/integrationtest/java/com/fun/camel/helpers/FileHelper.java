package com.fun.camel.helpers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static java.nio.file.StandardOpenOption.CREATE;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;

public abstract class FileHelper {

    public static void zip(String filename, String srcpath, String zipfilename) throws IOException {
        byte[] bytes = Files.readAllBytes(new File(srcpath, filename).toPath());
        byte[] zipbytes = ZIPHelper.zip(filename, bytes);
        Files.write(new File(srcpath, zipfilename).toPath(), zipbytes, TRUNCATE_EXISTING, CREATE);
    }

    public static void move(String filename, String srcpath, String destpath) throws IOException {
        Files.move(new File(srcpath, filename).toPath(), new File(destpath, filename).toPath(), REPLACE_EXISTING, ATOMIC_MOVE);
    }

    public static void zipMove(String filename, String srcpath, String zipfilename, String destpath) throws Exception {
        zip(filename, srcpath, zipfilename);
        move(zipfilename, srcpath, destpath);
    }
}
