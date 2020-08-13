package dev.ruivieira.counterfactual.op.creditcard.solution;

import dev.ruivieira.counterfactual.op.creditcard.entities.CreditCardApprovalEntity;
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
public class CreditCardApprovalBendableSolution  extends AbstractCreditCardApprovalSolution {
    private BendableBigDecimalScore score;

    public CreditCardApprovalBendableSolution() {
        CreditCardApprovalEntity approval = new CreditCardApprovalEntity();
        approval.setAge(getAgesList().createRandomIterator(new Random()).next());
        approval.setIncome(getIncomeList().createRandomIterator(new Random()).next());
        approval.setChildren(getChildrenList().createRandomIterator(new Random()).next());
        approval.setDaysEmployed(getDayEmployedList().createRandomIterator(new Random()).next());
        approval.setOwnRealty(getOwnRealtyList().createRandomIterator(new Random()).next());
        approval.setWorkPhone(getWorkPhoneList().createRandomIterator(new Random()).next());
        approval.setOwnCar(getOwnCarList().createRandomIterator(new Random()).next());
        creditCardApprovals.add(approval);
    }

    @PlanningEntityCollectionProperty
    public List<CreditCardApprovalEntity> getApprovalsList() {
        return creditCardApprovals;
    }

    @ValueRangeProvider(id = "ageRange")
    public ValueRange<Integer> getAgesList() {
        return ValueRangeFactory.createIntValueRange(16, 60);
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

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<>();
        // nothing to add because the only facts are already added automatically
        // by planner
        return facts;
    }

    @PlanningScore(bendableHardLevelsSize = 1, bendableSoftLevelsSize = 2)
    public BendableBigDecimalScore getScore() {
        return score;
    }

    public void setScore(BendableBigDecimalScore score) {
        this.score = score;
    }

}
