package org.example;

public interface ISymmetricalCrypt {
    byte[] encrypt(byte[] data);
    byte[] decrypt(byte[] data);
}
