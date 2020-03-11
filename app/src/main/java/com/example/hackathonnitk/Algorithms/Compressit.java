package com.example.hackathonnitk.Algorithms;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.zip.GZIPOutputStream;

public class Compressit {
    public byte[] compress(String data) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream(data.length());
        GZIPOutputStream gzip = new GZIPOutputStream(bos);
        gzip.write(data.getBytes());
        gzip.close();
        byte[] compressed = bos.toByteArray();
        System.out.println(compressed);
        bos.close();
        final String content = Base64.getEncoder().encodeToString(compressed);
        return compressed;
    }
}
