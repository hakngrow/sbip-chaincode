/*
 * SPDX-License-Identifier: Apache-2.0
 */
package org.hyperledger.fabric.samples.fabcoin;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;

import com.owlike.genson.Genson;

/**
 * Java implementation of the CoinLedger Contract
 */
@Contract(
        name = "FabCoin",
        info = @Info(
                title = "FabCoin contract",
                description = "The FabCoin contract",
                version = "0.0.1-SNAPSHOT",
                license = @License(
                        name = "Apache 2.0 License",
                        url = "http://www.apache.org/licenses/LICENSE-2.0.html"),
                contact = @Contact(
                        email = "hakngrow@gmail.com",
                        name = "Howie",
                        url = "https://hyperledger.example.com")))
@Default
public final class FabCoin implements ContractInterface {

    private final Genson genson = new Genson();

    private enum FabCoinErrors {
        ACCOUNT_NOT_FOUND,
        INSUFFICIENT_BALANCE
    }

    private static final int ACC_SERRIAL_START_INDEX = 3;

    /**
     * Creates some initial accounts
     *
     * @param ctx the transaction context
     */
    @Transaction()
    public void initLedger(final Context ctx) {

        ChaincodeStub stub = ctx.getStub();

        String[] accountData = {
            "{\"balance\": 1000,\"publicKey\": \"dummy\",\"timestamp\":\"1508484583259\"}",
            "{\"balance\": 3000,\"publicKey\": \"dummy\",\"timestamp\":\"1508484583259\"}"
        };

        for (int i = 0; i < accountData.length; i++) {
            String key = String.format("ACC%d", i + 1);

            Account account = genson.deserialize(accountData[i], Account.class);
            String accountState = genson.serialize(account);
            stub.putStringState(key, accountState);
        }
    }

    /**
     * Creates a new account.
     *
     * @param ctx the transaction context
     * @param accountId the account Id
     * @param balance the initial balance
     * @param publicKey the public key
     * @param timestamp the timestamp
     * @return the created account
     */
    @Transaction()
    public Account createAccount(
        final Context ctx,
        final String accountId,
        final double balance,
        final String publicKey,
        final long timestamp) {
        ChaincodeStub stub = ctx.getStub();

        Account account = new Account(balance, publicKey, timestamp);
        String accountState = genson.serialize(account);
        stub.putStringState(accountId, accountState);

        return account;
    }

    /**
     * Transfer coins between 2 accounts
     *
     * @param ctx the transaction context
     * @param fromAccountId the from account Id
     * @param toAccountId the to account Id
     * @param amount the amount
     * @return the updated from and to accounts
     */
    @Transaction()
    public String transfer(final Context ctx, final String fromAccountId, final String toAccountId,
        final double amount) {
        ChaincodeStub stub = ctx.getStub();

        String response = "ok";

        String accountState = stub.getStringState(fromAccountId);

        if (accountState.isEmpty()) {
            String errorMessage = String.format("Account %s does not exist", fromAccountId);
            response = "error:" + errorMessage;
            throw new ChaincodeException(errorMessage, FabCoinErrors.ACCOUNT_NOT_FOUND.toString());
        }

        Account fromAccount = genson.deserialize(accountState, Account.class);

        if (fromAccount.getBalance() < amount) {
            String errorMessage = String.format("Account %s does not have sufficient balance", fromAccountId);
            response = "error:" + errorMessage;
            throw new ChaincodeException(errorMessage, FabCoinErrors.INSUFFICIENT_BALANCE.toString());
        }

        accountState = stub.getStringState(toAccountId);

        if (accountState.isEmpty()) {
            String errorMessage = String.format("Account %s does not exist", toAccountId);
            response = "error:" + errorMessage;
            throw new ChaincodeException(errorMessage, FabCoinErrors.ACCOUNT_NOT_FOUND.toString());
        }

        Account toAccount = genson.deserialize(accountState, Account.class);

        long id = new Date().getTime();

        Account newToAccount = new Account(toAccount.getBalance() + amount, toAccount.getPublicKey(), id);
        String newToAccountState = genson.serialize(newToAccount);
        stub.putStringState(toAccountId, newToAccountState);

        Account newFromAccount = new Account(fromAccount.getBalance() - amount, fromAccount.getPublicKey(), id);
        String newFromAccountState = genson.serialize(newFromAccount);
        stub.putStringState(fromAccountId, newFromAccountState);

        return response;
    }

    /**
     * Retrieves an account with the specified account Id.
     *
     * @param ctx the transaction context
     * @param accountId the account Id
     * @return the account found if there was one
     */
    @Transaction()
    public Account queryAccount(final Context ctx, final String accountId) {
        ChaincodeStub stub = ctx.getStub();
        String accountState = stub.getStringState(accountId);

        if (accountState.isEmpty()) {
            String errorMessage = String.format("Account %s does not exist", accountId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCoinErrors.ACCOUNT_NOT_FOUND.toString());
        }

        Account account = genson.deserialize(accountState, Account.class);

        return account;
    }

    @Transaction()
    public double queryBalance(final Context ctx, final String accountId) {
        ChaincodeStub stub = ctx.getStub();
        String response = "ok";
        String accountState = stub.getStringState(accountId);

        if (accountState.isEmpty()) {
            String errorMessage = String.format("Account %s does not exist", accountId);
            response = "error:" + errorMessage;
            throw new ChaincodeException(errorMessage, FabCoinErrors.ACCOUNT_NOT_FOUND.toString());
        }

        Account account = genson.deserialize(accountState, Account.class);

        return account.getBalance();
    }

    /**
     * Retrieves all accounts
     *
     * @param ctx the transaction context
     * @return array of accounts
     */
    @Transaction()
    public String queryAllAccounts(final Context ctx) {
        ChaincodeStub stub = ctx.getStub();

        final String startKey = "ACC1";
        final String endKey = "ACC99";
        List<AccountQueryResult> queryResults = new ArrayList<AccountQueryResult>();

        QueryResultsIterator<KeyValue> results = stub.getStateByRange(startKey, endKey);

        for (KeyValue result: results) {
            Account account = genson.deserialize(result.getStringValue(), Account.class);
            queryResults.add(new AccountQueryResult(result.getKey(), account));
        }

        final String response = genson.serialize(queryResults);

        return response;
    }

    /**
     * Updates the balance of an account
     *
     * @param ctx the transaction context
     * @param accountId the account Id
     * @param newBalance the new balance
     * @return the updated Account
     */
    @Transaction()
    public Account updateAccountBalance(final Context ctx, final String accountId, final double newBalance) {
        ChaincodeStub stub = ctx.getStub();

        String accountState = stub.getStringState(accountId);

        if (accountState.isEmpty()) {
            String errorMessage = String.format("Account %s does not exist", accountId);
            System.out.println(errorMessage);
            throw new ChaincodeException(errorMessage, FabCoinErrors.ACCOUNT_NOT_FOUND.toString());
        }

        Account account = genson.deserialize(accountState, Account.class);

        Account newAccount = new Account(newBalance, account.getPublicKey(), account.getTimestamp());
        String newAccountState = genson.serialize(newAccount);
        stub.putStringState(accountId, newAccountState);

        return newAccount;
    }

}
