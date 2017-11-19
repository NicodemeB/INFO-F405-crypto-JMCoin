
package com.jmcoin.model;
import com.jmcoin.crypto.AES;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.encoders.Hex;
import org.omg.CORBA.PUBLIC_MEMBER;


public class Wallet {
    
    private String email;
    private String password;
    private KeyGenerator keyGen = new KeyGenerator(1024);
    private HashMap<PrivateKey,PublicKey> keys;
    public ArrayList<String> addresses;
    private List<Transaction> transactions;
    private double balance;
    private String sep = System.getProperty("file.separator");
    private String rep = System.getProperty("user.home") + sep + "Documents";
    
    private final String PRIV_KEY_FILE = rep+sep+"PrivateKeys";
    private final String PUBL_KEY_FILE = rep+sep+"PublicKeys";


    public Wallet(String email, String password) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException, AES.InvalidPasswordException, AES.InvalidAESStreamException, AES.StrongEncryptionNotAvailableException
    {
        File pk = new File (PUBL_KEY_FILE+sep);
        if (!pk.exists()){
            pk.mkdir();
        }
        File prk = new File (PRIV_KEY_FILE+sep);
        if (!prk.exists()){
            prk.mkdir();
        }
        this.email = email;
        this.password = password;
        this.keys = getWalletKeysFromFile(this.password);
        this.balance = getBalance(addresses);
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
        //ByteArrayInputStream inputPrivateKey = new ByteArrayInputStream(privateKey.getEncoded());
        //ByteArrayOutputStream encryptedPrivateKey = new ByteArrayOutputStream();
        
        //AES.encrypt(128, AESpw, inputPrivateKey ,outArray);

//        keyGen.writeToFile("/Users/Famille/Documents/PublicKeys/publicKey_"+new Date().getTime()+".txt", publicKey.getEncoded());
//        keyGen.writeToFile("/Users/Famille/Documents/PrivateKeys/privateKey_"+new Date().getTime()+".txt", privateKey.getEncoded());
        keyGen.writeToFile(PUBL_KEY_FILE+sep+"publicKey_"+new Date().getTime()+".txt", publicKey.getEncoded());
        keyGen.writeToFile(PRIV_KEY_FILE+sep+"privateKey_"+new Date().getTime()+".txt", privateKey.getEncoded());
        //keyGen.SaveKeyPair("/Users/Famille/Documents/", pair);
        keys.put(privateKey,publicKey);
        computeAddresses(this.keys);
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
    public HashMap<PrivateKey,PublicKey> decryptPrivateKey(String password, HashMap<String,String> keysFromFile)
    {
        return new HashMap<PrivateKey,PublicKey>();
    }
    public HashMap<PrivateKey,PublicKey> getWalletKeysFromFile(String password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, AES.InvalidPasswordException, AES.InvalidAESStreamException, AES.StrongEncryptionNotAvailableException 
    {
        KeyFactory kf = KeyFactory.getInstance("RSA");
        ArrayList<PrivateKey> privateKeyList = new ArrayList();
        ArrayList<PublicKey> publicKeyList = new ArrayList();

//        try (Stream<Path> paths = Files.walk(Paths.get("/Users/Famille/Documents/PrivateKeys"))) {

        try (Stream<Path> paths = Files.walk(Paths.get(PRIV_KEY_FILE))) {

            paths
            .filter(Files::isRegularFile)
            .forEach(filePath-> {
                try {
                    byte[] bytePrivKey = keyGen.getFileInBytes(filePath.toString());
                    PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(bytePrivKey));
                    privateKeyList.add(privateKey);
                } 
                catch (IOException ex) {
                    //throws new exception
                    Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeySpecException ex) {
                    Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }); 
        }

//        try (Stream<Path> paths = Files.walk(Paths.get("/Users/Famille/Documents/PublicKeys"))) {
        try (Stream<Path> paths = Files.walk(Paths.get(PUBL_KEY_FILE))) {

            paths
            .filter(Files::isRegularFile)
            .forEach(filePath-> {
                try {
                    byte[] bytePubKey = keyGen.getFileInBytes(filePath.toString());
                    PublicKey pubKey = kf.generatePublic(new X509EncodedKeySpec(bytePubKey));
                    publicKeyList.add(pubKey);
                } 
                catch (IOException ex) {
                    //throws new exception
                    Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
                } catch (InvalidKeySpecException ex) {
                    System.out.println("InvalidKeySpecException : file hidden in keys folder ");
                }
            });
        } 
        
        HashMap<PrivateKey,PublicKey> keyCouples = new HashMap<PrivateKey,PublicKey>();
        for(int i = 0; i< privateKeyList.size() && i < publicKeyList.size(); i++){
            keyCouples.put(privateKeyList.get(i), publicKeyList.get(i));
        }
        
        //ByteArrayInputStream encryptedPrivKey = new ByteArrayInputStream(privKey);
        //ByteArrayOutputStream decryptedPrivKey = new ByteArrayOutputStream();
        //AES.decrypt(password.toCharArray(), encryptedPrivKey, decryptedPrivKey);
        return keyCouples;
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
 