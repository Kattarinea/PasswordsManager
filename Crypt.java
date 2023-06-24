package com.example.passwordsmanager;

import android.util.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Crypt {
    SecretKeySpec secretKeySpec;
    static byte[] ivByte=new byte[8];
    static byte[] ivByteOld=new byte[8];
    public Crypt(String password) {

        byte[] keyByte = password.getBytes();
        secretKeySpec = new SecretKeySpec(keyByte, "DES");

    }
    public String EncryptPass(String text) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        Cipher c = Cipher.getInstance("DES");
        c.init(Cipher.ENCRYPT_MODE, secretKeySpec);
        byte[] textBytes = c.doFinal(text.getBytes());
        String cipherText = Base64.encodeToString(textBytes,Base64.DEFAULT);
        return cipherText;
    }

    public String DecryptPass(String cipherText) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("DES");
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
        byte[] textBytes = cipher.doFinal(Base64.decode(cipherText,Base64.DEFAULT));
        String text = new String(textBytes);
        return text;
    }


    public boolean EncryptFile(File file_in, File file_out)
    {
        boolean res=false;
        FileInputStream inS;
        CipherOutputStream outS;
        try {
            Cipher c = Cipher.getInstance("DES/CFB/PKCS5Padding");
            c.init(Cipher.ENCRYPT_MODE,secretKeySpec,new IvParameterSpec(ivByte));
            inS = new FileInputStream(file_in);
            outS = new CipherOutputStream(new FileOutputStream(file_out),c);
            if(write(inS,outS))
                res = true;
            inS.close();
            outS.close();
        }
        catch (Exception e)
        {
            return res;
        }
        return res;
    }

    public boolean DecryptFile(File file_in, File file_out)
    {
        boolean res=false;
        FileOutputStream outS;
        CipherInputStream inS;
        try {
            Cipher c = Cipher.getInstance("DES/CFB/PKCS5Padding");
            c.init(Cipher.DECRYPT_MODE,secretKeySpec, new IvParameterSpec(ivByte));
            outS = new FileOutputStream(file_out);
            inS = new CipherInputStream(new FileInputStream(file_in),c);
            if(write(inS,outS))
                res = true;
            inS.close();
            outS.close();
        }
        catch (Exception e)
        {
            return res;
        }
        return res;
    }


    private boolean write(InputStream inS, OutputStream outS) {
        int i=0;
        byte[] buf = new byte[1024];
        try
        {
            while ((i=inS.read(buf))!=-1)
            {
                outS.write(buf,0,i);
            }
        }
        catch (Exception e){
            return false;
        }
        return true;
    }
}
