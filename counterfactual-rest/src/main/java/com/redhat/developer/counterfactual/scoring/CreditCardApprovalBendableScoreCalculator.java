package com.redhat.developer.counterfactual.scoring;

import com.redhat.developer.counterfactual.Measures;
import com.redhat.developer.counterfactual.entities.CreditCardApprovalEntity;
import com.redhat.developer.counterfactual.metrics.Metric;
import com.redhat.developer.counterfactual.metrics.Metrics;
import com.redhat.developer.counterfactual.solutions.Approval;
import com.redhat.developer.model.Output;
import com.redhat.developer.model.PredictionOutput;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.List;

public class CreditCardApprovalBendableScoreCalculator
    extends AbstractCreditCardApprovalScoreCalculator implements EasyScoreCalculator<Approval> {

  private static final Logger logger =
      LoggerFactory.getLogger(CreditCardApprovalBendableScoreCalculator.class);

  @Override
  public BendableBigDecimalScore calculateScore(Approval solution) {

    double hardScore = 0.0;
    double softScore = 0.0;
    int changedParameters = 0;

    for (CreditCardApprovalEntity entity : solution.getApprovalsList()) {

      final List<PredictionOutput> predictions = getModel().predict(entity);

      final double[] inputData = buildInputArray();

      final double[] solutionData = buildSolutionData(entity);

      final double inputDistance = Math.pow(Measures.manhattan(inputData, solutionData), 2.0);

      final Output output = predictions.get(0).getOutputs().get(0);

      final Metric metric = new Metric();
      metric.age = entity.getAge();

      metric.income = entity.getIncome();
      metric.daysEmployed = entity.getDaysEmployed();

      metric.approved = output.getValue().asNumber() == 1.0;

      if (output.getValue().asNumber() != 1.0) {
        hardScore -= 1;
      }

      softScore -= inputDistance;

      metric.softScore = -inputDistance;

      Metrics.metrics.add(metric);

      if (!entity.getAge().equals(this.getAge())) {
        changedParameters -= 1;
      }
      if (!entity.getChildren().equals(this.getChildren())) {
        changedParameters -= 1;
      }

      metric.hardScore = output.getValue().asNumber();

      logger.debug("Input distance: " + inputDistance);
      logger.debug("Hard Score distance: " + hardScore);
    }

    return BendableBigDecimalScore.of(
        new BigDecimal[] {BigDecimal.valueOf(hardScore), BigDecimal.valueOf(changedParameters)},
        new BigDecimal[] {BigDecimal.valueOf(softScore)});
  }
}
