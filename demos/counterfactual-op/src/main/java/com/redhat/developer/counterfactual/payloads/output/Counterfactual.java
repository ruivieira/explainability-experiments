package com.redhat.developer.counterfactual.payloads.output;

import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.Output;

import java.util.List;

public class Counterfactual {
    public List<Feature> features;
    public Output prediction;
}
