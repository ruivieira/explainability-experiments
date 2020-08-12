package dev.ruivieira.counterfactual;

import com.redhat.developer.model.Feature;

import java.util.List;

public class CounterfactualUtils {
    public static String getCounterfactual(List<Feature> features, Feature goal) {
        StringBuilder builder = new StringBuilder();
        builder.append("In order to have a ").append(goal.getName()).append(" of ").append(goal.getValue().asNumber());
        builder.append(", you would need to have:\n");
        for (Feature feature : features) {
            builder.append("\t").append(feature.getName()).append(" of ").append(feature.getValue().asString()).append("\n");
        }
        return builder.toString();
    }
}
