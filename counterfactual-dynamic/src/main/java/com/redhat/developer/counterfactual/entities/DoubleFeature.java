package com.redhat.developer.counterfactual.entities;

import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class DoubleFeature implements CounterfactualEntity {
    @PlanningVariable(valueRangeProviderRefs = {"doubleRange"})
    public Double value;

    double doubleRangeMinimum;
    double doubleRangeMaximum;

    private Feature feature;
    private boolean constrained;

    public DoubleFeature() {
    }

    public DoubleFeature(Feature feature, double minimum, double maximum, boolean constrained) {
        this.value = feature.getValue().asNumber();
        this.feature = feature;
        this.doubleRangeMinimum = minimum;
        this.doubleRangeMaximum = maximum;
        this.constrained = constrained;
    }

    public DoubleFeature(Feature feature, double minimum, double maximum) {
        this(feature, minimum, maximum, false);
    }

    @ValueRangeProvider(id = "doubleRange")
    public ValueRange getValueRange() {
        return ValueRangeFactory.createDoubleValueRange(doubleRangeMinimum, doubleRangeMaximum);
    }

    @Override
    public String toString() {
        return "DoubleFeature{"
                + "value="
                + value
                + ", doubleRangeMinimum="
                + doubleRangeMinimum
                + ", doubleRangeMaximum="
                + doubleRangeMaximum
                + ", id='"
                + feature.getName()
                + '\''
                + '}';
    }

    @Override
    public double distance() {
        return this.value - this.feature.getValue().asNumber();
    }

    @Override
    public Feature asFeature() {
        return FeatureFactory.newNumericalFeature(feature.getName(), this.value);
    }

    @Override
    public boolean isConstrained() {
        return constrained;
    }

    @Override
    public boolean isChanged() {
        return !this.feature.getValue().getUnderlyingObject().equals(this.value);
    }
}
