package com.cardanoj.jna.coreapi.transaction.model;

import com.cardanoj.jna.coreapi.account.Account;
import com.cardanoj.jna.coreapi.model.Utxo;
import com.cardanoj.jna.crypto.SecretKey;
import com.cardanoj.jna.transaction.spec.MultiAsset;
import com.cardanoj.jna.transaction.spec.Policy;
import com.cardanoj.jna.transaction.spec.script.NativeScript;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used while minting a new native token
 * @deprecated Use Composable Functions API or QuickTx API to build transaction
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Deprecated(since = "0.5.0")
public class MintTransaction extends TransactionRequest {

    private List<MultiAsset> mintAssets;
    @JsonIgnore
    private Policy policy;

    @Builder
    public MintTransaction(Account sender, String receiver, BigInteger fee, List<Account> additionalWitnessAccounts,
                           List<Utxo> utxosToInclude, String datumHash, List<MultiAsset> mintAssets,
                           @Deprecated NativeScript policyScript,
                           @Deprecated List<SecretKey> policyKeys, Policy policy) {
        super(sender, receiver, fee, additionalWitnessAccounts, utxosToInclude, datumHash);

        //merge mintAssets if same policyids
        if (mintAssets != null && mintAssets.size() > 1) {
            List<MultiAsset> multiAssets = new ArrayList<>();
            for (MultiAsset ma : mintAssets) {
                multiAssets = MultiAsset.mergeMultiAssetLists(multiAssets, List.of(ma));
            }

            this.mintAssets = multiAssets;
        } else {
            this.mintAssets = mintAssets;
        }

        if (policy != null) {
            this.policy = policy;
        } else {
            this.policy = new Policy(policyScript, policyKeys);
        }
    }
}
