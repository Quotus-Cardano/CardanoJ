package com.cardanoj.plutus;

import co.nstant.in.cbor.CborException;
import com.cardanoj.common.cbor.CborSerializationUtil;
import com.cardanoj.exception.CborSerializationException;
import com.cardanoj.plutus.annotation.Constr;
import com.cardanoj.plutus.annotation.PlutusField;
import com.cardanoj.plutus.api.PlutusObjectConverter;
import com.cardanoj.plutus.impl.DefaultPlutusObjectConverter;
import com.cardanoj.plutus.spec.*;
import com.cardanoj.util.HexUtil;
import lombok.Builder;
import lombok.Data;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.*;

class DefaultPlutusObjectConverterTest {

    static PlutusObjectConverter plutusObjectConverter;

    @BeforeAll
    static void setup() {
        plutusObjectConverter = new DefaultPlutusObjectConverter();
    }

    @Test
    void toPlutusData() throws CborSerializationException {
        A a = A.builder()
                .l(100L)
                .b(BigInteger.valueOf(30000000))
                .name("hello")
                .address("0x5468697320697320612074657374")
                .build();

        PlutusObjectConverter plutusObjectConverter = new DefaultPlutusObjectConverter();
        PlutusData constrPlutusData = plutusObjectConverter.toPlutusData(a);

        ConstrPlutusData expected = ConstrPlutusData.of(1,
                BigIntPlutusData.of(100L),
                BigIntPlutusData.of(30000000),
                BytesPlutusData.of("hello"),
                BytesPlutusData.of(HexUtil.decodeHexString("0x5468697320697320612074657374")));

        System.out.println(constrPlutusData.serialize());
        Assertions.assertThat(constrPlutusData.serialize()).isEqualTo(expected.serialize());
    }

    @Test
    void toPlutusData_whenCustomObjField() throws CborSerializationException {
        A a = A.builder()
                .l(100L)
                .b(BigInteger.valueOf(30000000))
                .name("hello")
                .address("0x5468697320697320612074657374")
                .build();

        SuperA sa = SuperA.builder()
                .a(a)
                .c(BigInteger.valueOf(200))
                .country("Africa")
                .days(Arrays.asList("Monday", "Tuesday", "Sunday"))
                .ints(Arrays.asList(4, 5005, 6, 89000000))
                .build();

        PlutusObjectConverter plutusObjectConverter = new DefaultPlutusObjectConverter();
        PlutusData constrPlutusData = plutusObjectConverter.toPlutusData(sa);

        ConstrPlutusData expected = ConstrPlutusData.of(0,
                ConstrPlutusData.of(1,
                        BigIntPlutusData.of(100L),
                        BigIntPlutusData.of(30000000),
                        BytesPlutusData.of("hello"),
                        BytesPlutusData.of(HexUtil.decodeHexString("0x5468697320697320612074657374"))
                ),
                BigIntPlutusData.of(200),
                BytesPlutusData.of("Africa"),
                ListPlutusData.of(
                        BytesPlutusData.of("Monday"),
                        BytesPlutusData.of("Tuesday"),
                        BytesPlutusData.of("Sunday")),
                ListPlutusData.of(
                        BigIntPlutusData.of(4),
                        BigIntPlutusData.of(5005),
                        BigIntPlutusData.of(6),
                        BigIntPlutusData.of(89000000)
                )
        );

        System.out.println(constrPlutusData.serialize());
        Assertions.assertThat(constrPlutusData.serialize()).isEqualTo(expected.serialize());
    }

    @Test
    void toPlutusData_whenPlutusData() {
        PlutusData plutusData = BigIntPlutusData.of(100);
        PlutusObjectConverter plutusObjectConverter = new DefaultPlutusObjectConverter();

        PlutusData actual = plutusObjectConverter.toPlutusData(plutusData);

        Assertions.assertThat(actual).isEqualTo(plutusData);
    }

    @Test
    void toPlutusData_whenInteger() throws CborException, CborSerializationException {
        PlutusObjectConverter plutusObjectConverter = new DefaultPlutusObjectConverter();

        PlutusData actual = plutusObjectConverter.toPlutusData(100);

        Assertions.assertThat(actual.getDatumHash()).isEqualTo(BigIntPlutusData.of(100).getDatumHash());
    }

    @Test
    void toPlutusData_whenString() throws CborException, CborSerializationException {
        PlutusObjectConverter plutusObjectConverter = new DefaultPlutusObjectConverter();

        PlutusData actual = plutusObjectConverter.toPlutusData("hello");

        Assertions.assertThat(actual.getDatumHash()).isEqualTo(BytesPlutusData.of("hello").getDatumHash());
    }

