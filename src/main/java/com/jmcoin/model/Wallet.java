
package com.jmcoin.model;
import com.jmcoin.crypto.AES;
import com.jmcoin.crypto.SignaturesVerification;
import com.jmcoin.util.BytesUtil;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
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
            getAddresses().add(SignaturesVerification.DeriveJMAddressFromPubKey(this.keys.get(privK)));
        }
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
    public void createTransaction(String fromAddress, String toAddress, double amountToSend, PrivateKey privKey, PublicKey pubKey) throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IOException, FileNotFoundException, SignatureException
    {
        //Recupérer la liste de transacction avec des outputs disponibles pour cette adresse TO DO from network
        ArrayList<Transaction> addressTransactions = new ArrayList<Transaction>();
        ArrayList<Input> addressInputs = new ArrayList<Input>();
        double totalOutputAmount = 0;
        
        for(int i = 0 ; i < addressTransactions.size(); i++)
        {
            if(addressTransactions.get(i).getOutputBack().getAddress().equals(fromAddress))
            {
                totalOutputAmount+= addressTransactions.get(i).getOutputBack().getAmount();
                addressInputs.add(new Input(fromAddress,addressTransactions.get(i).getOutputBack().getAmount(),addressTransactions.get(i).getHash()));
                
            }
            else
            {  
                if((addressTransactions.get(i).getOutputOut().getAddress().equals(fromAddress)))
                {
                    totalOutputAmount+= addressTransactions.get(i).getOutputOut().getAmount();
                    addressInputs.add(new Input(fromAddress,addressTransactions.get(i).getOutputOut().getAmount(),addressTransactions.get(i).getHash()));
                }
                else
                {
                    //bug aucune des output n'appartient à l'adresse utilisée
                }
            }
            
            if(amountToSend <= totalOutputAmount)
            {
                Output oOut = new Output(amountToSend, toAddress);
                Output oBack = new Output(totalOutputAmount-amountToSend, fromAddress);
                Transaction tr = new Transaction(addressInputs,oOut, oBack,pubKey);
                tr.setSignature(SignaturesVerification.signTransaction(tr, privKey));
                
                //hasher et set le hash dans la transaction
                MessageDigest digest = MessageDigest.getInstance("SHA-256");
                tr.setHash(digest.digest(tr.getBytes()));
                //ENVOYER LA TRANSACTION
            }
            else
            {
                // solde insufisant
            }
            
            
        }
        
        addTransaction(new Transaction());
    }
    
    public void addTransaction(Transaction transaction)
    {
        transactions.add(transaction);
    }
            
    // //TODO To do when we know how to fetch the chain
    // ------------------------------------------- Chain
    public double getAddressBalance(String address)
    {
       //Recupérer la liste de transacction avec des outputs disponibles pour cette adresse TO DO from network
        ArrayList<Transaction> addressTransactions = new ArrayList<Transaction>();
        ArrayList<Input> addressInputs = new ArrayList<Input>();
        double totalOutputAmount = 0;
        
        for(int i = 0 ; i < addressTransactions.size(); i++)
        {
            if(addressTransactions.get(i).getOutputBack().getAddress().equals(address))
            {
                totalOutputAmount+= addressTransactions.get(i).getOutputBack().getAmount();
            }
            else
            {  //Revérification qu'il y a bien une des deux outputs qui appartient à l'adresse, nécessaire ?
                if((addressTransactions.get(i).getOutputOut().getAddress().equals(address)))
                {
                    totalOutputAmount+= addressTransactions.get(i).getOutputOut().getAmount();
                }
                else
                {
                    //bug aucune des output n'appartient à l'adresse utilisée
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
    
    public Block getBlockByHash(String hash)
    {
        return new Block();
    }
    public List<Block> getFullBlockchain()
    {
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
}
 