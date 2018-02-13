package com.fun.camel.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public abstract class ZIPHelper {

    public static byte[] zip(String name, String content) throws IOException {
        return zip(name, content.getBytes(Charset.forName("UTF-8")));
    }

    public static byte[] zip(String name, byte[] content) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(bos);
        zos.putNextEntry(new ZipEntry(name));
        zos.write(content);
        zos.close();
        return bos.toByteArray();
    }
}
