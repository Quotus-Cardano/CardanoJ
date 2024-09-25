package com.cardanoj.coreapi.transaction.util;

import com.cardanoj.coreapi.model.ProtocolParams;
import com.cardanoj.plutus.spec.CostMdls;
import com.cardanoj.plutus.spec.CostModel;
import com.cardanoj.plutus.spec.Language;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

public class CostModelUtil {

    //babbage -- 14th Feb 2023 HF
    private final static long[] plutusV1Costs = new long[]{
            205665,
            812,
            1,
            1,
            1000,
            571,
            0,
            1,
            1000,
            24177,
            4,
            1,
            1000,
            32,
            117366,
            10475,
            4,
            23000,
            100,
            23000,
            100,
            23000,
            100,
            23000,
            100,
            23000,
            100,
            23000,
            100,
            100,
            100,
            23000,
            100,
            19537,
            32,
            175354,
            32,
            46417,
            4,
            221973,
            511,
            0,
            1,
            89141,
            32,
            497525,
            14068,
            4,
            2,
            196500,
            453240,
            220,
            0,
            1,
            1,
            1000,
            28662,
            4,
            2,
            245000,
            216773,
            62,
            1,
            1060367,
            12586,
            1,
            208512,
            421,
            1,
            187000,
            1000,
            52998,
            1,
            80436,
            32,
            43249,
            32,
            1000,
            32,
            80556,
            1,
            57667,
            4,
            1000,
            10,
            197145,
            156,
            1,
            197145,
            156,
            1,
            204924,
            473,
            1,
            208896,
            511,
            1,
            52467,
            32,
            64832,
            32,
            65493,
            32,
            22558,
            32,
            16563,
            32,
            76511,
            32,
            196500,
            453240,
            220,
            0,
            1,
            1,
            69522,
            11687,
            0,
            1,
            60091,
            32,
            196500,
            453240,
            220,
            0,
            1,
            1,
            196500,
            453240,
            220,
            0,
            1,
            1,
            806990,
            30482,
            4,
            1927926,
            82523,
            4,
            265318,
            0,
            4,
            0,
            85931,
            32,
            205665,
            812,
            1,
            1,
            41182,
            32,
            212342,
            32,
            31220,
            32,
            32696,
            32,
            43357,
            32,
            32247,
            32,
            38314,
            32,
            57996947,
            18975,
            10
    };

    private final static long[] plutusV2Costs = new long[]{
            205665,
            812,
            1,
            1,
            1000,
            571,
            0,
            1,
            1000,
            24177,
            4,
            1,
            1000,
            32,
            117366,
            10475,
            4,
            23000,
            100,
            23000,
            100,
            23000,
            100,
            23000,
            100,
            23000,
            100,
            23000,
            100,
            100,
            100,
            23000,
            100,
            19537,
            32,
            175354,
            32,
            46417,
            4,
            221973,
            511,
            0,
            1,
            89141,
            32,
            497525,
            14068,
            4,
            2,
            196500,
            453240,
            220,
            0,
            1,
            1,
            1000,
            28662,
            4,
            2,
            245000,
            216773,
            62,
            1,
            1060367,
            12586,
            1,
            208512,
            421,
            1,
            187000,
            1000,
            52998,
            1,
            80436,
            32,
            43249,
            32,
            1000,
            32,
            80556,
            1,
            57667,
            4,
            1000,
            10,
            197145,
            156,
            1,
            197145,
            156,
            1,
            204924,
            473,
            1,
            208896,
            511,
            1,
            52467,
            32,
            64832,
            32,
            65493,
            32,
            22558,
            32,
            16563,
            32,
            76511,
            32,
            196500,
            453240,
            220,
            0,
            1,
            1,
            69522,
            11687,
            0,
            1,
            60091,
            32,
            196500,
            453240,
            220,
            0,
            1,
            1,
            196500,
            453240,
            220,
            0,
            1,
            1,
            1159724,
            392670,
            0,
            2,
            806990,
            30482,
            4,
            1927926,
            82523,
            4,
            265318,
            0,
            4,
            0,
            85931,
            32,
            205665,
            812,
            1,
            1,
            41182,
            32,
            212342,
            32,
            31220,
            32,
            32696,
            32,
            43357,
            32,
            32247,
            32,
            38314,
            32,
            35892428,
            10,
            57996947,
            18975,
            10,
            38887044,
            32947,
            10
    };

    public final static CostModel PlutusV1CostModel = new CostModel(Language.PLUTUS_V1, plutusV1Costs);
    public final static CostModel PlutusV2CostModel = new CostModel(Language.PLUTUS_V2, plutusV2Costs);

    /**
     * Get language view encoding for costmodels
     * @param costModels
     * @return Language view encoding in bytes
     */
    public static byte[] getLanguageViewsEncoding(CostModel... costModels) {
        CostMdls costMdls = new CostMdls();
        for (CostModel cm : costModels) {
            costMdls.add(cm);
        }

        return costMdls.getLanguageViewEncoding();
    }

    /**
     * Get costmodel for a language from protocol parameters.
     * @param protocolParams
     * @param language
     * @return Optional with costmodel if found, otherwise Optional.empty()
     */
    public static Optional<CostModel> getCostModelFromProtocolParams(ProtocolParams protocolParams, Language language) {
        String languageKey = null;
        if (language == Language.PLUTUS_V1) {
            languageKey = "PlutusV1";
        } else if (language == Language.PLUTUS_V2) {
            languageKey = "PlutusV2";
        }

        if (protocolParams.getCostModels() == null)
            return Optional.empty();

        Map<String, Map<String, Long>> costModels = protocolParams.getCostModels();
        Map<String, Long> costModelMap = costModels.get(languageKey);
        if (costModelMap == null)
            return Optional.empty();

        long[] costModel = costModelMap.entrySet().stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(e -> e.getValue())
                .mapToLong(x -> x)
                .toArray();

        return Optional.of(new CostModel(language, costModel));
    }
}