package cn.androidminds.commonapi.jwt;

import java.security.*;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;


public class RsaKeyUtil {
    public static ArrayList<byte[]> generateKeyPair(String password) {
        SecureRandom secureRandom = new SecureRandom(password.getBytes());
        KeyPairGenerator keyPairGenerator = null;
        try {
            keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        keyPairGenerator.initialize(1024, secureRandom);
        KeyPair keyPair = keyPairGenerator.genKeyPair();
        ArrayList<byte[]> list = new ArrayList<>();
        list.add(keyPair.getPrivate().getEncoded());
        list.add(keyPair.getPublic().getEncoded());
        return list;
    }

    public static PublicKey getPublicKey(byte[] publicKey) throws InvalidKeySpecException {
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKey);
        try {
            return KeyFactory.getInstance("RSA").generatePublic(spec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PrivateKey getPrivateKey(byte[] privateKey) throws InvalidKeySpecException {
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(privateKey);
        try {
            return KeyFactory.getInstance("RSA").generatePrivate(spec);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return null;
    }
}

