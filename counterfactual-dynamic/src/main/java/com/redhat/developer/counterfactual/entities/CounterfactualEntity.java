package com.redhat.developer.counterfactual.entities;

import org.kie.kogito.explainability.model.Feature;

public interface CounterfactualEntity {
    public double distance();

    public Feature asFeature();

    public boolean isConstrained();

    public boolean isChanged();
}
