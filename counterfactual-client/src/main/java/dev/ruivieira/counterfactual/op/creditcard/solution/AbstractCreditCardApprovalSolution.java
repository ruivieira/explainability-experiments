package dev.ruivieira.counterfactual.op.creditcard.solution;

import dev.ruivieira.counterfactual.op.creditcard.entities.CreditCardApprovalEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


public class AbstractCreditCardApprovalSolution {
    protected final List<CreditCardApprovalEntity> creditCardApprovals = new ArrayList<>();
    private final List<Integer> ages = new ArrayList<>();
    private final List<Integer> incomes = new ArrayList<>();
    private final List<Integer> children = new ArrayList<>();
    private final List<Integer> daysEmployed = new ArrayList<>();
    private final List<Boolean> ownRealties = new ArrayList<>();
    private final List<Boolean> workPhones = new ArrayList<>();
    private final List<Boolean> ownCards = new ArrayList<>();

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

}
