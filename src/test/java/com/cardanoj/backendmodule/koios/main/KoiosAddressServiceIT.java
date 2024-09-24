package com.cardanoj.backendmodule.koios.main;

import com.cardanoj.backend.api.AddressService;
import com.cardanoj.backend.model.AddressContent;
import com.cardanoj.backend.model.AddressTransactionContent;
import com.cardanoj.coreapi.common.OrderEnum;
import com.cardanoj.coreapi.exception.ApiException;
import com.cardanoj.coreapi.model.Result;
import com.cardanoj.util.JsonUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class KoiosAddressServiceIT extends KoiosBaseTest {

    private AddressService addressService;

    @BeforeEach
    public void setup() {
        addressService = backendService.getAddressService();
    }

    @Test
    void testGetAddressInfo() throws ApiException {
        Result<AddressContent> result = addressService.getAddressInfo("addr_test1qzr0g2kvyknzhyez3aatyjwpaw5z5n65cwfxc5ctcqq28ed3hcc035r9r76tkxehlr9wdla9twe02dpv843nru6czj6qycpamy");
        System.out.println(JsonUtil.getPrettyJson(result.getValue()));

        assertTrue(result.isSuccessful());
        assertFalse(result.getValue().getAmount().isEmpty());
    }

    @Test
    public void testGetTransactions() throws ApiException {
        String address = "addr_test1qzx9hu8j4ah3auytk0mwcupd69hpc52t0cw39a65ndrah86djs784u92a3m5w475w3w35tyd6v3qumkze80j8a6h5tuqq5xe8y";
        List<AddressTransactionContent> txns = addressService.getTransactions(address, 50, 1).getValue();

        System.out.println(txns);
        assertTrue(txns.size() > 2);
        assertEquals("0cf3d7be93b6fd904c5785645931c12f1031436cb96a0702a6e4de75a9fb8af9", txns.get(0).getTxHash());
        assertTrue(txns.get(0).getBlockHeight() != 0);
        assertTrue(txns.get(0).getBlockTime() != 0);
    }

    @Test
    void testGetTransactionsWithOrder() throws ApiException {
        String address = "addr_test1qzr0g2kvyknzhyez3aatyjwpaw5z5n65cwfxc5ctcqq28ed3hcc035r9r76tkxehlr9wdla9twe02dpv843nru6czj6qycpamy";
        List<AddressTransactionContent> txns = addressService.getTransactions(address, 50, 1, OrderEnum.desc).getValue();

        System.out.println(txns);
        assertTrue(txns.size() > 2);
        assertNotEquals("119a69ae496b03936335ed22416116c454a26c00c5c59b22f34535851ba3aa42", txns.get(0).getTxHash());
        assertTrue(txns.get(0).getBlockHeight() != 0);
        assertTrue(txns.get(0).getBlockTime() != 0);
    }

    @Test
    public void testGetTransactionsWithOrder_whenFromAndToBlocksProvided() throws ApiException {
        String address = "addr_test1qzx9hu8j4ah3auytk0mwcupd69hpc52t0cw39a65ndrah86djs784u92a3m5w475w3w35tyd6v3qumkze80j8a6h5tuqq5xe8y";
        List<AddressTransactionContent> txns = addressService.getTransactions(address, 50, 1, OrderEnum.desc, "1825606", "1825606").getValue();

        System.out.println(txns);
        assertThat(txns.size()).isEqualTo(1);
        assertEquals("c8c3b6161fd6430edd81ecfbefb5d136bf126d2f3e95b6c51f09eff745c53b76", txns.get(0).getTxHash());
        assertTrue(txns.get(0).getBlockHeight() != 0);
        assertTrue(txns.get(0).getBlockTime() != 0);
    }
}
