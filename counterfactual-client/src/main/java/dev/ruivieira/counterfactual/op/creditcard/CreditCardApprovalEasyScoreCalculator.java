package dev.ruivieira.counterfactual.op.creditcard;

import com.redhat.developer.model.*;
import dev.ruivieira.counterfactual.Measures;
import dev.ruivieira.counterfactual.models.CreditCardApprovalModel;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CreditCardApprovalEasyScoreCalculator
    implements EasyScoreCalculator<CreditCardApprovalSolution> {

  private Double age;
  private Double income;
  private Double children;
  private Double daysEmployed;
  private Integer ownRealty;
  private Integer workPhone;
  private Integer ownCar;
  private Model model;

  public CreditCardApprovalEasyScoreCalculator() {
    super();
    try {
      this.model = new CreditCardApprovalModel();
    } catch (IOException | SAXException | JAXBException e) {
      e.printStackTrace();
    }
  }

  public void setAge(Double age) {
    this.age = age;
  }

  public void setIncome(Double income) {
    this.income = income;
  }

  public void setChildren(Double children) {
    this.children = children;
  }

  public void setDaysEmployed(Double daysEmployed) {
    this.daysEmployed = daysEmployed;
  }

  public void setOwnRealty(Integer ownRealty) {
    this.ownRealty = ownRealty;
  }

  public void setWorkPhone(Integer workPhone) {
    this.workPhone = workPhone;
  }

  public void setOwnCar(Integer ownCar) {
    this.ownCar = ownCar;
  }

  @Override
  public HardSoftBigDecimalScore calculateScore(CreditCardApprovalSolution solution) {

    final List<Feature> features = new ArrayList<>();

    final CreditCardApprovalEntity approvalEntity = solution.getApprovalsList().get(0);

    final Feature age = FeatureFactory.newNumericalFeature("AGE", approvalEntity.getAge().doubleValue());
    final Feature income =
        FeatureFactory.newNumericalFeature("AMT_INCOME_TOTAL", approvalEntity.getIncome().doubleValue());
    final Feature children =
        FeatureFactory.newNumericalFeature("CNT_CHILDREN", approvalEntity.getChildren().doubleValue());
    final Feature daysEmployed =
        FeatureFactory.newNumericalFeature("DAYS_EMPLOYED", approvalEntity.getDaysEmployed().doubleValue());
    final Feature ownRealty =
        FeatureFactory.newNumericalFeature(
            "FLAG_OWN_REALTY", approvalEntity.getOwnRealty() ? 1 : 0);
    final Feature workPhone =
        FeatureFactory.newNumericalFeature(
            "FLAG_WORK_PHONE", approvalEntity.getWorkPhone() ? 1 : 0);
    final Feature ownCar =
        FeatureFactory.newNumericalFeature("FLAG_OWN_CAR", approvalEntity.getOwnCar() ? 1 : 0);

    features.add(children);
    features.add(daysEmployed);
    features.add(age);
    features.add(income);
    features.add(workPhone);
    features.add(ownRealty);
    features.add(ownCar);

    final List<PredictionInput> inputs = new ArrayList<>();
    inputs.add(new PredictionInput(features));

    final List<PredictionOutput> predictions = model.predict(inputs);

    final double predictionDistance =
        Math.pow(1.0 - predictions.get(0).getOutputs().get(0).getValue().asNumber(), 2);
    //
    final double[] inputData =
        new double[] {
          this.age,
          this.income,
          this.children,
          this.daysEmployed,
          this.ownRealty,
          this.workPhone,
          this.ownCar
        };

    final double[] solutionData =
        new double[] {
          approvalEntity.getAge(),
          approvalEntity.getIncome(),
          approvalEntity.getChildren(),
          approvalEntity.getDaysEmployed(),
          approvalEntity.getOwnRealty() ? 1.0 : 0.0,
          approvalEntity.getWorkPhone() ? 1.0 : 0.0,
          approvalEntity.getOwnCar() ? 1.0 : 0.0
        };

    final double inputDistance = Measures.manhattan(inputData, solutionData);
//    System.out.println("Input distance: " + inputDistance);

    double hardScore = 0.0;

    if (approvalEntity.getAge() != this.age.intValue()) {
      hardScore -= 1;
    }
    if (approvalEntity.getChildren() != this.children.intValue()) {
      hardScore -= 1;
    }

    if (approvalEntity.getDaysEmployed() != this.daysEmployed.intValue()) {
      hardScore -= 1;
    }

        return HardSoftBigDecimalScore.of(
        BigDecimal.valueOf(hardScore),
        BigDecimal.valueOf(-Math.abs(predictionDistance)));

  }
}