    @Test
    void toPlutusData_whenCustomObjFieldAndMap() throws CborSerializationException, CborException {
        A a = A.builder()
                .l(100L)
                .b(BigInteger.valueOf(30000000))
                .name("hello")
                .address("0x5468697320697320612074657374")
                .build();

        SuperA sa = SuperA.builder()
                .a(a)
                .c(BigInteger.valueOf(200))
                .country("Africa")
                .days(Arrays.asList("Monday", "Tuesday", "Sunday"))
                .ints(Arrays.asList(4, 5005, 6, 89000000))
                .build();

        Map<String, A> map = new HashMap<>();
        map.put("one", A.builder().name("1a").build());
        map.put("two", A.builder().name("2a").build());
        map.put("three", A.builder().name("3a").build());

        SuperB sb = SuperB.builder()
                .sa(sa)
                .c(BigInteger.valueOf(99999))
                .map(map)
                .build();

        PlutusObjectConverter plutusObjectConverter = new DefaultPlutusObjectConverter();
        PlutusData constrPlutusData = plutusObjectConverter.toPlutusData(sb);

        ConstrPlutusData saExpected = ConstrPlutusData.of(0,
                ConstrPlutusData.of(1,
                        BigIntPlutusData.of(100L),
                        BigIntPlutusData.of(30000000),
                        BytesPlutusData.of("hello"),
                        BytesPlutusData.of(HexUtil.decodeHexString("0x5468697320697320612074657374"))
                ),
                BigIntPlutusData.of(200),
                BytesPlutusData.of("Africa"),
                ListPlutusData.of(
                        BytesPlutusData.of("Monday"),
                        BytesPlutusData.of("Tuesday"),
                        BytesPlutusData.of("Sunday")),
                ListPlutusData.of(
                        BigIntPlutusData.of(4),
                        BigIntPlutusData.of(5005),
                        BigIntPlutusData.of(6),
                        BigIntPlutusData.of(89000000)
                )
        );

        MapPlutusData sbMap = new MapPlutusData();
        sbMap.put(BytesPlutusData.of("one"), ConstrPlutusData.of(1, BigIntPlutusData.of(0), BigIntPlutusData.of(0), BytesPlutusData.of("1a"), BytesPlutusData.of("")));
        sbMap.put(BytesPlutusData.of("two"), ConstrPlutusData.of(1, BigIntPlutusData.of(0), BigIntPlutusData.of(0), BytesPlutusData.of("2a"), BytesPlutusData.of("")));
        sbMap.put(BytesPlutusData.of("three"), ConstrPlutusData.of(1, BigIntPlutusData.of(0), BigIntPlutusData.of(0), BytesPlutusData.of("3a"), BytesPlutusData.of("")));

        ConstrPlutusData expected = ConstrPlutusData.of(0,
                saExpected,
                sbMap,
                BigIntPlutusData.of(99999));

        System.out.println(HexUtil.encodeHexString(CborSerializationUtil.serialize(constrPlutusData.serialize())));
        Assertions.assertThat(constrPlutusData.serialize()).isEqualTo(expected.serialize());
    }

    @Test
    void testA_whenFieldIsPrimitive() throws CborSerializationException {
        C c = new C();
        c.l = 500;

        PlutusObjectConverter plutusObjectConverter = new DefaultPlutusObjectConverter();
        PlutusData constrPlutusData = plutusObjectConverter.toPlutusData(c);
        System.out.println(constrPlutusData.serializeToHex());

        ConstrPlutusData expected = ConstrPlutusData.of(0, BigIntPlutusData.of(500), BigIntPlutusData.of(0));
        Assertions.assertThat(constrPlutusData.serialize()).isEqualTo(expected.serialize());
    }

    @Nested
    class OptionalTests {
        @Test
        void optionalWithIntValue() {
            Optional<Integer> optional =  Optional.of(42);
            PlutusData plutusData = plutusObjectConverter.toPlutusData(optional);

            Assertions.assertThat(plutusData.serializeToHex()).isEqualTo("d8799f182aff");
        }

        @Test
        void optionalWithEmpty() {
            Optional<Integer> optional =  Optional.empty();
            PlutusData plutusData = plutusObjectConverter.toPlutusData(optional);

            Assertions.assertThat(plutusData.serializeToHex()).isEqualTo("d87a80");
        }

        @Test
        void classWithOptionalFieldAndNonEmptyValue() {
            ClassWithOptional c = new ClassWithOptional();
            c.l = 500;
            c.i = 100;
            c.k = Optional.of(42L);

            ConstrPlutusData plutusData = (ConstrPlutusData) plutusObjectConverter.toPlutusData(c);

            //value of k field
            ConstrPlutusData kPlutusData = (ConstrPlutusData)plutusData.getData().getPlutusDataList().get(2);
            Assertions.assertThat(kPlutusData.serializeToHex()).isEqualTo("d8799f182aff");
            Assertions.assertThat(kPlutusData.getAlternative()).isEqualTo(0);
        }

        @Test
        void classWithOptionalFieldAndEmptyValue() {
            ClassWithOptional c = new ClassWithOptional();
            c.l = 500;
            c.i = 100;
            c.k = Optional.empty();

            ConstrPlutusData plutusData = (ConstrPlutusData) plutusObjectConverter.toPlutusData(c);

            //value of k field
            ConstrPlutusData kPlutusData = (ConstrPlutusData)plutusData.getData().getPlutusDataList().get(2);
            Assertions.assertThat(kPlutusData.serializeToHex()).isEqualTo("d87a80");
            Assertions.assertThat(kPlutusData.getAlternative()).isEqualTo(1);
        }
    }
}

@Data
@Builder
@Constr(alternative = 1)
class A {
    @PlutusField
    @Builder.Default
    Long l = 0L;

    @PlutusField
    @Builder.Default
    BigInteger b = BigInteger.valueOf(0);

    @PlutusField
    @Builder.Default
    String name = "";

    @PlutusField
    @Builder.Default
    String address = "";
}

@Data
@Builder
@Constr
class SuperA {

    @PlutusField
    A a;

    @PlutusField
    BigInteger c;

    @PlutusField
    String country;

    @Builder.Default
    @PlutusField
    List<String> days = new ArrayList<>();

    @Builder.Default
    @PlutusField
    List<Integer> ints = new ArrayList<>();

}

@Data
@Builder
@Constr
class SuperB {

    @PlutusField
    SuperA sa;

    @PlutusField
    @Builder.Default
    Map<String, A> map = new HashMap<>();

    @PlutusField
    BigInteger c;
}

@Constr
class C {
    @PlutusField
    long l;

    @PlutusField
    int i;
}


@Constr
class ClassWithOptional {
    @PlutusField
    long l;

    @PlutusField
    int i;

    @PlutusField
    Optional<Long> k;
}
