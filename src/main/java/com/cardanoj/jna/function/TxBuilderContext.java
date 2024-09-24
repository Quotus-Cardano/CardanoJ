package com.cardanoj.jna.function;

import com.cardanoj.jna.coreapi.ProtocolParamsSupplier;
import com.cardanoj.jna.coreapi.TransactionEvaluator;
import com.cardanoj.jna.coreapi.UtxoSupplier;
import com.cardanoj.jna.coreapi.helper.FeeCalculationService;
import com.cardanoj.jna.coreapi.helper.TransactionBuilder;
import com.cardanoj.jna.coreapi.helper.impl.FeeCalculationServiceImpl;
import com.cardanoj.jna.coreapi.model.ProtocolParams;
import com.cardanoj.jna.coreapi.model.Utxo;
import com.cardanoj.jna.coinselection.UtxoSelectionStrategy;
import com.cardanoj.jna.coinselection.UtxoSelector;
import com.cardanoj.jna.coinselection.impl.DefaultUtxoSelectionStrategyImpl;
import com.cardanoj.jna.coinselection.impl.DefaultUtxoSelector;
import com.cardanoj.jna.plutus.spec.CostMdls;
import com.cardanoj.jna.transaction.spec.MultiAsset;
import com.cardanoj.jna.transaction.spec.Transaction;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Provides necessary services which are required to build the transaction
 * It also stores some temporary information like multiAsset info for minting transaction.
 */
@Data
public class TxBuilderContext {
    private UtxoSupplier utxoSupplier;
    private ProtocolParams protocolParams;
    private UtxoSelectionStrategy utxoSelectionStrategy;
    private UtxoSelector utxoSelector;
    private FeeCalculationService feeCalculationService;
    private TransactionEvaluator transactionEvaluator;
    private CostMdls costMdls;

    //Needed to check if the output is for minting
    //This list is cleared after each Input Builder
    private List<MultiAsset> mintMultiAssets = new ArrayList<>();
    //Stores utxos used in the transaction.
    //This list is cleared after each build() call.
    private Set<Utxo> utxos = new HashSet<>();

    @Setter(AccessLevel.NONE)
    private boolean mergeOutputs = true;

    public TxBuilderContext(UtxoSupplier utxoSupplier, ProtocolParamsSupplier protocolParamsSupplier) {
        this(utxoSupplier, protocolParamsSupplier.getProtocolParams());
    }

    public TxBuilderContext(UtxoSupplier utxoSupplier, ProtocolParams protocolParams) {
        this.utxoSupplier = utxoSupplier;
        this.protocolParams = protocolParams;
        this.utxoSelectionStrategy = new DefaultUtxoSelectionStrategyImpl(utxoSupplier);
        this.utxoSelector = new DefaultUtxoSelector(utxoSupplier);

        this.feeCalculationService = new FeeCalculationServiceImpl(
                new TransactionBuilder(utxoSupplier, () -> protocolParams));
    }

    /**
     * Set UtxoSelectionStrategy
     * @param utxoSelectionStrategy
     * @return TxBuilderContext
     */
    public TxBuilderContext setUtxoSelectionStrategy(UtxoSelectionStrategy utxoSelectionStrategy) {
        this.utxoSelectionStrategy = utxoSelectionStrategy;
        return this;
    }

    /**
     * Get UtxoSelectionStrategy
     * @return UtxoSelectionStrategy
     */
    public UtxoSelectionStrategy getUtxoSelectionStrategy() {
        return utxoSelectionStrategy;
    }

    /**
     * Set UtxoSelector
     * @param utxoSelector
     * @return TxBuilderContext
     */
    public TxBuilderContext setUtxoSelector(UtxoSelector utxoSelector) {
        this.utxoSelector = utxoSelector;
        return this;
    }

    /**
     * Get UtxoSelector
     * @return UtxoSelector
     */
    public UtxoSelector getUtxoSelector() {
        return utxoSelector;
    }

    public ProtocolParams getProtocolParams() {
        return this.protocolParams;
    }

    public void addMintMultiAsset(MultiAsset multiAsset) {
        mintMultiAssets = MultiAsset.mergeMultiAssetLists(mintMultiAssets, List.of(multiAsset));
    }

    public List<MultiAsset> getMintMultiAssets() {
        return mintMultiAssets;
    }

    public void clearMintMultiAssets() {
        mintMultiAssets.clear();
    }

    public TxBuilderContext withTxnEvaluator(TransactionEvaluator transactionEvaluator) {
        this.transactionEvaluator = transactionEvaluator;
        return this;
    }

    public TransactionEvaluator getTxnEvaluator() {
        return transactionEvaluator;
    }

    public TxBuilderContext withCostMdls(CostMdls costMdls) {
        this.costMdls = costMdls;
        return this;
    }

    /**
     * If true, the outputs will be merged if there are multiple outputs with same address. Default is true.
     * @param mergeOutputs
     */
    public TxBuilderContext mergeOutputs(boolean mergeOutputs) {
        this.mergeOutputs = mergeOutputs;
        return this;
    }

    /**
     * @deprecated
     * Use {@link #withCostMdls(CostMdls)} instead
     * @param costMdls
     */
    @Deprecated(since = "0.4.3", forRemoval = true)
    public void setCostMdls(CostMdls costMdls) {
        withCostMdls(costMdls);
    }

    public void addUtxo(Utxo utxo) {
        utxos.add(utxo);
    }

    public Set<Utxo> getUtxos() {
        return utxos;
    }

    public void clearUtxos() {
        utxos.clear();
    }

    public static TxBuilderContext init(UtxoSupplier utxoSupplier, ProtocolParams protocolParams) {
        return new TxBuilderContext(utxoSupplier, protocolParams);
    }

    public static TxBuilderContext init(UtxoSupplier utxoSupplier, ProtocolParamsSupplier protocolParamsSupplier) {
        return new TxBuilderContext(utxoSupplier, protocolParamsSupplier);
    }

    /**
     * Build a <code>{@link Transaction}</code> using given <code>{@link TxBuilder}</code> function
     * @param txBuilder function to build the transaction
     * @return <code>Transaction</code>
     * @throws com.cardanoj.jna.function.exception.TxBuildException if exception during transaction build
     */
    public Transaction build(TxBuilder txBuilder) {
        Transaction transaction = new Transaction();
        txBuilder.apply(this, transaction);
        clearTempStates();
        return transaction;
    }

    /**
     * Build and sign a <code>{@link Transaction}</code> using given <code>{@link TxBuilder}</code> and <code>Signer</code>
     * @param txBuilder function to build the transaction
     * @param signer function to sign the transaction
     * @return signed <code>Transaction</code>
     * @throws com.cardanoj.jna.function.exception.TxBuildException if exception during transaction build
     */
    public Transaction buildAndSign(TxBuilder txBuilder, TxSigner signer) {
        Transaction transaction = build(txBuilder);
        return signer.sign(transaction);
    }

    /**
     * Transform the given <code>{@link Transaction}</code> using the <code>{@link TxBuilder}</code>
     * @param transaction transaction to transform
     * @param txBuilder function to transform the given transaction
     * @throws com.cardanoj.jna.function.exception.TxBuildException if exception during transaction build
     */
    public void build(Transaction transaction, TxBuilder txBuilder) {
        txBuilder.apply(this, transaction);
        clearTempStates();
    }

    private void clearTempStates() {
        clearMintMultiAssets();
        clearUtxos();
    }
}
