package io.arcs.grademanager.utils;

import java.io.*;

public class ObjectSerializer {

    public static String getSerialize(Serializable obj) throws IOException {
        if (obj == null)
            return "";
        try {
            ByteArrayOutputStream serialObj = new ByteArrayOutputStream();
            ObjectOutputStream objStream = new ObjectOutputStream(serialObj);
            objStream.writeObject(obj);
            objStream.close();
            return encodeBytes(serialObj.toByteArray());
        } catch (Exception e) {
            throw new IOException("Serialization error: " + e.getMessage(), e);
        }
    }

    public static String getSerializeDisregardException(Serializable obj) {
        try {
            return ObjectSerializer.getSerialize(obj);
        } catch (IOException e) {
            return null;
        }
    }

    public static Object getDeserialize(String str) throws IOException {
        if (str == null || str.length() == 0)
            return null;
        try {
            ByteArrayInputStream serialObj = new ByteArrayInputStream(decodeBytes(str));
            ObjectInputStream objStream = new ObjectInputStream(serialObj);
            return objStream.readObject();
        } catch (Exception e) {
            throw new IOException("Serialization error: " + e.getMessage(), e);
        }
    }

    public static Object getDeserializeDisregardException(String str) {
        try {
            return ObjectSerializer.getDeserialize(str);
        } catch (IOException e) {
            return null;
        }
    }

    public static String encodeBytes(byte[] bytes) {
        StringBuilder strBuf = new StringBuilder();

        for (int i : bytes) {
            strBuf.append((char) (((i >> 4) & 0xF) + ((int) 'a')));
            strBuf.append((char) (((i) & 0xF) + ((int) 'a')));
        }

        return strBuf.toString();
    }

    public static byte[] decodeBytes(String str) {
        byte[] bytes = new byte[str.length() / 2];
        for (int i = 0; i < str.length(); i += 2) {
            char c = str.charAt(i);
            bytes[i / 2] = (byte) ((c - 'a') << 4);
            c = str.charAt(i + 1);
            bytes[i / 2] += (c - 'a');
        }
        return bytes;
    }

}