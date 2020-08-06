package dev.ruivieira.counterfactual.op.bmi;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

@PlanningEntity
public class BMIEntity {
    private Double height;
    private Double weight;

    private BMIEntity() {
    }

    public BMIEntity(Double height , Double weight) {

        this.height = height;
        this.weight = weight;
    }

    @PlanningVariable(valueRangeProviderRefs = { "heightRange" })
    public Double getHeight() {
        return height;
    }

    public void setHeight(Double height) {

        this.height = height;
    }

    @PlanningVariable(valueRangeProviderRefs = { "weightRange" })
    public Double getWeight() {
        return weight;
    }

    public void setWeight(Double weight) {
        this.weight = weight;
    }
}
