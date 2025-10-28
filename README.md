# 🧱 Simple Blockchain in Java

A **minimal blockchain implementation in Java**, built to demonstrate the core concepts of cryptocurrency systems, including block structure, hashing, proof-of-work mining, digital signatures, transactions, and UTXO-based balances.
As an *educational prototype, it focuses on clarity and conceptual accuracy to illustrate how decentralized ledgers maintain security, integrity, and immutability through cryptography and consensus mechanisms.

---

## 🚀 Features

- 🧩 **Block structure** with hash linking
- 🔐 **Digital signatures** using ECDSA to verify transaction authenticity
- ⚒️ **Proof-of-Work** mining with adjustable difficulty
- 💸 **Transactions and UTXOs** (unspent transaction outputs)
- 🌳 **Merkle root calculation** for block transaction integrity
- 🪙 **Wallet system** with public/private key pairs
- 🧾 **Balance tracking** and fund transfers between wallets
- 🧠 **Blockchain validation** to detect tampering

---

## 🧠 How It Works

### 1. Wallets
Each wallet generates an **ECDSA public/private key pair**.
- The **public key** acts as the wallet address.
- The **private key** is used to sign transactions.

### 2. Transactions
Transactions consume **UTXOs** (unspent outputs) from previous transactions and create new outputs.
They ensure:
- The sender has enough balance.
- The transaction is signed by the rightful owner.

### 3. Mining
Blocks are mined using a **proof-of-work** mechanism:  
The miner finds a nonce that produces a hash beginning with a number of leading zeros equal to the network difficulty.  
This makes tampering computationally expensive.

### 4. Merkle Root
All transaction hashes in a block are recursively combined and hashed into a single **Merkle root**, which provides an integrity check for the block’s transactions.

### 5. Validation
Each block references the previous block’s hash, forming an immutable chain.  
If any block’s data changes, all following hashes become invalid, revealing tampering.



