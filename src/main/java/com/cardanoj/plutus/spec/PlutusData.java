package com.cardanoj.plutus.spec;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.*;
import co.nstant.in.cbor.model.Number;
import com.cardanoj.common.cbor.CborSerializationUtil;
import com.cardanoj.crypto.Blake2bUtil;
import com.cardanoj.exception.CborDeserializationException;
import com.cardanoj.exception.CborRuntimeException;
import com.cardanoj.exception.CborSerializationException;
import com.cardanoj.util.HexUtil;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.*;
import lombok.NonNull;
import lombok.*;

public interface PlutusData {

//    plutus_data = ; New
//    constr<plutus_data>
//  / { * plutus_data => plutus_data }
//  / [ * plutus_data ]
//            / big_int
//  / bounded_bytes

    //    big_int = int / big_uint / big_nint ; New
//    big_uint = #6.2(bounded_bytes) ; New
//    big_nint = #6.3(bounded_bytes) ; New
    static PlutusData unit() {
        return ConstrPlutusData.builder()
                .data(ListPlutusData.of())
                .build();
    }

    DataItem serialize() throws CborSerializationException;

    static PlutusData deserialize(DataItem dataItem) throws CborDeserializationException {
        if (dataItem == null)
            return null;

        if (dataItem instanceof Number) {
            return BigIntPlutusData.deserialize((Number) dataItem);
        } else if (dataItem instanceof ByteString) {
            return BytesPlutusData.deserialize((ByteString) dataItem);
        }  else if (dataItem instanceof UnicodeString) {
            return BytesPlutusData.deserialize(((UnicodeString) dataItem));
        } else if (dataItem instanceof Array) {
            if (dataItem.getTag() == null) {
                return ListPlutusData.deserialize((Array) dataItem);
            } else {
                return ConstrPlutusData.deserialize(dataItem);
            }
        } else if (dataItem instanceof Map) {
            return MapPlutusData.deserialize((Map) dataItem);
        } else
            throw new CborDeserializationException("Cbor deserialization failed. Invalid type. " + dataItem);
    }

    static PlutusData deserialize(@NonNull byte[] serializedBytes) throws CborDeserializationException {
        try {
            DataItem dataItem = CborDecoder.decode(serializedBytes).get(0);
            return deserialize(dataItem);
        } catch (CborException | CborDeserializationException e) {
            throw new CborDeserializationException("Cbor de-serialization error", e);
        }
    }

    /**
     * Returns the datum hash as hex string
     * @return datum hash as hex string
     * @throws CborRuntimeException
     */
    @JsonIgnore
    default String getDatumHash() {
        return HexUtil.encodeHexString(getDatumHashAsBytes());
    }

    /**
     * Returns the datum hash as byte array
     * @return datum hash as byte array
     * @throws CborRuntimeException
     */
    @JsonIgnore
    default byte[] getDatumHashAsBytes() {
        try {
            return Blake2bUtil.blake2bHash256(CborSerializationUtil.serialize(serialize()));
        } catch (CborSerializationException | CborException e) {
            throw new CborRuntimeException("Cbor serialization error", e);
        }
    }

    default String serializeToHex()  {
        try {
            return HexUtil.encodeHexString(CborSerializationUtil.serialize(serialize()));
        } catch (Exception e) {
            throw new CborRuntimeException("Cbor serialization error", e);
        }
    }

    default byte[] serializeToBytes()  {
        try {
            return CborSerializationUtil.serialize(serialize());
        } catch (Exception e) {
            throw new CborRuntimeException("Cbor serialization error", e);
        }
    }
}