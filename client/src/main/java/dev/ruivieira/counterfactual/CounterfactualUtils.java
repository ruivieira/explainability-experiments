package dev.ruivieira.counterfactual;

import com.redhat.developer.model.Feature;

import java.util.List;

public class CounterfactualUtils {
    public static String getCounterfactual(List<Feature> features, Feature goal) {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        builder.append("In order to have a ").append(goal.getName()).append(" of ").append(goal.getValue().asNumber());
        builder.append(", you would need to have ");
        for (Feature feature : features) {
                builder.append(feature.getName()).append(" of ").append(feature.getValue().asString());
            builder.append(", ");
        }
        return builder.toString();
    }
}
