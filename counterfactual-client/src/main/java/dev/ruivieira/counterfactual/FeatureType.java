package dev.ruivieira.counterfactual;

import com.redhat.developer.model.Type;
import com.redhat.developer.model.Value;

public class FeatureType {
    private final String name;
    private final Type type;
    private final Value value;
    private final Boolean constrained;

    public FeatureType(String name, Type type, Value value, Boolean constrained) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.constrained = constrained;
    }

    public Boolean isConstrained() {
        return constrained;
    }

    public String getName() {
        return this.name;
    }

    public Type getType() {
        return this.type;
    }

    public Value getValue() {
        return this.value;
    }

    public String toString() {
        return "Feature{name='" + this.name + "', type=" + this.type + ", value=" + this.value + "}";
    }
}
