/*
 * SPDX-License-Identifier: Apache-2.0
 */

package org.hyperledger.fabric.samples.fabcoin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;
//import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.shim.ChaincodeException;
import org.hyperledger.fabric.shim.ChaincodeStub;
import org.hyperledger.fabric.shim.ledger.KeyValue;
import org.hyperledger.fabric.shim.ledger.QueryResultsIterator;
//import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
//import org.mockito.InOrder;

public final class FabCoinTest {

    private final class MockKeyValue implements KeyValue {

        private final String key;
        private final String value;

        MockKeyValue(final String key, final String value) {
            super();
            this.key = key;
            this.value = value;
        }

        @Override
        public String getKey() {
            return this.key;
        }

        @Override
        public String getStringValue() {
            return this.value;
        }

        @Override
        public byte[] getValue() {
            return this.value.getBytes();
        }

    }

    private final class MockCoinResultsIterator implements QueryResultsIterator<KeyValue> {

        private final List<KeyValue> entries;

        MockCoinResultsIterator() {
            super();

            entries = new ArrayList<KeyValue>();

            entries.add(new MockKeyValue("ACC1",
            "{\"balance\":1234,\"publicKey\":\"dummy\",\"timestamp\":1508484583259}"));
            entries.add(new MockKeyValue("ACC2",
            "{\"balance\":2222,\"publicKey\":\"dummy\",\"timestamp\":1508484583259}"));
            entries.add(new MockKeyValue("ACC3",
            "{\"balance\":3399,\"publicKey\":\"dummy\",\"timestamp\":1508484583259}"));
            entries.add(new MockKeyValue("ACC4",
            "{\"balance\":4400,\"publicKey\":\"dummy\",\"timestamp\":1508484583259}"));
        }

        @Override
        public Iterator<KeyValue> iterator() {
            return entries.iterator();
        }

        @Override
        public void close() throws Exception {
            // do nothing
        }

    }

    @Test
    public void invokeUnknownTransaction() {
        FabCoin contract = new FabCoin();
        Context ctx = mock(Context.class);

        Throwable thrown = catchThrowable(() -> {
            contract.unknownTransaction(ctx);
        });

        assertThat(thrown).isInstanceOf(ChaincodeException.class).hasNoCause()
                .hasMessage("Undefined contract method called");
        assertThat(((ChaincodeException) thrown).getPayload()).isEqualTo(null);

        verifyZeroInteractions(ctx);
    }

    @Test
    public void invokeCreateAccountTransaction() {
        FabCoin contract = new FabCoin();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);
        when(stub.getStringState("ACC88")).thenReturn("");

        Account account = contract.createAccount(ctx, "ACC88", 2468, "dummy", 1508484583259L);

        assertThat(account).isEqualTo(new Account(2468, "dummy", 1508484583259L));
    }

    @Test
    public void invokeQueryAccountTransaction() {
        FabCoin contract = new FabCoin();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);

        when(ctx.getStub()).thenReturn(stub);
        when(stub.getStringState("ACC1"))
                .thenReturn("{\"balance\":1234,\"publicKey\":\"dummy\",\"timestamp\":1508484583259}");

        Account account = contract.queryAccount(ctx, "ACC1");

        assertThat(account).isEqualTo(new Account(1234, "dummy", 1508484583259L));
    }

/*
    @Test
    public void invokeQueryAllEntriesTransaction() {
        FabCoin contract = new FabCoin();
        Context ctx = mock(Context.class);
        ChaincodeStub stub = mock(ChaincodeStub.class);
        when(ctx.getStub()).thenReturn(stub);
        when(stub.getStateByRange("ACC1", "ACC2")).thenReturn(new MockCoinResultsIterator());

        String entries = contract.queryAllEntries(ctx, "ACC1");

        System.out.println(entries);

        assertThat(entries).isEqualTo(
        "[{\"key\":\"ACC1\",\"record\":{\"operation\":\"+\",\"timestamp\":\"1508484583259\",\"value\":\"1234\"}},"
        + "{\"key\":\"ACC1\",\"record\":{\"operation\":\"+\",\"timestamp\":\"1508484583260\",\"value\":\"1000\"}}]");
    }
*/
}
