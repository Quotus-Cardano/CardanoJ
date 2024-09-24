package com.cardanoj.jna.plutus.annotation;

import com.cardanoj.jna.plutus.spec.*;
import com.cardanoj.jna.util.HexUtil;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class BasePlutusDataConverter {

    protected ConstrPlutusData initConstr(long alternative) {
        return ConstrPlutusData
                .builder()
                .alternative(alternative)
                .data(ListPlutusData.builder().build())
                .build();
    }

    protected PlutusData toPlutusData(Long number) {
        return BigIntPlutusData.of(number);
    }

    protected PlutusData toPlutusData(BigInteger bi) {
        return BigIntPlutusData.of(bi);
    }

    protected PlutusData toPlutusData(Integer i) {
        return BigIntPlutusData.of(i);
    }

    protected PlutusData toPlutusData(byte[] bytes) {
        return BytesPlutusData.of(bytes);
    }

    protected PlutusData toPlutusData(String s) {
        if (s.startsWith("0x") || s.startsWith("0X")) {
            byte[] bytes = HexUtil.decodeHexString(s);
            return BytesPlutusData.of(bytes);
        } else {
            return BytesPlutusData.of(s);
        }
    }

    protected PlutusData toPlutusData(Boolean b) {
        Objects.requireNonNull(b, "Boolean value cannot be null");

        if (b)
            return ConstrPlutusData.of(1);
        else
            return ConstrPlutusData.of(0);
    }

    protected Long plutusDataToLong(PlutusData data) {
        return ((BigIntPlutusData) data).getValue().longValue();
    }

    protected Integer plutusDataToInteger(PlutusData data) {
        return ((BigIntPlutusData) data).getValue().intValue();
    }

    protected BigInteger plutusDataToBigInteger(PlutusData data) {
        return ((BigIntPlutusData) data).getValue();
    }

    protected byte[] plutusDataToBytes(PlutusData data) {
        return ((BytesPlutusData) data).getValue();
    }

    protected String plutusDataToString(PlutusData data, String encoding) {
        return deserializeBytesToString(((BytesPlutusData) data).getValue(), encoding);
    }

    protected Boolean plutusDataToBoolean(PlutusData data) {
        return ((ConstrPlutusData)data).getAlternative() == 1;
    }

    protected String deserializeBytesToString(byte[] bytes, String encoding) {
        if (encoding == null || encoding.isEmpty() || encoding.equalsIgnoreCase("utf-8")) {
            return new String(bytes, StandardCharsets.UTF_8);
        } else if (encoding.equalsIgnoreCase("hex")) {
            return HexUtil.encodeHexString(bytes);
        } else {
            throw new IllegalArgumentException("Unsupported encoding: " + encoding);
        }
    }
}
