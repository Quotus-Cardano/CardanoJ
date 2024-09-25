package com.cardanoj.metadata.helper;

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.*;
import com.cardanoj.exception.CborDeserializationException;
import com.cardanoj.metadata.exception.MetadataDeSerializationException;
import com.cardanoj.util.HexUtil;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static co.nstant.in.cbor.model.MajorType.*;

public class MetadataToJsonNoSchemaConverter {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Convert cbor metadata bytes to json string
     * @param cborBytes
     * @return
     */
    public static String cborBytesToJson(byte[] cborBytes)  {
        try {
           return cborHexToJson(HexUtil.encodeHexString(cborBytes));
        } catch (Exception e) {
            throw new MetadataDeSerializationException("Deserialization error", e);
        }
    }

    /**
     * Converts cbor metadata bytes in hex format to json string
     * @param hex
     * @return
     */
    public static String cborHexToJson(String hex)  {
        try {
            java.util.Map result = cborHexToJavaMap(hex);

            return objectMapper.writeValueAsString(result);
        } catch (Exception e) {
            throw new MetadataDeSerializationException("Deserialization error", e);
        }
    }

    private static java.util.Map cborHexToJavaMap(String hex) throws CborDeserializationException {
        byte[] cborBytes = HexUtil.decodeHexString(hex);
        List<DataItem> dataItemList = null;
        try {
            dataItemList = CborDecoder.decode(cborBytes);
        } catch (CborException e) {
            throw new CborDeserializationException("Cbor deserialization failed", e);
        }

        if(dataItemList != null && dataItemList.size() > 1)
            throw new MetadataDeSerializationException("Multiple DataItems found at top level. Should be zero : " + dataItemList.size());

        java.util.Map result = new HashMap();
        DataItem dataItem = dataItemList.get(0);
        if(dataItem instanceof Map) {
            result = processMap((Map)dataItem);
        } else {
            throw new MetadataDeSerializationException("Top leve object should be a Map : " + dataItem.getMajorType().toString());
        }
        return result;
    }

    private static java.util.Map processMap(Map map) {
        java.util.Map resultMap = new HashMap();
        Collection<DataItem> keys = map.getKeys();
        for(DataItem keyItem: keys) {
            DataItem valueItem = map.get(keyItem);
            Object key = processKey(keyItem);
            Object value = processValue(valueItem);

            resultMap.put(key, value);
        }
        return resultMap;
    }

    private static Object processKey(DataItem keyItem) {
        if (UNSIGNED_INTEGER.equals(keyItem.getMajorType())){
            return ((UnsignedInteger) keyItem).getValue();
        } else if(NEGATIVE_INTEGER.equals(keyItem.getMajorType())) {
            return ((NegativeInteger) keyItem).getValue();
        } else if (BYTE_STRING.equals(keyItem.getMajorType())) {
            byte[] bytes = ((ByteString) keyItem).getBytes();
            return "0x" + HexUtil.encodeHexString(bytes);
        } else if (UNICODE_STRING.equals(keyItem.getMajorType())) {
            return ((UnicodeString) keyItem).getString();
        } else {
            throw new MetadataDeSerializationException("Invalid key type : " + keyItem.getMajorType());
        }
    }

    private static Object processValue(DataItem valueItem) {
        if(UNSIGNED_INTEGER.equals(valueItem.getMajorType())){
            return ((UnsignedInteger)valueItem).getValue();
        } else if(NEGATIVE_INTEGER.equals(valueItem.getMajorType())) {
            return ((NegativeInteger)valueItem).getValue();
        } else if(BYTE_STRING.equals(valueItem.getMajorType())) {
            byte[] bytes = ((ByteString)valueItem).getBytes();
            return "0x" + HexUtil.encodeHexString(bytes);
        } else if(UNICODE_STRING.equals(valueItem.getMajorType())) {
            return ((UnicodeString)valueItem).getString();
        } else if(MAP.equals(valueItem.getMajorType())){
            return processMap((Map)valueItem);
        } else if(ARRAY.equals(valueItem.getMajorType())) {
            return processArray((Array)valueItem);
        } else {
            throw new MetadataDeSerializationException("Unsupported type : " + valueItem.getMajorType());
        }
    }

    private static Object processArray(Array array) {
        List<DataItem> dataItems = array.getDataItems();
        List resultList = new ArrayList();
        for(DataItem valueItem: dataItems) {
            Object valueObj = processValue(valueItem);
            resultList.add(valueObj);
        }
        return resultList;
    }

}
