package dev.ruivieira.counterfactual.op.creditcard.scoring;

import com.redhat.developer.model.PredictionInput;
import com.redhat.developer.model.PredictionOutput;
import dev.ruivieira.counterfactual.Measures;
import dev.ruivieira.counterfactual.op.creditcard.entities.CreditCardApprovalEntity;
import dev.ruivieira.counterfactual.op.creditcard.solution.CreditCardApprovalBendableSolution;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class CreditCardApprovalBendableScoreCalculator extends AbstractCreditCardApprovalScoreCalculator implements EasyScoreCalculator<CreditCardApprovalBendableSolution> {

  private static Logger logger = LoggerFactory.getLogger(CreditCardApprovalBendableScoreCalculator.class);

  @Override
  public BendableBigDecimalScore calculateScore(CreditCardApprovalBendableSolution solution) {

    final CreditCardApprovalEntity approvalEntity = solution.getApprovalsList().get(0);

    final List<PredictionInput> inputs = buildPredictionInputs(approvalEntity);

    final List<PredictionOutput> predictions = getModel().predict(inputs);

    final double[] inputData = buildInputArray();

    final double[] solutionData = buildSolutionData(approvalEntity);

    final double inputDistance = Measures.manhattan(inputData, solutionData);

    double hardScore = 0.0;

    if (predictions.get(0).getOutputs().get(0).getValue().asNumber() != 1.0) {
      hardScore -= 1;
    }

    final double softScore = -Math.abs(inputDistance);

    int changedParameters = 0;

    if (approvalEntity.getAge() != this.getAge().intValue()) {
      changedParameters -= 1;
    }
    if (approvalEntity.getChildren() != this.getChildren().intValue()) {
      changedParameters -= 1;
    }

    logger.debug("Input distance: " + inputDistance);
    logger.debug("Hardscore distance: " + hardScore);

    return BendableBigDecimalScore.of(
            new BigDecimal[]{BigDecimal.valueOf(hardScore)},
            new BigDecimal[]{BigDecimal.valueOf(changedParameters), BigDecimal.valueOf(softScore)});
  }
}
