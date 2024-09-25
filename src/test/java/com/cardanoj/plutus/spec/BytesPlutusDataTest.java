package com.cardanoj.plutus.spec;

import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.UnicodeString;
import com.cardanoj.common.cbor.CborSerializationUtil;
import com.cardanoj.exception.CborDeserializationException;
import com.cardanoj.exception.CborSerializationException;
import com.cardanoj.util.HexUtil;
import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BytesPlutusDataTest {

    @Test
    void serializeDeserialize() throws CborSerializationException, CborException {
        BytesPlutusData bytesPlutusData = BytesPlutusData.builder()
                .value("Hello World!".getBytes(StandardCharsets.UTF_8))
                .build();

        DataItem di = bytesPlutusData.serialize();
        byte[] bytes = CborSerializationUtil.serialize(di);

        assertThat(HexUtil.encodeHexString(bytes)).isEqualTo("4c48656c6c6f20576f726c6421");
    }

    @Test
    void unicodeDeserializeSerialize() throws CborDeserializationException, CborSerializationException {
        String testString = "Hello World!";

        UnicodeString unicodeString = new UnicodeString(testString);
        PlutusData deserialize = PlutusData.deserialize(unicodeString);

        BytesPlutusData bytesPlutusData = BytesPlutusData.of(testString);

        assertTrue(bytesPlutusData.equals(deserialize));
    }
}
