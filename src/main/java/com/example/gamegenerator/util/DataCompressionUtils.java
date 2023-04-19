package com.example.gamegenerator.util;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public class DataCompressionUtils {
  public static byte[] compress(byte[] data) {
    Deflater deflater = new Deflater();
    deflater.setInput(data);
    deflater.finish();

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    while (!deflater.finished()) {
      int count = deflater.deflate(buffer);
      outputStream.write(buffer, 0, count);
    }

    try {
      outputStream.close();
    } catch (IOException e) {
      // handle exception
    }

    return outputStream.toByteArray();
  }

  public static byte[] decompress(byte[] data) {
    Inflater inflater = new Inflater();
    inflater.setInput(data);

    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    byte[] buffer = new byte[1024];
    while (!inflater.finished()) {
      try {
        int count = inflater.inflate(buffer);
        outputStream.write(buffer, 0, count);
      } catch (DataFormatException e) {
        // handle exception
      }
    }

    try {
      outputStream.close();
    } catch (IOException e) {
      // handle exception
    }

    return outputStream.toByteArray();
  }
}