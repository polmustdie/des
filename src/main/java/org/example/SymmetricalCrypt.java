package org.example;

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

        byte[] tmp = new byte[data.length + leng];
        byte[] bloc = new byte[8];

        K = roundKeys.generateSubKeys(key);

        int count = 0;

        for (i = 0; i < data.length + leng; i++) {
            if (i > 0 && i % 8 == 0) {
                for (int r = 0; r < 16; r++){

                        bloc = cryptTransformation.encryptBloc(bloc, K[r]);

                    System.arraycopy(bloc, 0, tmp, i - 8, bloc.length);
                }

            }
            if (i < data.length)
                bloc[i % 8] = data[i];
            else{
                bloc[i % 8] = padding[count % 8];
                count++;
            }
        }
        if (bloc.length == 8){
            for (int r = 0; r < 16; r++){
                    bloc = cryptTransformation.encryptBloc(bloc, K[r]);

                System.arraycopy(bloc, 0, tmp, i - 8, bloc.length);
            }
        }
        return tmp;
    }


    public byte[] decrypt(byte[] data, byte[] key) {


        byte [][] K;


        int i;

        // 01
        // 02 02 00000010 00000010
        // 03 03 03 000000011  000000011 000000011

        byte[] tmp = new byte[data.length];
        byte[] bloc = new byte[8];

        K = roundKeys.generateSubKeys(key);
        for (i = 0; i < data.length; i++) {
            if (i > 0 && i % 8 == 0) {
                for (int r = 0; r < 16; r++){
                        bloc = cryptTransformation.encryptBloc(bloc, K[15-r]);
//                    }
                    System.arraycopy(bloc, 0, tmp, i - 8, bloc.length);
                }

            }
            if (i < data.length)
                bloc[i % 8] = data[i];

        }
        if (bloc.length == 8){
            for (int r = 0; r < 16; r++){
                    bloc = cryptTransformation.encryptBloc(bloc, K[15-r]);
                System.arraycopy(bloc, 0, tmp, i - 8, bloc.length);
            }
        }

        tmp = deletePadding(tmp);

        return tmp;
    }








    private static byte[] deletePadding(byte[] input) {
        int paddingLength = input[input.length-1];


        byte[] tmp = new byte[input.length - paddingLength];
        System.arraycopy(input, 0, tmp, 0, tmp.length);
        return tmp;
    }
}
