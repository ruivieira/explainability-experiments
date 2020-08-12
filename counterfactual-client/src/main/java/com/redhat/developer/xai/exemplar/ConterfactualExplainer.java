package com.redhat.developer.xai.exemplar;

import com.redhat.developer.model.Counterfactual;
import com.redhat.developer.model.Model;
import com.redhat.developer.model.Prediction;
import com.redhat.developer.xai.LocalExplainer;

public class ConterfactualExplainer implements LocalExplainer<Counterfactual> {
    @Override
    public Counterfactual explain(Prediction prediction, Model model) {
        return null;
    }
}
