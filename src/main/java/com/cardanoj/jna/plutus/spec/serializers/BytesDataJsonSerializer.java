package com.cardanoj.jna.plutus.spec.serializers;

import com.cardanoj.jna.plutus.spec.BytesPlutusData;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.cardanoj.jna.util.HexUtil;

import java.io.IOException;

import static com.cardanoj.jna.plutus.spec.serializers.PlutusDataJsonKeys.BYTES;

public class BytesDataJsonSerializer extends StdSerializer<BytesPlutusData> {

    public BytesDataJsonSerializer() {
        this(null);
    }

    public BytesDataJsonSerializer(Class<BytesPlutusData> clazz) {
        super(clazz);
    }

    @Override
    public void serialize(BytesPlutusData value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeStringField(BYTES, HexUtil.encodeHexString(value.getValue()));
        gen.writeEndObject();
    }
}

