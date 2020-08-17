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

    double hardScore = 0.0;
    double softScore = 0.0;
    int changedParameters = 0;

    for (CreditCardApprovalEntity entity : solution.getApprovalsList()) {
      final List<PredictionInput> inputs = buildPredictionInputs(entity);

      final List<PredictionOutput> predictions = getModel().predict(inputs);

      final double[] inputData = buildInputArray();

      final double[] solutionData = buildSolutionData(entity);

      final double inputDistance = Math.pow(Measures.manhattan(inputData, solutionData), 2.0);

      if (predictions.get(0).getOutputs().get(0).getValue().asNumber() != 1.0) {
        hardScore -= 1;
      }

      softScore -= inputDistance;

      if (entity.getAge() != this.getAge().intValue()) {
        changedParameters -= 1;
      }
      if (entity.getChildren() != this.getChildren().intValue()) {
        changedParameters -= 1;
      }

      logger.debug("Input distance: " + inputDistance);
      logger.debug("Hardscore distance: " + hardScore);

    }

    return BendableBigDecimalScore.of(
            new BigDecimal[]{BigDecimal.valueOf(hardScore), BigDecimal.valueOf(changedParameters)},
            new BigDecimal[]{BigDecimal.valueOf(softScore)});
  }
}
