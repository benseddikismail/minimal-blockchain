import java.security.*;
import java.util.ArrayList;
public class Transaction {
    public String transactionID;
    public PublicKey sender;
    public PublicKey receiver;
    public float value;
    public byte[] signature;

    public ArrayList<TransactionInput> inputs = new ArrayList<TransactionInput>();
    public ArrayList<TransactionOutput> outputs = new ArrayList<TransactionOutput>();

    public static int sequence = 0;

    public Transaction(PublicKey from, PublicKey to, float value, ArrayList<TransactionInput> inputs) {
        this.sender = from;
        this.receiver = to;
        this.value = value;
        this.inputs = inputs;
    }

    private String calculateHash() {
        sequence++;
        return StringUtil.applySHA256(
            StringUtil.getStringFromKey(sender) +
                    StringUtil.getStringFromKey(receiver) +
                    Float.toString(value) + sequence
        );
    }

    public void generateSignature(PrivateKey privateKey) {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(receiver) + Float.toString(value);
        signature = StringUtil.applyECDSASig(privateKey, data);
    }

    public boolean verifySignature() {
        String data = StringUtil.getStringFromKey(sender) + StringUtil.getStringFromKey(receiver) + Float.toString(value);
        return StringUtil.verifyECDSASig(sender, data, signature);
    }

    public boolean processTransaction() {
        if(!verifySignature()) {
            System.out.println("Transaction signature failed to verify");
            return false;
        }

        //gather transaction inputs, make sure they're unspent
        for(TransactionInput i : inputs) {
            i.UTXO = BasicBlockChain.UTXOs.get(i.transactionOuputID);
        }

        //check if the transaction is valid
        if(getInputsValue() < BasicBlockChain.minimumTransaction)  {
            System.out.println("Transaction inputs too small: " + getInputsValue());
            return false;
        }

        //generate transaction outputs
        float leftOver = getInputsValue() - value;
        transactionID = calculateHash();
        outputs.add(new TransactionOutput(this.receiver, value, transactionID)); //send value to receiver
        outputs.add(new TransactionOutput(this.sender, leftOver, transactionID));

        //add outputs to unspent list
        for(TransactionOutput o : outputs) {
            BasicBlockChain.UTXOs.put(o.ID, o);
        }

        //remove transaction inputs from UTXO list as unspent
        for(TransactionInput i : inputs){
            if(i.UTXO == null) continue;
            BasicBlockChain.UTXOs.remove(i.UTXO.ID);
        }
        return true;
    }

    //returns sum of inputs (UTXOs) values
    public float getInputsValue() {
        float total = 0;
        for(TransactionInput i : inputs) {
            if(i.UTXO == null) continue; //skip trx that can't be found
            total += i.UTXO.value;
        }
        return total;
    }

    //returns sum of outputs
    public float getOutputsValue(){
        float total = 0;
        for(TransactionOutput o : outputs) {
            total += o.value;
        }
        return total;
    }

}
