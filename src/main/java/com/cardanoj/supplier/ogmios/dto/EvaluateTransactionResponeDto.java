package com.cardanoj.supplier.ogmios.dto;

import com.cardanoj.coreapi.model.EvaluationResult;
import com.cardanoj.plutus.spec.RedeemerTag;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@NoArgsConstructor
public class EvaluateTransactionResponeDto {
    private ValidatorDto validator;
    private ExecutionUnitDto budget;

    public EvaluationResult toEvaluationResult() {
        Objects.requireNonNull(validator, "validator field is null");
        Objects.requireNonNull(budget, "budget field is null");
        EvaluationResult evaluationResult = new EvaluationResult();

        RedeemerTag redeemerTag;
        switch (validator.getPurpose()) {
            case "spend":
                redeemerTag = RedeemerTag.Spend;
                break;
            case "mint":
                redeemerTag = RedeemerTag.Mint;
                break;
            case "publish":
                redeemerTag = RedeemerTag.Cert;
                break;
            case "withdraw":
                redeemerTag = RedeemerTag.Reward;
                break;
            default:
                throw new IllegalStateException("Unexpected purpose value: " + validator.getPurpose());
        }

        evaluationResult.setIndex(validator.getIndex());
        evaluationResult.setRedeemerTag(redeemerTag);
        evaluationResult.setExUnits(budget.toExecutionUnit());
        return evaluationResult;
    }
}
