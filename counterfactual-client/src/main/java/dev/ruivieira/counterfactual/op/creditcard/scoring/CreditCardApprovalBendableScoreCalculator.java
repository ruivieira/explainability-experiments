package dev.ruivieira.counterfactual.op.creditcard.scoring;

import com.redhat.developer.model.Output;
import com.redhat.developer.model.PredictionOutput;
import dev.ruivieira.counterfactual.Measures;
import dev.ruivieira.counterfactual.op.creditcard.entities.CreditCardApprovalEntity;
import dev.ruivieira.counterfactual.op.creditcard.solution.CreditCardApprovalBendableSolution;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class CreditCardApprovalBendableScoreCalculator extends AbstractCreditCardApprovalScoreCalculator implements EasyScoreCalculator<CreditCardApprovalBendableSolution> {

  private static Logger logger = LoggerFactory.getLogger(CreditCardApprovalBendableScoreCalculator.class);
  private CSVPrinter printer;

  public CreditCardApprovalBendableScoreCalculator() {
    super();
    try {
//      FileWriter out = new FileWriter("plots/data/bendable_filtered.csv");
      FileWriter out = new FileWriter("plots/data/bendable_closest.csv");
      this.printer = CSVFormat.DEFAULT
              .withHeader("age", "income", "approved", "hs1", "hs2", "ss").print(out);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @Override
  public BendableBigDecimalScore calculateScore(CreditCardApprovalBendableSolution solution) {

    double hardScore = 0.0;
    double softScore = 0.0;
    int changedParameters = 0;

    for (CreditCardApprovalEntity entity : solution.getApprovalsList()) {

      final List<PredictionOutput> predictions = getModel().predict(entity);

      final double[] inputData = buildInputArray();

      final double[] solutionData = buildSolutionData(entity);

      final double inputDistance = Math.pow(Measures.manhattan(inputData, solutionData), 2.0);

      final Output output = predictions.get(0).getOutputs().get(0);

      if (output.getValue().asNumber() != 1.0) {
        hardScore -= 1;
      }

      softScore -= inputDistance;

      if (!entity.getAge().equals(this.getAge())) {
        changedParameters -= 1;
      }
      if (!entity.getChildren().equals(this.getChildren())) {
        changedParameters -= 1;
      }

      logger.debug("Input distance: " + inputDistance);
      logger.debug("Hardscore distance: " + hardScore);
//      logger.debug("age = {}, income = {}, children = {}, days = {}, realties = {}, phone = {}, car = {}",
//              entity.getAge(), entity.getIncome(), entity.getChildren(), entity.getDaysEmployed(),
//              entity.getOwnRealty(), entity.getWorkPhone(), entity.getOwnCar());

      try {
        printer.printRecord(entity.getAge(), entity.getIncome(), output.getValue().asNumber(), hardScore, changedParameters, softScore);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }


    return BendableBigDecimalScore.of(
            new BigDecimal[]{BigDecimal.valueOf(hardScore), BigDecimal.valueOf(changedParameters)},
            new BigDecimal[]{BigDecimal.valueOf(softScore)});
  }
}
