import java.security.Security;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.GsonBuilder;

public class BasicBlockChain {

    public static ArrayList<Block> blockchain = new ArrayList<Block>();
    public static HashMap<String, TransactionOutput> UTXOs = new HashMap<String, TransactionOutput>();
    public static int difficulty = 5;
    public static float minimumTransaction = 0.1f;
    public static Wallet walletA;
    public static Wallet walletB;
    public static Transaction genesisTransaction;


    public static void main(String[] args) {

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

        //create new wallets

        walletA = new Wallet();
        walletB = new Wallet();
        Wallet coinbase = new Wallet();

        genesisTransaction = new Transaction(coinbase.publicKey, walletA.publicKey, 100f, null);
        genesisTransaction.generateSignature(coinbase.privateKey); // sign the coinbase trx manually
        genesisTransaction.transactionID = "0"; //set trx ID manually
        genesisTransaction.outputs.add(new TransactionOutput(genesisTransaction.receiver, genesisTransaction.value, genesisTransaction.transactionID)); //add trx output manually
        UTXOs.put(genesisTransaction.outputs.get(0).ID, genesisTransaction.outputs.get(0));

        System.out.println("Creating and mining the genesis block...");
        Block genesis = new Block("0");
        genesis.addTransaction(genesisTransaction);
        addBlock(genesis);

        // testing
        Block block1 = new Block(genesis.hash);
        System.out.println("\nWallet A's balance = " + walletA.getBalance());
        System.out.println("\nWallet A attempting to send funds (40) to wallet B...");
        block1.addTransaction(walletA.sendFunds(walletB.publicKey, 40f));
        addBlock(block1);
        System.out.println("\nWallet A's balance = " + walletA.getBalance());
        System.out.println("\nWallet B's balance = " + walletB.getBalance());


        Block block2 = new Block(block1.hash);
        System.out.println("\nWallet A attempting to send more funds (1000) than it has...");
        block2.addTransaction(walletA.sendFunds(walletB.publicKey, 1000f));
        addBlock(block2);
        System.out.println("\nWallet A's balance = " + walletA.getBalance());
        System.out.println("\nWallet B's balance = " + walletB.getBalance());

        Block block3 = new Block(genesis.hash);
        System.out.println("\nWallet B attempting to send funds (20) to Wallet A...");
        block3.addTransaction(walletB.sendFunds(walletA.publicKey, 20f));
        System.out.println("\nWallet A's balance = " + walletA.getBalance());
        System.out.println("\nWallet B's balance = " + walletB.getBalance());


        isChainValid();

    }

    public static Boolean isChainValid() {
        Block currentBlock;
        Block previousBlock;
        String hashTarget = new String(new char[difficulty]).replace('\0','0');
        HashMap<String, TransactionOutput> tempUTXOs = new HashMap<String, TransactionOutput>();
        tempUTXOs.put(genesisTransaction.outputs.get(0).ID, genesisTransaction.outputs.get(0));

        for (int i = 1; i < blockchain.size(); i++) {
            currentBlock = blockchain.get(i);
            previousBlock = blockchain.get(i-1);

            if (!currentBlock.hash.equals(currentBlock.calculateHash())) {
                System.out.println("Current hashes not equal");
                return false;
            }

            if (!previousBlock.hash.equals(currentBlock.previousHash)) {
                System.out.println("Previous hashes not equal");
                return false;
            }

            if (!currentBlock.hash.substring(0, difficulty).equals(hashTarget)) {
                System.out.println("This block hasn't been mined");
                return false;
            }

            //loop through blockchains transactions
            TransactionOutput tempOutput;
            for(int t = 0; t < currentBlock.transactions.size(); t++) {
                Transaction currentTransaction = currentBlock.transactions.get(t);
                if(!currentTransaction.verifySignature()) {
                    System.out.println("Signature on transaction ("+t+") is invalid");
                    return false;
                }
                if(currentTransaction.getInputsValue() != currentTransaction.getOutputsValue()) {
                    System.out.println("Inputs and Outputs of transaction ("+t+") not equal");
                    return false;
                }
                for(TransactionInput input : currentTransaction.inputs) {
                    tempOutput = tempUTXOs.get(input.transactionOuputID);
                    if(tempOutput == null) {
                        System.out.println("Referenced input of transaction ("+t+") is missing");
                        return false;
                    }
                    if(input.UTXO.value != tempOutput.value) {
                        System.out.println("Referenced input of transaction ("+t+") value is invalid");
                        return false;
                    }
                    tempUTXOs.remove(input.transactionOuputID);
                }
                for(TransactionOutput output : currentTransaction.outputs) {
                    tempUTXOs.put(output.ID, output);
                }
                if(currentTransaction.outputs.get(0).receiver != currentTransaction.receiver) {
                    System.out.println("Transaction ("+t+") output recipient is not who it should be");
                    return false;
                }
                if(currentTransaction.outputs.get(1).receiver != currentTransaction.sender) {
                    System.out.println("Transaction ("+t+") output 'change' is not the sender");
                    return false;
                }
            }
        }
        System.out.println("Blockchain is valid");
        return true;
    }

    public static void addBlock(Block newBlock) {
        newBlock.mineBlock(difficulty);
        blockchain.add(newBlock);
    }
}