package core;

import core.Change;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Transaction {
    public final Change[] changes;
    public String hash;
    public String nextHash;

    public Transaction(Change[] changes){
        this.changes = changes;
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(
                    Integer.toString(hashCode()).getBytes(StandardCharsets.UTF_8));
            this.hash = bytesToHex(encodedhash);
        }catch(NoSuchAlgorithmException e){
            // surely this won't happen
            this.hash = null;
        }
    }

    // copied from https://www.baeldung.com/sha-256-hashing-java
    private static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for(byte b : hash){
            String hex = Integer.toHexString(0xff & b);
            if(hex.length() == 1){
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
