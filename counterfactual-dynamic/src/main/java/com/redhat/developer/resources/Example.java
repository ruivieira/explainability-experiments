package com.redhat.developer.resources;

import com.redhat.developer.counterfactual.entities.BooleanFeature;
import com.redhat.developer.counterfactual.entities.CounterfactualEntity;
import com.redhat.developer.counterfactual.entities.DoubleFeature;
import com.redhat.developer.counterfactual.entities.IntegerFeature;
import com.redhat.developer.counterfactual.models.CreditCardApprovalModel;
import com.redhat.developer.counterfactual.solutions.CounterfactualSolution;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class Example {
    public static void main(String[] args) {

        UUID problemId = UUID.randomUUID();
        SolverConfig solverConfig = SolverConfig.createFromXmlResource("SolverConfig.xml");
        solverConfig.withEntityClasses(IntegerFeature.class, DoubleFeature.class, BooleanFeature.class);

        SolverManager<CounterfactualSolution, UUID> solverManager =
                SolverManager.create(solverConfig, new SolverManagerConfig());

        List<CounterfactualEntity> entities = new ArrayList<>();

        final IntegerFeature income = new IntegerFeature(FeatureFactory.newNumericalFeature("income", 50000), 0, 1000000);
        entities.add(income);

        final IntegerFeature children = new IntegerFeature(FeatureFactory.newNumericalFeature("children", 0), 0, 20, true);
        entities.add(children);

        final IntegerFeature dayEmployed = new IntegerFeature(FeatureFactory.newNumericalFeature("daysEmployed", 100), 0, 18250);
        entities.add(dayEmployed);

        final IntegerFeature age = new IntegerFeature(FeatureFactory.newNumericalFeature("age", 20), 18, 100, true);
        entities.add(age);

        final BooleanFeature ownCar = new BooleanFeature(FeatureFactory.newBooleanFeature("ownCar", true));
        entities.add(ownCar);

        final BooleanFeature ownRealty = new BooleanFeature(FeatureFactory.newBooleanFeature("ownRealty", false));
        entities.add(ownRealty);

        final BooleanFeature workPhone = new BooleanFeature(FeatureFactory.newBooleanFeature("workPhone", true));
        entities.add(workPhone);

        final Feature goal = FeatureFactory.newNumericalFeature("approved", 1);

        CounterfactualSolution problem =
                new CounterfactualSolution(entities, CreditCardApprovalModel.getModel(), goal);


        SolverJob<CounterfactualSolution, UUID> solverJob = solverManager.solve(problemId, problem);
        CounterfactualSolution solution;
        try {
            // Wait until the solving ends
            solution = solverJob.getFinalBestSolution();
            System.out.println(solution.toString());
        } catch (InterruptedException | ExecutionException e) {
            throw new IllegalStateException("Solving failed.", e);
        }

        System.out.println("The counterfactual is:");
        for (CounterfactualEntity cfEntity : solution.getEntities()) {
            System.out.println(cfEntity.asFeature().toString());
        }
    }
}
