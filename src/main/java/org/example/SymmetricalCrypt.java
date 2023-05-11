package org.example;

import static org.example.Main.*;

public class SymmetricalCrypt implements ISymmetricalCrypt{
    RoundKeys roundKeys;
    CryptTransformation cryptTransformation;
    SymmetricalCrypt(RoundKeys roundKeys, CryptTransformation cryptTransformation) {
        this.cryptTransformation = cryptTransformation;
        this.roundKeys = roundKeys;
    }
    @Override
    public byte[] encrypt(byte[] data, byte[] key) {
        byte [][] K;
        int leng=0;
        byte[] padding;
        int i;
        leng = 8 - data.length % 8;
        padding = new byte[leng];

        for (i = 0; i < leng; i++)
            padding[i] = (byte)leng;
        // 01
        // 02 02 00000010 00000010
        // 03 03 03 000000011  000000011 000000011

        byte[] tmp = new byte[8];
        byte[] tmp2 = new byte[data.length + leng];
        byte[] bloc = new byte[8];

        K = roundKeys.generateSubKeys(key);

        int count = 0;

        for (i = 0; i < data.length + leng; i++) {
            if (i > 0 && i % 8 == 0) {
                bloc = permutFunc(bloc, IP);
                for (int r = 0; r < 16; r++){

                        bloc = cryptTransformation.encryptBloc(bloc, K[r]);


                }
                tmp = permutFunc(bloc, invIP);
                System.arraycopy(tmp, 0, tmp2, i - 8, bloc.length);
            }
            if (i < data.length)
                bloc[i % 8] = data[i];
            else{
                bloc[i % 8] = padding[count % 8];
                count++;
            }
        }
        if (bloc.length == 8){
            bloc = permutFunc(bloc, IP);
            for (int r = 0; r < 16; r++){
                    bloc = cryptTransformation.encryptBloc(bloc, K[r]);


            }
            tmp = permutFunc(bloc, invIP);
            System.arraycopy(tmp, 0, tmp2, i - 8, bloc.length);

        }

        return tmp2;
    }


    public byte[] decrypt(byte[] data, byte[] key) {


        byte [][] K;


        int i;

        // 01
        // 02 02 00000010 00000010
        // 03 03 03 000000011  000000011 000000011
        byte[] tmp;
        byte[] tmp2 = new byte[data.length];
        byte[] bloc = new byte[8];

        K = roundKeys.generateSubKeys(key);
        for (i = 0; i < data.length; i++) {
            if (i > 0 && i % 8 == 0) {
                bloc = permutFunc(bloc, IP);
                for (int r = 0; r < 16; r++){
                        bloc = cryptTransformation.decryptBloc(bloc, K[15-r]);


                }
                tmp = permutFunc(bloc, invIP);
                System.arraycopy(tmp, 0, tmp2, i-8, bloc.length);


            }
            bloc[i % 8] = data[i];

        }
        if (bloc.length == 8){
            bloc = permutFunc(bloc, IP);
            for (int r = 0; r < 16; r++){
                    bloc = cryptTransformation.decryptBloc(bloc, K[15-r]);
            }
            tmp = permutFunc(bloc, invIP);
            System.arraycopy(tmp, 0, tmp2, i - 8, bloc.length);
        }
        tmp2 = deletePadding(tmp2);

        return tmp2;
    }

    private static byte[] deletePadding(byte[] input) {
        int paddingLength = input[input.length-1];
        byte[] tmp = new byte[input.length - paddingLength];
        System.arraycopy(input, 0, tmp, 0, tmp.length);
        return tmp;
    }
}
