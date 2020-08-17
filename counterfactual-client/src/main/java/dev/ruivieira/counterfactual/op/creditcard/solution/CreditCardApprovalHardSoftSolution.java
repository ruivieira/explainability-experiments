package dev.ruivieira.counterfactual.op.creditcard.solution;

import dev.ruivieira.counterfactual.op.creditcard.entities.CreditCardApprovalEntity;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@PlanningSolution
public class CreditCardApprovalHardSoftSolution extends AbstractCreditCardApprovalSolution {

  private HardSoftBigDecimalScore score;

  public CreditCardApprovalHardSoftSolution() {
    super();
  }

  @ValueRangeProvider(id = "ageRange")
  public ValueRange<Double> getAgesList() {
    return ValueRangeFactory.createDoubleValueRange(16.0, 60.0);
  }

  @ValueRangeProvider(id = "incomeRange")
  public ValueRange<Integer> getIncomeList() {
    return ValueRangeFactory.createIntValueRange(100000, 400000);
  }

  @ValueRangeProvider(id = "childrenRange")
  public ValueRange<Integer> getChildrenList() {
    return ValueRangeFactory.createIntValueRange(0, 3);
  }

  @ValueRangeProvider(id = "daysEmployedRange")
  public ValueRange<Integer> getDayEmployedList() {
    return ValueRangeFactory.createIntValueRange(0, 5000);
  }

  @ValueRangeProvider(id = "ownRealtyRange")
  public ValueRange<Boolean> getOwnRealtyList() {
    return ValueRangeFactory.createBooleanValueRange();
  }

  @ValueRangeProvider(id = "workPhoneRange")
  public ValueRange<Boolean> getWorkPhoneList() {
    return ValueRangeFactory.createBooleanValueRange();
  }

  @ValueRangeProvider(id = "ownCarRange")
  public ValueRange<Boolean> getOwnCarList() {
    return ValueRangeFactory.createBooleanValueRange();
  }

  public Collection<? extends Object> getProblemFacts() {
    List<Object> facts = new ArrayList<>();
    // nothing to add because the only facts are already added automatically
    // by planner
    return facts;
  }

  @PlanningEntityCollectionProperty
  public List<CreditCardApprovalEntity> getApprovalsList() {
    return creditCardApprovals;
  }

  @PlanningScore
  public HardSoftBigDecimalScore getScore() {
    return score;
  }

  public void setScore(HardSoftBigDecimalScore score) {
    this.score = score;
  }
}
