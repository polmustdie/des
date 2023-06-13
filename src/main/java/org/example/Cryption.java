package org.example;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class Cryption {
    @NonNull
    ISymmetricalCrypt iSymmetricalCrypt;
    @NonNull
    Modes mode;
    @NonNull
    byte[] IV;
    IModes iMode;
    Cryption(ISymmetricalCrypt iSymmetricalCrypt,Modes mode, byte[] IV){
        this.iSymmetricalCrypt = iSymmetricalCrypt;
        this.mode= mode;
        this.IV = IV;
        findMode();
    }

    void findMode(){
        if (mode == Modes.CBC) {
            this.iMode = new CBC(iSymmetricalCrypt, IV);
            this.iMode.setPreviousBlock(IV);
        }
    }
    byte[] encrypt(byte[] data){
        return iMode.encrypt(data);

    }
    byte[] decrypt(byte[] data) {
        return iMode.decrypt(data);
    }




}
