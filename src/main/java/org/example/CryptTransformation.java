package org.example;

import static org.example.Main.*;

public class CryptTransformation implements  ICryptTransformation{
    public byte[] encryptBloc(byte[] bloc, byte[] key) {
        byte[] tmp = new byte[bloc.length];
        byte[] R = new byte[bloc.length / 2];
        byte[] L = new byte[bloc.length / 2];

        tmp = permutFunc(bloc, IP);

        L = getBits(tmp, 0, IP.length/2);
        R = getBits(tmp, IP.length/2, IP.length/2);


        byte[] tmpR = R;

            R = func(R, key);

        R = xor(L, R);
        L = tmpR;


        tmp = concatBits(R, IP.length/2, L, IP.length/2);

        tmp = permutFunc(tmp, invIP);
        return tmp;
    }

    private static byte[] func(byte[] R, byte[] K) {
        byte[] tmp;
        tmp = permutFunc(R, exp);
        tmp = xor(tmp, K);
        tmp = sFunc(tmp);
        tmp = permutFunc(tmp, P);
        return tmp;
    }

    private static byte[] sFunc(byte[] in) {
        in = separateBytes(in, 6);
        byte[] out = new byte[in.length / 2];
        int halfByte = 0;
        for (int b = 0; b < in.length; b++) {
            //    0110100 01011000 11110000 00001111 10001101 010101010
            byte valByte = in[b];
            int r = 2 * (valByte >> 7 & 0x0001) + (valByte >> 2 & 0x0001);
            int c = valByte >> 3 & 0x000F;
            int val = sBoxes[b][r][c];
            if (b % 2 == 0)
                halfByte = val;
            else
                out[b / 2] = (byte) (16 * halfByte + val);
        }
        return out;
    }

    private static byte[] xor(byte[] a, byte[] b) {
        byte[] out = new byte[a.length];
        for (int i = 0; i < a.length; i++) {
            out[i] = (byte) (a[i] ^ b[i]);
        }
        return out;

    }
    private static byte[] separateBytes(byte[] in, int len) {
        int numOfBytes = (8 * in.length - 1) / len + 1; //48 / 8 = 6 byte
        byte[] out = new byte[numOfBytes];
        for (int i = 0; i < numOfBytes; i++) { //8
            for (int j = 0; j < len; j++) { //6
                int val = getBit(in, len * i + j);
                setBit(out, 8 * i + j, val);
                //      01101010 01011000 11110000 00001111 10001101 010101010
                //
                //      01101000 10...
            }
        }
        return out;
    }
}
