package com.jmcoin.model;

import javax.persistence.*;
import java.io.Serializable;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
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
    private PublicKey pubKey;

    public Transaction() {
        inputs = new ArrayList<>();
    }

    public void setPubKey(PublicKey pubKey) {
        this.pubKey = pubKey;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public PublicKey getPubKey() {
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
        //this Output can be null

		/*if(o == null) {
			throw new IllegalArgumentException("Transaction.addOutputBack: Parameter cannot be null");
		}*/
        this.outputBack = o;

    }


    public int getSize() {
        int size = 0;
        for (Input input : this.inputs)
            size += input.getSize();
        size += (this.outputBack == null ? 0 : this.outputBack.getSize());
        size += (this.outputOut == null ? 0 : this.outputOut.getSize());
        size += (this.hash == null ? 0 : this.hash.length);
        size += (this.signature == null ? 0 : this.signature.length);
        size += (this.pubKey == null ? 0 : this.pubKey.getEncoded().length);
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
                Arrays.equals(this.pubKey.getEncoded(), transaction.pubKey.getEncoded());
    }

    /* FIXME should not be done here
     * public boolean isValid() {
        int total = 0;
        for (Input i : inputs) {
            String unvf = JMProtocolImpl.sendRequest(NetConst.RELAY_NODE_LISTEN_PORT, NetConst.RELAY_DEBUG_HOST_NAME, NetConst.GIVE_ME_UNSPENT_OUTPUTS, null);
            Output[] unspentOutputs = IOFileHandler.getFromJsonString(unvf, Output[].class);
            //if i.output is not in unspent ouputs pool -> false
            //if i.output.address is not this.outputs[0].address -> false
            total += i.getAmount();
        }
        total += outputOut.getAmount();
        total += outputBack.getAmount();
        if (total != 0) return false;

        return false;
    }*/

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
        if (this.pubKey != null) buf.put(pubKey.getEncoded());
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
