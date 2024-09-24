package com.cardanoj.jna.transaction.spec.cert;

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.UnsignedInteger;
import com.cardanoj.jna.exception.CborSerializationException;
import com.cardanoj.jna.transaction.spec.governance.DRep;
import com.cardanoj.jna.util.HexUtil;
import lombok.*;

import java.util.List;
import java.util.Objects;

import static com.cardanoj.jna.common.cbor.CborSerializationUtil.toHex;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StakeVoteDelegCert implements Certificate {
    private final CertificateType type = CertificateType.STAKE_VOTE_DELEG_CERT;

    private StakeCredential stakeCredential;
    private String poolKeyHash;
    private DRep drep;

    @Override
    public Array serialize() throws CborSerializationException {
        Objects.requireNonNull(stakeCredential);
        Objects.requireNonNull(poolKeyHash);
        Objects.requireNonNull(drep);

        Array certArray = new Array();
        certArray.add(new UnsignedInteger(type.getValue()));
        certArray.add(stakeCredential.serialize());
        certArray.add(new ByteString(HexUtil.decodeHexString(poolKeyHash)));
        certArray.add(drep.serialize());

        return certArray;
    }

    @SneakyThrows
    public static StakeVoteDelegCert deserialize(DataItem di) {
        Array certArray = (Array) di;
        List<DataItem> dataItemList = certArray.getDataItems();

        StakeCredential stakeCredential = StakeCredential.deserialize((Array) dataItemList.get(1));
        String poolKeyHash = toHex(dataItemList.get(2));
        DRep drep = DRep.deserialize(dataItemList.get(3));
        return new StakeVoteDelegCert(stakeCredential, poolKeyHash, drep);
    }
}
