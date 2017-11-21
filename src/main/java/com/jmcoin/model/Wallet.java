
package com.jmcoin.model;
import com.jmcoin.crypto.AES;
import com.jmcoin.util.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.util.encoders.Hex;


public class Wallet {
    
    private String email;
    private KeyGenerator keyGen = new KeyGenerator(1024);
    private HashMap<PrivateKey,PublicKey> keys;
    public ArrayList<String> addresses;
    private List<Transaction> transactions;
    private double balance;
    
    private final String REP = System.getProperty("user.home");
    private final String SEP = System.getProperty("file.separator");
    private final String PRIV_KEYS = REP + SEP + "Documents"+SEP+"PrivateKeys";
    private final String PUB_KEYS = REP + SEP + "Documents"+SEP+"PublicKeys";
    

    public Wallet(String email, String password) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException, AES.InvalidPasswordException, AES.InvalidAESStreamException, AES.StrongEncryptionNotAvailableException
    {
    	File file = new File(PRIV_KEYS);
    	if(!file.exists() || !file.isDirectory()) file.mkdir();
    	file = new File(PUB_KEYS);
    	if(!file.exists() || !file.isDirectory()) file.mkdir();
        this.email = email;
        this.keys = getWalletKeysFromFile(password);
        this.balance = getBalance(addresses);
        this.addresses = new ArrayList<String>();
        
    }   
    // ------------------------------------------Keys
    public void createKeys(String password) throws IOException, AES.InvalidKeyLengthException, AES.StrongEncryptionNotAvailableException
    {
        keyGen.createKeys();
        PrivateKey privateKey = keyGen.getPrivateKey();
        PublicKey publicKey = keyGen.getPublicKey();
        //KeyPair pair = keyGen.getKeypair();

        char[] AESpw = password.toCharArray();
        ByteArrayInputStream inputPrivateKey = new ByteArrayInputStream(privateKey.getEncoded());
        ByteArrayOutputStream encryptedPrivateKey = new ByteArrayOutputStream();
        
        AES.encrypt(128, AESpw, inputPrivateKey , encryptedPrivateKey);

        keyGen.writeToFile(PUB_KEYS+SEP+"publicKey_"+System.currentTimeMillis()+".txt", publicKey.getEncoded());
        keyGen.writeToFile(PRIV_KEYS+SEP+"privateKey_"+System.currentTimeMillis()+".txt", encryptedPrivateKey.toByteArray());
        keys.put(privateKey,publicKey);
        computeAddresses(this.keys);
    }
    public void computeAddresses(HashMap<PrivateKey,PublicKey> keys) throws IOException
    {
        RIPEMD160Digest dgst = new RIPEMD160Digest();
        for(PrivateKey privK : this.keys.keySet()){
        	byte[] key = this.keys.get(privK).getEncoded();
            dgst.update(key, 0, key.length);
            byte[] bytes = new byte[dgst.getDigestSize()];
            dgst.doFinal(bytes, 0);
            getAddresses().add(new String(Hex.encode(bytes)));
        }
    }
    /*public void computeAddresses(HashMap<PrivateKey,PublicKey> keys) throws IOException
    {
        Iterator it = keys.entrySet().iterator();
        RIPEMD160Digest dgst;
        PublicKey pk;
        
        while (it.hasNext()) 
        {
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
    }*/
    public HashMap<PrivateKey,PublicKey> decryptPrivateKey(String password, HashMap<String,String> keysFromFile)
    {
        return new HashMap<PrivateKey,PublicKey>();
    }
    public HashMap<PrivateKey,PublicKey> getWalletKeysFromFile(String password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, AES.InvalidPasswordException, AES.InvalidAESStreamException, AES.StrongEncryptionNotAvailableException 
    {
        KeyFactory kf = KeyFactory.getInstance("DSA");
        ArrayList<PrivateKey> privateKeyList = new ArrayList<>();
        ArrayList<PublicKey> publicKeyList = new ArrayList<>();
           
        try (Stream<Path> paths = Files.walk(Paths.get(PRIV_KEYS))) {

            paths
            .filter(Files::isRegularFile)
            .forEach(filePath-> {
                try {
                    char[] AESpw = password.toCharArray();
                    String name = filePath.getFileName().toString();
                    if (name.endsWith("txt")) {
                        byte[] bytePrivKey = keyGen.getFileInBytes(filePath.toString());
                        ByteArrayOutputStream decryptedPrivateKey = new ByteArrayOutputStream();
                        AES.decrypt(AESpw, new ByteArrayInputStream(bytePrivKey), decryptedPrivateKey);
                        PrivateKey privateKey = kf.generatePrivate(new PKCS8EncodedKeySpec(decryptedPrivateKey.toByteArray()));
                        privateKeyList.add(privateKey);
                    }   
                } 
                catch (IOException | InvalidKeySpecException | AES.InvalidPasswordException | AES.StrongEncryptionNotAvailableException | AES.InvalidAESStreamException ex) {
                    Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
                }
            }); 
        }
        
        try (Stream<Path> paths = Files.walk(Paths.get(PUB_KEYS))) {

            paths
            .filter(Files::isRegularFile)
            .forEach(filePath-> {
                
                try {
                    String name = filePath.getFileName().toString();
                    if (name.endsWith("txt")) {
                        byte[] bytePubKey = keyGen.getFileInBytes(filePath.toString());
                        PublicKey pubKey = kf.generatePublic(new X509EncodedKeySpec(bytePubKey));
                        publicKeyList.add(pubKey);
                    }
                    
                } 
                catch (IOException ex) 
                {
                    Logger.getLogger(Wallet.class.getName()).log(Level.SEVERE, null, ex);
                } 
                catch (InvalidKeySpecException ex) {
                    System.out.println("InvalidKeySpecException : Fichier caché dans le dossier des clés");
                }
            });
        } 
        
        HashMap<PrivateKey,PublicKey> keyCouples = new HashMap<>();
        for(int i = 0; i < privateKeyList.size() && i < publicKeyList.size(); i++)
        {
            keyCouples.put(privateKeyList.get(i), publicKeyList.get(i));
        }

        return keyCouples;
    }
    //--------------------------------------------Transactions
    public void createTransaction()
    {
        addTransaction(new Transaction());
    }
    
    public void addTransaction(Transaction transaction)
    {
        transactions.add(transaction);
    }
    
    public byte[] signTransaction(Transaction tr, PrivateKey privKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, FileNotFoundException, IOException, SignatureException
    {
        Signature dsa = Signature.getInstance("SHA1withDSA", "SUN"); 
        dsa.initSign(privKey);
        byte[] bytesTr = BytesUtil.toByteArray(tr);
        BufferedInputStream bufIn = new BufferedInputStream(new ByteArrayInputStream(bytesTr));
        byte[] buffer = new byte[1024];
        int len;
        while ((len = bufIn.read(buffer)) >= 0) {
            dsa.update(buffer, 0, len);
        }
        bufIn.close();
        return dsa.sign();
    }
    
    public boolean verifyTransaction(byte[] signature, Transaction tr, PublicKey pubKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, SignatureException
    {
        boolean verifies = false;
        if(/*pas de signature || */ tr == null || pubKey == null)
        {
            //Impossible de vérifier la transaction il manque des éléments.
        }
        else
        {
            Signature sig = Signature.getInstance("SHA1withDSA", "SUN");
            sig.initVerify(pubKey);
            byte[] bytesTr = BytesUtil.toByteArray(tr);
            BufferedInputStream bufIn = new BufferedInputStream(new ByteArrayInputStream(bytesTr));
            byte[] buffer = new byte[1024];
            int len;
            while ((len = bufIn.read(buffer)) >= 0) {
                sig.update(buffer, 0, len);
            };
            bufIn.close();
            verifies = sig.verify(signature);
        } 
        
        return verifies; 
    }
            
    // //TODO To do when we know how to fetch the chain
    // ------------------------------------------- Chain
    public double getAddressBalance(String address)
    {
       return 0;
    }
    
    public double getBalance(ArrayList<String> adresses)
    {
       return 0;
    }
    
    public Block getBlockByHash(String hash)
    {
        //récuperer le bloc de la chaine
        return new Block();
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
    
    public ArrayList<String> getAddresses() {
        return addresses;
    }
    
    public void setKeys(HashMap<PrivateKey,PublicKey> keys) {
        this.keys = keys;
    }
    //partie réseau à faire
}
 