package org.example;

import static org.example.Main.*;

public class RoundKeys implements IRoundKeys{
    @Override
    public byte[][] generateSubKeys(byte[] key) {
        byte[][] tmp = new byte[16][];
        byte[] tmpK = permutFunc(key, PC1); //DELETED 8 BITS

        byte[] C = getBits(tmpK, 0, PC1.length/2);
        byte[] D = getBits(tmpK, PC1.length/2, PC1.length/2);

        for (int i = 0; i < 16; i++) {

            C = shiftLeft(C, 28, keyShift[i]);
            D = shiftLeft(D, 28, keyShift[i]);

            byte[] cd = concatBits(C, 28, D, 28);

            tmp[i] = permutFunc(cd, PC2);
        }
        return tmp;
    }
    private static byte[] shiftLeft(byte[] input, int len, int pas) {
        int nrBytes = (len - 1) / 8 + 1;
        byte[] out = new byte[nrBytes];
        for (int i = 0; i < len; i++) {
            int val = getBit(input, (i + pas) % len);
            setBit(out, i, val);
        }
        return out;
    }



    }

