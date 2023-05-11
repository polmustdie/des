package org.example;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static enum modes {
        ECB,
        CBC,
        CFB,
        OFB,
        CTR,
        RD,
        RDH
    }
    // initial permuation table
     static int[] IP = { 58, 50, 42, 34, 26, 18, 10, 2, 60, 52, 44, 36,
            28, 20, 12, 4, 62, 54, 46, 38, 30, 22, 14, 6, 64, 56, 48, 40, 32,
            24, 16, 8, 57, 49, 41, 33, 25, 17, 9, 1, 59, 51, 43, 35, 27, 19,
            11, 3, 61, 53, 45, 37, 29, 21, 13, 5, 63, 55, 47, 39, 31, 23, 15, 7 };
    // inverse initial permutation
     static int[] invIP = { 40, 8, 48, 16, 56, 24, 64, 32, 39, 7, 47,
            15, 55, 23, 63, 31, 38, 6, 46, 14, 54, 22, 62, 30, 37, 5, 45, 13,
            53, 21, 61, 29, 36, 4, 44, 12, 52, 20, 60, 28, 35, 3, 43, 11, 51,
            19, 59, 27, 34, 2, 42, 10, 50, 18, 58, 26, 33, 1, 41, 9, 49, 17,
            57, 25 };

     static int[] P = { 16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5,
            18, 31, 10, 2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4,
            25

    };
    // initial key permutation 64 => 56 bit
     static int[] PC1 = { 57, 49, 41, 33, 25, 17, 9, 1, 58, 50, 42, 34,
            26, 18, 10, 2, 59, 51, 43, 35, 27, 19, 11, 3, 60, 52, 44, 36, 63,
            55, 47, 39, 31, 23, 15, 7, 62, 54, 46, 38, 30, 22, 14, 6, 61, 53,
            45, 37, 29, 21, 13, 5, 28, 20, 12, 4 };
    // key permutation at round i 56 => 48
     static int[] PC2 = { 14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10,
            23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2, 41, 52, 31, 37, 47, 55,
            30, 40, 51, 45, 33, 48, 44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29,
            32 };

     static int[] keyShift = { 1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2,
            2, 1 };

     static int[] exp = { 32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9, 8,
            9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17, 16, 17, 18, 19, 20, 21,
            20, 21, 22, 23, 24, 25, 24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32,
            1 };
    // substitution boxes
     static int[][][] sBoxes = {
            { 		{ 14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7 },
                    { 0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8 },
                    { 4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0 },
                    { 15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13 }
            },
            { 		{ 15, 1, 8, 14, 6, 11, 3, 2, 9, 7, 2, 13, 12, 0, 5, 10 },
                    { 3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5 },
                    { 0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15 },
                    { 13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9 }
            },
            { 		{ 10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8 },
                    { 13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1 },
                    { 13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7 },
                    { 1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12 }
            },
            { 		{ 7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15 },
                    { 13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9 },
                    { 10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4 },
                    { 3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14 }
            },
            { 		{ 2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9 },
                    { 14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6 },
                    { 4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14 },
                    { 11, 8, 12, 7, 1, 14, 2, 12, 6, 15, 0, 9, 10, 4, 5, 3 }
            },
            { 		{ 12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11 },
                    { 10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8 },
                    { 9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6 },
                    { 4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13 }

            },
            { 		{ 4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1 },
                    { 13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6 },
                    { 1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2 },
                    { 6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12 }

            },
            { 		{ 13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7 },
                    { 1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2 },
                    { 7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8 },
                    { 2, 1, 14, 7, 4, 10, 18, 13, 15, 12, 9, 0, 3, 5, 6, 11 }

            } };
    static byte[] getBits(byte[] input, int pos, int n) {
        int numOfBytes = (n - 1) / 8 + 1;
        byte[] out = new byte[numOfBytes];
        for (int i = 0; i < n; i++) {
            int val = getBit(input, pos + i);
            setBit(out, i, val);
        }
        return out;
    }
    static byte[] permutFunc(byte[] input, int[] table) {
        int nrBytes = (table.length - 1) / 8 + 1;
        byte[] out = new byte[nrBytes];
        for (int i = 0; i < table.length; i++) {
            int val = getBit(input, table[i] - 1);
            setBit(out, i, val);
        }
        return out;

    }

    static byte[] concatBits(byte[] a, int aLen, byte[] b, int bLen) {
        int numOfBytes = (aLen + bLen - 1) / 8 + 1;
        byte[] out = new byte[numOfBytes];
        int j = 0;
        for (int i = 0; i < aLen; i++) {
            int val = getBit(a, i);
            setBit(out, j, val);
            j++;
        }
        for (int i = 0; i < bLen; i++) {
            int val = getBit(b, i);
            setBit(out, j, val);
            j++;
        }
        return out;
    }

     static void setBit(byte[] data, int pos, int val) {
        int posByte = pos / 8;
        int posBit = pos % 8;
        byte tmpB = data[posByte];
        //1111111101111111 --- 0011111111011111
        //                     0000000011111111
        //                     0000000011011111
        //                     0000000011111111
        //                     0000000000100000
        //                     0000000011111111
        tmpB = (byte) (((0xFF7F >> posBit) & tmpB) & 0x00FF);
        byte newByte = (byte) ((val << (8 - (posBit + 1))) | tmpB);
        data[posByte] = newByte;
    }

     static int getBit(byte[] data, int pos) {
        int posByte = pos / 8;
        int posBit = pos % 8;
        byte tmpB = data[posByte];
         return tmpB >> (8 - (posBit + 1)) & 0x0001;
    }

    public static byte[] vectorGeneration(){
        byte[] buffer = new byte[8];
        for (int i = 0; i < buffer.length; i++)
            buffer[i] = (byte)(Math.random() * 256);
        return buffer;
    }

    public static void main(String[] args) {
        RoundKeys roundKeys = new RoundKeys();
        CryptTransformation cryptTransformation = new CryptTransformation();
        SymmetricalCrypt symmetricalCrypt = new SymmetricalCrypt(roundKeys, cryptTransformation);
//        String str = "ILOVEEEE";
//        String k = "5qw8sd4h";
//        byte[] encrypted = symmetricalCrypt.encrypt(str.getBytes(), k.getBytes());
//        System.out.println(new String(encrypted));
//        byte[] decrypted = symmetricalCrypt.decrypt(encrypted, k.getBytes());
//        System.out.println(new String(decrypted));
        byte[] array = new byte[1];
        byte[] array2 = new byte[1];
        try {
            array = Files.readAllBytes(Paths.get("src/main/java/org/example/img.png"));
            array2 = Files.readAllBytes(Paths.get("/Users/polinanesterova/Downloads/fruits.py"));
        } catch (IOException e) {
            System.out.println("File not found");
        }
        String str = "I love cryptography";
        byte[] k = vectorGeneration();
        byte[] encrypted = symmetricalCrypt.encrypt(array, k);
        System.out.println(new String(encrypted));
        byte[] decrypted = symmetricalCrypt.decrypt(encrypted, k);
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream("src/main/java/org/example/imgOUT.png"));
                OutputStream out1 = new BufferedOutputStream(new FileOutputStream("src/main/java/org/example/imgOUTeNCRYPTED.txt"))
        ) {
            out.write(decrypted);
            out1.write(encrypted);
        } catch (FileNotFoundException e) {
            System.out.println("Ex");
        } catch (IOException e) {
            System.out.println("Ex2");
        }


        byte[] encrypted2 = symmetricalCrypt.encrypt(array2, k);
        System.out.println(new String(encrypted));
        byte[] decrypted2 = symmetricalCrypt.decrypt(encrypted2, k);
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream("src/main/java/org/example/fruitsOUT1.py"))) {
            out.write(decrypted2);
        } catch (FileNotFoundException e) {
            System.out.println("Ex");
        } catch (IOException e) {
            System.out.println("Ex2");
        }
        System.out.println(new String(decrypted));

    }
}