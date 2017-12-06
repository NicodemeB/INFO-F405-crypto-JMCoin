
package com.jmcoin.model;
import com.jmcoin.crypto.AES;
import com.jmcoin.crypto.SignaturesVerification;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class Wallet {
    
    private KeyGenerator keyGen = new KeyGenerator(1024);
    private HashMap<PrivateKey,PublicKey> keys;
    private ArrayList<String> addresses;
    private double balance;
    
    private final String REP = System.getProperty("user.home");
    private final String SEP = System.getProperty("file.separator");
    private final String PRIV_KEYS = REP + SEP + "Documents"+SEP+"PrivateKeys";
    private final String PUB_KEYS = REP + SEP + "Documents"+SEP+"PublicKeys";
    
    public Wallet(String password) throws NoSuchAlgorithmException, NoSuchProviderException, IOException, InvalidKeySpecException, AES.InvalidPasswordException, AES.InvalidAESStreamException, AES.StrongEncryptionNotAvailableException{
    	this.addresses = new ArrayList<String>();
    	File file = new File(PRIV_KEYS);
    	if(!file.exists() || !file.isDirectory()) file.mkdir();
    	file = new File(PUB_KEYS);
    	if(!file.exists() || !file.isDirectory()) file.mkdir();
        this.keys = getWalletKeysFromFile(password);
        this.balance = getBalance(addresses);
    }   
    
    // ------------------------------------------Keys
    public void createKeys(String password) throws IOException, AES.InvalidKeyLengthException, AES.StrongEncryptionNotAvailableException{
        keyGen.createKeys();
        PrivateKey privateKey = keyGen.getPrivateKey();
        PublicKey publicKey = keyGen.getPublicKey();
        char[] AESpw = password.toCharArray();
        ByteArrayInputStream inputPrivateKey = new ByteArrayInputStream(privateKey.getEncoded());
        ByteArrayOutputStream encryptedPrivateKey = new ByteArrayOutputStream();
        
        AES.encrypt(128, AESpw, inputPrivateKey , encryptedPrivateKey);

        keyGen.writeToFile(PUB_KEYS+SEP+"publicKey_"+System.currentTimeMillis()+".txt", publicKey.getEncoded());
        keyGen.writeToFile(PRIV_KEYS+SEP+"privateKey_"+System.currentTimeMillis()+".txt", encryptedPrivateKey.toByteArray());
        keys.put(privateKey,publicKey);
        computeAddresses(this.keys);
    }
    
    public void computeAddresses(HashMap<PrivateKey,PublicKey> keys) throws IOException{
        for(PrivateKey privK : this.keys.keySet()){
            this.addresses.add(SignaturesVerification.DeriveJMAddressFromPubKey(this.keys.get(privK).getEncoded()));
        }
    }
    
    public HashMap<PrivateKey,PublicKey> getWalletKeysFromFile(String password) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, AES.InvalidPasswordException, AES.InvalidAESStreamException, AES.StrongEncryptionNotAvailableException {
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
                catch (IOException ex) {
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
    
    // ------------------------------------------- Chain
    public double getAddressBalance(String address)
    {
       //TODO Recupérer la liste de transacction avec des outputs disponibles pour cette adresse TO DO from network
        ArrayList<Transaction> addressTransactions = new ArrayList<Transaction>();
        double totalOutputAmount = 0;
        
        for(int i = 0 ; i < addressTransactions.size(); i++)
        {
            if(addressTransactions.get(i).getOutputBack().getAddress().equals(address))
            {
                totalOutputAmount+= addressTransactions.get(i).getOutputBack().getAmount();
            }
            else
            {   //Revérification qu'il y a bien une des deux outputs qui appartient à l'adresse, nécessaire ?
                if((addressTransactions.get(i).getOutputOut().getAddress().equals(address)))
                {
                    totalOutputAmount+= addressTransactions.get(i).getOutputOut().getAmount();
                }
                else
                {
                    System.out.println("Wallet : No output belonging to this address");
                }
            }
        }
        return totalOutputAmount;
    }
    
    public double getBalance(ArrayList<String> adresses)
    {
        double totalAmount = 0; 
        for(String address : addresses)
        {
           totalAmount += getAddressBalance(address);
        }
        return totalAmount;
    }
    
    public HashMap<PrivateKey,PublicKey> getKeys() {
       return keys;
    }
}
 