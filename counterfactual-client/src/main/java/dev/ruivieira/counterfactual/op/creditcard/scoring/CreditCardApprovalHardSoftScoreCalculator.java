package dev.ruivieira.counterfactual.op.creditcard.scoring;

import com.redhat.developer.model.Output;
import com.redhat.developer.model.PredictionInput;
import com.redhat.developer.model.PredictionOutput;
import dev.ruivieira.counterfactual.Measures;
import dev.ruivieira.counterfactual.op.creditcard.entities.CreditCardApprovalEntity;
import dev.ruivieira.counterfactual.op.creditcard.solution.CreditCardApprovalHardSoftSolution;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class CreditCardApprovalHardSoftScoreCalculator extends AbstractCreditCardApprovalScoreCalculator  implements EasyScoreCalculator<CreditCardApprovalHardSoftSolution> {

  private static Logger logger = LoggerFactory.getLogger(CreditCardApprovalHardSoftScoreCalculator.class);

  public HardSoftBigDecimalScore calculateScore(CreditCardApprovalHardSoftSolution solution) {

    double hardScore = 0.0;
    double softScore = 0.0;

    for (CreditCardApprovalEntity entity : solution.getApprovalsList()) {
      final List<PredictionInput> inputs = buildPredictionInputs(entity);

      final List<PredictionOutput> predictions = getModel().predict(inputs);

      final double[] inputData = buildInputArray();

      final double[] solutionData = buildSolutionData(entity);

      final double inputDistance = Math.pow(Measures.manhattan(inputData, solutionData), 2.0);

      softScore -= inputDistance;

      final Output output = predictions.get(0).getOutputs().get(0);

      if (output.getValue().asNumber() != 1.0) {
        hardScore -= 1;
      }

      logger.debug("Input distance: " + inputDistance);
      logger.debug("Hardscore distance: " + hardScore);
      logger.debug("age = {}, income = {}, children = {}, days = {}, realties = {}, phone = {}, car = {}",
              entity.getAge(), entity.getIncome(), entity.getChildren(), entity.getDaysEmployed(),
              entity.getOwnRealty(), entity.getWorkPhone(), entity.getOwnCar());

    }

    return HardSoftBigDecimalScore.of(
            BigDecimal.valueOf(hardScore),
            BigDecimal.valueOf(softScore));

  }
}
