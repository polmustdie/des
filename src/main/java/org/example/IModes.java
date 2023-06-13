package org.example;

import lombok.Data;


public interface IModes {
    public void reset();
    byte[] encrypt(byte[] data);
    byte[] decrypt(byte[] data);
    public static byte[] getArray64(byte[] initArray, int startIndex)
    {
        byte[] copy = new byte[8];
        System.arraycopy(initArray, startIndex, copy, 0, 8);
        return copy;
    }
    public static byte[] XORByteArray(byte[] first, byte[] second)
    {
        if (first.length != second.length)
        {
            System.out.println("ERROR XORByteArray(byte[] first, byte[] second): Array have to be same length");
            return new byte[0];
        }
        byte[] result = new byte[first.length];
        for (int i = 0; i < first.length; i++)
        {
            result[i] = (byte) (first[i] ^ second[i]);
        }
        return result;
    }

    void setPreviousBlock(byte[] iv);
}
