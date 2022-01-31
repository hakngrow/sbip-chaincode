/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.fabcoin;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public final class AccountQueryResultTest {

    @Nested
    class Equality {

        @Test
        public void isReflexive() {
            AccountQueryResult aqr = new AccountQueryResult("ACC1", new Account(1234, "dummy", 1508484583259L));

            assertThat(aqr).isEqualTo(aqr);
        }

        @Test
        public void isSymmetric() {
            Account account = new Account(1234, "dummy", 1508484583259L);
            AccountQueryResult aqrA = new AccountQueryResult("ACC1", account);
            AccountQueryResult aqrB = new AccountQueryResult("ACC1", account);

            assertThat(aqrA).isEqualTo(aqrB);
            assertThat(aqrB).isEqualTo(aqrA);
        }

        @Test
        public void isTransitive() {
            Account account = new Account(1234, "dummy", 1508484583259L);
            AccountQueryResult aqrA = new AccountQueryResult("ACC1", account);
            AccountQueryResult aqrB = new AccountQueryResult("ACC1", account);
            AccountQueryResult aqrC = new AccountQueryResult("ACC1", account);

            assertThat(aqrA).isEqualTo(aqrB);
            assertThat(aqrB).isEqualTo(aqrC);
            assertThat(aqrA).isEqualTo(aqrC);
        }

        @Test
        public void handlesKeyInequality() {
            Account account = new Account(1234, "dummy", 1508484583259L);
            AccountQueryResult aqrA = new AccountQueryResult("ACC1", account);
            AccountQueryResult aqrB = new AccountQueryResult("ACC2", account);

            assertThat(aqrA).isNotEqualTo(aqrB);
        }

        @Test
        public void handlesRecordInequality() {
            AccountQueryResult aqrA = new AccountQueryResult("ACC1", new Account(1234, "dummy", 1508484583259L));
            AccountQueryResult aqrB = new AccountQueryResult("ACC1", new Account(9876, "dummy", 1508484583259L));

            assertThat(aqrA).isNotEqualTo(aqrB);
        }

        @Test
        public void handlesKeyRecordInequality() {
            AccountQueryResult aqrA = new AccountQueryResult("ACC1", new Account(1234, "dummy", 1508484583259L));
            AccountQueryResult aqrB = new AccountQueryResult("ACC2", new Account(9876, "dummy", 1508484583259L));

            assertThat(aqrA).isNotEqualTo(aqrB);
        }

        @Test
        public void handlesOtherObjects() {
            AccountQueryResult aqrA = new AccountQueryResult("ACC1", new Account(1234, "dummy", 1508484583259L));
            String aqrB = "not an account result";

            assertThat(aqrA).isNotEqualTo(aqrB);
        }

        @Test
        public void handlesNull() {
            AccountQueryResult aqrA = new AccountQueryResult("ACC1", new Account(1234, "dummy", 1508484583259L));

            assertThat(aqrA).isNotEqualTo(null);
        }
    }

    @Test
    public void toStringIdentifiesAccountQueryResult() {
        AccountQueryResult aqrA = new AccountQueryResult("ACC1", new Account(1234, "dummy", 1508484583259L));

        System.out.println(aqrA.toString());

        assertThat(aqrA.toString()).isEqualTo("AccountQueryResult@568dcc8c [key=ACC1, "
                + "record=Account@52da5abb [balance=1234.0, publicKey=dummy, timestamp=1508484583259]]");
    }
}
