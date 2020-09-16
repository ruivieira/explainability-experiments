package com.redhat.developer.counterfactual.models;

import org.kie.kogito.explainability.model.Feature;

import java.util.List;
import java.util.Map;

public interface Model {

    public Feature predict(List<Feature> features);

}
