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

@PlanningSolution
public class CreditCardApprovalBendableSolution  extends AbstractCreditCardApprovalSolution {
    private BendableBigDecimalScore score;

    public CreditCardApprovalBendableSolution() {
        super();
    }

    @PlanningEntityCollectionProperty
    public List<CreditCardApprovalEntity> getApprovalsList() {
        return creditCardApprovals;
    }

    @ValueRangeProvider(id = "ageRange")
    public ValueRange<Double> getAgesList() {
        return ValueRangeFactory.createDoubleValueRange(18.0, 60.0);
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

    @PlanningScore(bendableHardLevelsSize = 2, bendableSoftLevelsSize = 1)
    public BendableBigDecimalScore getScore() {
        return score;
    }

    public void setScore(BendableBigDecimalScore score) {
        this.score = score;
    }

}
