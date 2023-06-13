package org.example;

import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Data
@Setter
public class CBC implements  IModes{
    @NonNull
    ISymmetricalCrypt iSymmetricalCrypt;
    @NonNull
    byte[] IV;
    byte[] previousBlock;
    @Override
    public void reset() {
        previousBlock = IV;
    }
    CBC(ISymmetricalCrypt iSymmetricalCrypt, byte[] IV){
        this.iSymmetricalCrypt = iSymmetricalCrypt;
        this.IV = IV;
    }

    @Override
    public void setPreviousBlock(byte[] IV){
        this.previousBlock = IV;
    }


    @Override
    public byte[] encrypt(byte[] data) {
        byte[] prevBlockNew = new byte[previousBlock.length];
        System.arraycopy(previousBlock, 0, prevBlockNew, 0, previousBlock.length);
        try {
            for (int i = 0; i < data.length; i += 8)
            {
                byte[] block = IModes.getArray64(data, i);
                block = IModes.XORByteArray(block, previousBlock);
                previousBlock = iSymmetricalCrypt.encrypt(block);
                System.arraycopy(previousBlock, 0, data, i, 8);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        IV = prevBlockNew;
        return data;
    }

    @Override
    public byte[] decrypt(byte[] data) {
        reset();
        byte[] blockSave = new byte[8];
        try {
            for (int i = 0; i < data.length; i += 8)
            {
                byte[] block = IModes.getArray64(data, i);
                System.arraycopy(block, 0, blockSave, 0, block.length);
                var decrypt = iSymmetricalCrypt.decrypt(block);
                System.arraycopy(IModes.XORByteArray(previousBlock, decrypt), 0, data, i, 8);
                System.arraycopy(blockSave, 0, previousBlock, 0, blockSave.length);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return data;
    }
}
