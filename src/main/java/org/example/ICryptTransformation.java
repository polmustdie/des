package org.example;

public interface ICryptTransformation {
    byte[] encryptBloc(byte[] bloc, byte[] key);
}
