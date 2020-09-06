package rmc.web3j;


import org.bouncycastle.util.encoders.Hex;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.HashMap;

public class Address {


    static HashMap<String,String> hashMap = new HashMap<>();

    public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {


        for (int i = 0; i < 10 ; i++) {
            ECKeyPair ecKeyPair = Keys.createEcKeyPair();
            BigInteger privateKeyInDec = ecKeyPair.getPrivateKey();
            Credentials keys = Credentials.create(ECKeyPair.create(privateKeyInDec));
            String privateKey = Hex.toHexString(keys.getEcKeyPair().getPrivateKey().toByteArray());
            String address = keys.getAddress().replace("0x","RMC");
            hashMap.put(address,privateKey);
        }


        for(String key : hashMap.keySet()){
            System.out.println("address:"+key+"  privateKey:"+hashMap.get(key));
        }

    }


}
