package com.jmcoin.model;

import javax.persistence.*;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@NamedQueries({
        @NamedQuery(name = "Transaction.findAll", query = "SELECT t FROM Transaction t")
})
public class Transaction implements Serializable {


    private static final long serialVersionUID = -1113345289965914322L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Basic(optional = false)
    private Long id;
    @OneToMany(cascade = CascadeType.ALL)
    private List<Input> inputs;
    @OneToOne(cascade = CascadeType.ALL)
    private Output outputOut;
    @OneToOne(cascade = CascadeType.ALL)
    private Output outputBack;
    @Lob
    private byte[] hash;
    @Lob
    private byte[] signature;
    @Basic(optional = false)
    private byte[] pubKey;

    public Transaction() {
        inputs = new ArrayList<>();
    }

    public void setPubKey(byte[] pubKey) {
        this.pubKey = pubKey;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getPubKey() {
        return pubKey;
    }

    public byte[] getSignature() {
        return signature;
    }


    public byte[] getHash() {
        return hash;
    }

    public List<Input> getInputs() {
        return inputs;
    }

    public Output getOutputOut() {
        return outputOut;
    }

    public Output getOutputBack() {
        return outputBack;
    }

    public void addInput(Input i) {
        if (i == null) {
            throw new IllegalArgumentException("Transaction.addInput: Parameter cannot be null");
        }
        if (!this.inputs.contains(i)) {
            inputs.add(i);
        }
    }

    public void setOutputOut(Output o) {
        if (o == null) {
            throw new IllegalArgumentException("Transaction.addOunput: Parameter cannot be null");
        }
        this.outputOut = o;
    }

    public void setOutputBack(Output o) {
        this.outputBack = o;

    }


    public int getSize() {
        int size = 0;
        for (Input input : this.inputs)
            size += input.getSize();
        size += (this.outputBack == null ? 0 : this.outputBack.getSize());
        size += (this.outputOut == null ? 0 : this.outputOut.getSize());
        //size += (this.hash == null ? 0 : this.hash.length);
        size += (this.signature == null ? 0 : this.signature.length);
        size += (this.pubKey == null ? 0 : this.pubKey.length);
        return size;
    }

    public boolean equals(Transaction transaction) {
        if (transaction == null || transaction.inputs == null || transaction.outputOut == null || transaction.outputBack == null || this.inputs.size() != transaction.inputs.size())
            return false;
        for (int i = 0; i < this.inputs.size() && i < transaction.inputs.size(); i++) {
            if (!this.inputs.get(i).equals(transaction.inputs.get(i))) return false;
        }

        if (!this.outputOut.equals(transaction.outputOut)) return false;
        if (!this.outputBack.equals(transaction.outputBack)) return false;
        return Arrays.equals(this.hash, transaction.hash) &&
                Arrays.equals(this.signature, transaction.signature) &&
                Arrays.equals(this.pubKey, transaction.pubKey);
    }

    /**
     * @param withSign has to be false when this routine is called in order to compute the signature!
     * @return
     */
    public byte[] getBytes(boolean withSign) {
        ByteBuffer buf = ByteBuffer.allocate(getSize() - (withSign || this.signature == null ? 0 : this.signature.length));
        for (Input input : this.inputs) {
            buf.put(input.getBytes());
        }
        if (withSign && this.signature != null) buf.put(this.signature);
        if (this.pubKey != null) buf.put(pubKey);
        if (this.outputBack != null) buf.put(outputBack.getBytes());
        if (this.outputOut != null) buf.put(outputOut.getBytes());
        return buf.array();
    }

    /**
     * Computes the hash based on all properties but the {@link #hash}
     *
     * @return
     * @throws NoSuchAlgorithmException
     */
    public void computeHash() throws NoSuchAlgorithmException {
        MessageDigest dig = MessageDigest.getInstance("SHA-256");
        dig.update(getBytes(true));
        this.hash = dig.digest();
    }
}
