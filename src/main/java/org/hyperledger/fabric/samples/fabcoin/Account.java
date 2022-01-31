/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.fabcoin;

import java.util.Objects;

import org.hyperledger.fabric.contract.annotation.DataType;
import org.hyperledger.fabric.contract.annotation.Property;

import com.owlike.genson.annotation.JsonProperty;

@DataType()
public final class Account {

    @Property()
    private final double balance;

    @Property()
    private final String publicKey;

    @Property()
    private final long timestamp;

    public double getBalance() {
        return this.balance;
    }

    public String getPublicKey() {
        return this.publicKey;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    public Account(
        @JsonProperty("balance") final double balance,
        @JsonProperty("publicKey") final String publicKey,
        @JsonProperty("timestamp") final long timestamp) {
        this.balance = balance;
        this.publicKey = publicKey;
        this.timestamp = timestamp;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if ((obj == null) || (getClass() != obj.getClass())) {
            return false;
        }

        Account other = (Account) obj;

        return Objects.deepEquals(
            new Object[] {getBalance(), getPublicKey(), getTimestamp()},
            new Object[] {other.getBalance(), other.getPublicKey(), other.getTimestamp()});
    }

    @Override
    public int hashCode() {
        return Objects.hash(getBalance(), getPublicKey(), getTimestamp());
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName()
            + "@" + Integer.toHexString(hashCode())
            + " [balance=" + String.valueOf(balance) + ", publicKey=" + publicKey + ", timestamp=" + timestamp + "]";
    }
}
