import java.security.PublicKey;
import java.util.function.BinaryOperator;

public class TransactionOutput {
    public String ID;
    public PublicKey receiver; //the new owner of the coins
    public float value; //amount of coins they own
    public String parentTransactionID; //id of the transaction this output was created in

    public TransactionOutput(PublicKey receiver, float value, String parentTransactionID) {
        this.receiver = receiver;
        this.value = value;
        this.parentTransactionID = parentTransactionID;
        this.ID = StringUtil.applySHA256(StringUtil.getStringFromKey(receiver)+Float.toString(value)+parentTransactionID);
    }

    //check if coin belongs to you
    public boolean isMine(PublicKey publicKey) {
        return receiver == publicKey;
    }
}
