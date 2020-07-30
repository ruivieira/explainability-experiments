package dev.ruivieira.counterfactual.op.bmi;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.valuerange.ValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.api.score.buildin.simplebigdecimal.SimpleBigDecimalScore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@PlanningSolution
public class BMISolution {
    List<BMIEntity> bmis = new ArrayList<>();
    List<Double> bmiHeights = new ArrayList<>();
    List<Double> bmiWeights = new ArrayList<>();
    private final int MAX_HEIGHT = 200;
    private final int MAX_WEIGHT = 200;
    private HardSoftBigDecimalScore score;

    public BMISolution() {
        bmis.add(new BMIEntity(Math.random()*MAX_HEIGHT, Math.random()*MAX_WEIGHT));
        for (int i = 0; i < 100; i++) {
            bmiWeights.add(Math.random()*MAX_WEIGHT);
            bmiHeights.add(Math.random()*MAX_HEIGHT);
        }
    }

    @PlanningEntityCollectionProperty
    public List<BMIEntity> getTaskList() {
        return bmis;
    }

    @ValueRangeProvider(id = "heightRange")
    public ValueRange<Double> getHeightList() {
        return ValueRangeFactory.createDoubleValueRange(50, 250);
    }

    @ValueRangeProvider(id = "weightRange")
    public ValueRange<Double> getWeightList() {
        return ValueRangeFactory.createDoubleValueRange(5, 250);
    }

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<>();
        // nothing to add because the only facts are already added automatically
        // by planner
        return facts;
    }

    @PlanningScore
    public HardSoftBigDecimalScore getScore() {
        return score;
    }

    public void setScore(HardSoftBigDecimalScore score) {
        this.score = score;
    }

}
