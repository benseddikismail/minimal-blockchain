public class TransactionInput {
    public String transactionOuputID;
    public TransactionOutput UTXO;

    public TransactionInput(String transactionOuputID) {
        this.transactionOuputID = transactionOuputID;
    }

}
