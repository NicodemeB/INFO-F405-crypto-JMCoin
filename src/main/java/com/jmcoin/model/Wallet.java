
package com.jmcoin.model;
import com.jmcoin.crypto.AES;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.encoders.Hex;


public class Wallet {
    
    private String email;
    private String password;
    private KeyGenerator keyGen;
    private HashMap<PrivateKey,PublicKey> keys;
    public ArrayList<String> addresses;
    private List<Transaction> transactions;
    private double balance;

    public Wallet(String email, String password) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException, AES.InvalidPasswordException, AES.InvalidAESStreamException, AES.StrongEncryptionNotAvailableException
    {
        this.email = email;
        this.password = password;
        HashMap<String,String> keysFromFile = getWalletKeysFromFile(this.password);
        this.keys = decryptKeys(getPassword(),keysFromFile);
        this.balance = getBalance(addresses);
        this.keyGen = new KeyGenerator(1024);
        this.addresses = new ArrayList<String>();
    }   
    // ------------------------------------------Keys
    public void createKeys(String password) throws IOException, AES.InvalidKeyLengthException, AES.StrongEncryptionNotAvailableException
    {
        keyGen.createKeys();
        PrivateKey privateKey = keyGen.getPrivateKey();
        PublicKey publicKey = keyGen.getPublicKey();
        KeyPair pair = keyGen.getKeypair();
        byte[] encodedPublicKey = publicKey.getEncoded();
        
        //String b64PublicKey = Base64.getEncoder().encodeToString(encodedPublicKey);
        //System.out.println("cle privée " +b64PublicKey);
        
        char[] AESpw = password.toCharArray();
        ByteArrayInputStream inputPrivateKey = new ByteArrayInputStream(privateKey.getEncoded());
        
        ByteArrayOutputStream outArray = new ByteArrayOutputStream();
        
        AES.encrypt(128, AESpw, inputPrivateKey ,outArray);
        
        keyGen.writeToFile("/Users/Famille/Documents/publicKeys.txt", publicKey.getEncoded());
        keyGen.writeToFile("/Users/Famille/Documents/privateKeys.txt", outArray.toByteArray());
                //keyGen.SaveKeyPair("/Users/Famille/Documents/", pair);
        
        keys.put(privateKey,publicKey);

    }
    public void computeAddresses(HashMap<PrivateKey,PublicKey> keys) throws IOException
    {
        Iterator it = keys.entrySet().iterator();
        RIPEMD160Digest dgst;
        PublicKey pk;
        
        while (it.hasNext()) {
            
            Map.Entry pair = (Map.Entry)it.next();
            pk = (PublicKey)pair.getValue();
            byte[] key = pk.getEncoded();
            dgst = new RIPEMD160Digest();
            dgst.update(key, 0, key.length);
            byte[] bytes = new byte[dgst.getDigestSize()];
            dgst.doFinal(bytes, 0);
            String hashedAddress = new String(Hex.encode(bytes));
            getAddresses().add(hashedAddress);
        }  
    }
    public HashMap<PrivateKey,PublicKey> decryptKeys(String password, HashMap<String,String> keysFromFile)
    {
        return new HashMap<PrivateKey,PublicKey>();
    }

    public HashMap<String,String> getWalletKeysFromFile(String password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, AES.InvalidPasswordException, AES.InvalidAESStreamException, AES.StrongEncryptionNotAvailableException 
    {
        //keyGen.LoadKeyPair("/Users/Famille/Documents/", "RSA");
        byte[] privKey = new byte[20]; // key retrieved from file
        ByteArrayInputStream encryptedPrivKey = new ByteArrayInputStream(privKey);
        ByteArrayOutputStream decryptedPrivKey = new ByteArrayOutputStream();
        AES.decrypt(password.toCharArray(), encryptedPrivKey, decryptedPrivKey);
        return new HashMap<String,String>();
    }
    //--------------------------------------------Transactions
    public void createTransaction()
    {
        Transaction t = new Transaction();
        addTransaction(t);
    }
    public void addTransaction(Transaction transaction)
    {
        transactions.add(transaction);
    }
    // ------------------------------------------- Chain
    public double getAddressBalance(String address)
    {
       double balance = 0;
       return balance;
    }
    public double getBalance(ArrayList<String> adresses)
    {
       double balance = 0;
       return balance;
    }
    public Block getBlockByHash(String hash)
    {
        //recuperer le bloc de la chaine
        Block block = new Block();
        return block;
    }
    public List<Block> getFullBlockchain()
    {
        //demande au relay la liste de block
        return new ArrayList<Block>();
    }
    public HashMap<PrivateKey,PublicKey> getKeys() 
    {
       return keys;
    }
    public String getEmail() {
        return email;
    }
    public String getPassword() {
        return password;
    }
    public ArrayList<String> getAddresses() {
        return addresses;
    }
    public void setKeys(HashMap<PrivateKey,PublicKey> keys) {
        this.keys = keys;
    }
    
    //partie réseau à faire
}
 