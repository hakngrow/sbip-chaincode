/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.fabcoin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class AccountTest {

    @Nested
    class Equality {

        @Test
        public void isReflexive() {
            Account account = new Account(1234, "dummy", 1508484583259L);

            assertThat(account).isEqualTo(account);
        }

        @Test
        public void isSymmetric() {
            Account accountA = new Account(1234, "dummy", 1508484583259L);
            Account accountB = new Account(1234, "dummy", 1508484583259L);

            assertThat(accountA).isEqualTo(accountB);
            assertThat(accountB).isEqualTo(accountA);
        }

        @Test
        public void isTransitive() {
            Account accountA = new Account(1234, "dummy", 1508484583259L);
            Account accountB = new Account(1234, "dummy", 1508484583259L);
            Account accountC = new Account(1234, "dummy", 1508484583259L);

            assertThat(accountA).isEqualTo(accountB);
            assertThat(accountB).isEqualTo(accountC);
            assertThat(accountC).isEqualTo(accountA);
        }

        @Test
        public void handlesInequality() {
            Account accountA = new Account(1234, "dummy", 1508484583259L);
            Account accountB = new Account(9876, "dummy", 1508484583259L);

            assertThat(accountA).isNotEqualTo(accountB);
        }

        @Test
        public void handlesOtherObjects() {
            Account accountA = new Account(1234, "dummy", 1508484583259L);
            String accountB = "not an account";

            assertThat(accountA).isNotEqualTo(accountB);
        }

        @Test
        public void handlesNull() {
            Account accountA = new Account(1234, "dummy", 1508484583259L);

            assertThat(accountA).isNotEqualTo(null);
        }
    }

    @Test
    public void toStringIdentifiesLedgerEntry() {
        Account accountA = new Account(1234, "dummy", 1508484583259L);

        System.out.println(accountA.toString());

        assertThat(accountA.toString()).isEqualTo(
            "Account@52da5abb [balance=1234.0, publicKey=dummy, timestamp=1508484583259]");
    }
}
