package com.redhat.developer.counterfactual.solutions;

import com.redhat.developer.counterfactual.entities.CreditCardApprovalEntity;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

@PlanningSolution
public class Approval {
  protected final List<CreditCardApprovalEntity> creditCardApprovals = new ArrayList<>();
  private BendableBigDecimalScore score;

  public Approval() {
    final Random r = new Random();
    CreditCardApprovalEntity approval = new CreditCardApprovalEntity();
    approval.setAge(getAgesList().createRandomIterator(r).next());
    approval.setIncome(getIncomeList().createRandomIterator(r).next());
    approval.setChildren(getChildrenList().createRandomIterator(r).next());
    approval.setDaysEmployed(getDayEmployedList().createRandomIterator(r).next());
    approval.setOwnRealty(getOwnRealtyList().createRandomIterator(r).next());
    approval.setWorkPhone(getWorkPhoneList().createRandomIterator(r).next());
    approval.setOwnCar(getOwnCarList().createRandomIterator(r).next());
    creditCardApprovals.add(approval);
  }

  @PlanningEntityCollectionProperty
  public List<CreditCardApprovalEntity> getApprovalsList() {
    return creditCardApprovals;
  }

  @ValueRangeProvider(id = "ageRange")
  public ValueRange<Double> getAgesList() {
    return ValueRangeFactory.createDoubleValueRange(16, 70);
  }

  @ValueRangeProvider(id = "incomeRange")
  public ValueRange<Integer> getIncomeList() {
    return ValueRangeFactory.createIntValueRange(10000, 300000);
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

  @PlanningScore(bendableHardLevelsSize = 2, bendableSoftLevelsSize = 1)
  public BendableBigDecimalScore getScore() {
    return score;
  }

  public void setScore(BendableBigDecimalScore score) {
    this.score = score;
  }

  public Collection<? extends Object> getProblemFacts() {
    List<Object> facts = new ArrayList<>();
    // nothing to add because the only facts are already added automatically
    // by planner
    return facts;
  }
}
