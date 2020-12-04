/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.developer.resources;

import com.redhat.developer.counterfactual.models.CreditCardApprovalModel;
import com.redhat.developer.counterfactual.payloads.input.CreditCardApprovalEntity;
import com.redhat.developer.counterfactual.payloads.input.Payload;
import com.redhat.developer.counterfactual.payloads.output.Counterfactual;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualConfigurationFactory;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualExplainer;
import org.kie.kogito.explainability.local.counterfactual.entities.CounterfactualEntity;
import org.kie.kogito.explainability.model.*;
import org.optaplanner.core.config.solver.SolverConfig;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

@Path("/creditcard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApprovalResource {

    private static final int steps = 100000;
    final long predictionTimeOut = 10L;
    final TimeUnit predictionTimeUnit = TimeUnit.MINUTES;

    @POST
    @Path("/prediction")
    public Output prediction(CreditCardApprovalEntity entity) throws InterruptedException, ExecutionException, TimeoutException {
        List<Feature> features = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("AMT_INCOME_TOTAL", entity.income.value));

        features.add(FeatureFactory.newNumericalFeature("CNT_CHILDREN", entity.children.value));

        features.add(FeatureFactory.newNumericalFeature("DAYS_EMPLOYED", entity.daysEmployed.value));

        features.add(FeatureFactory.newNumericalFeature("AGE", entity.age.value));

        features.add(FeatureFactory.newBooleanFeature("FLAG_OWN_CAR", entity.ownCar.value));

        features.add(FeatureFactory.newBooleanFeature("FLAG_OWN_REALTY", entity.ownRealty.value));

        features.add(FeatureFactory.newBooleanFeature("FLAG_WORK_PHONE", entity.workPhone.value));

        PredictionInput input = new PredictionInput(features);
        PredictionOutput output = CreditCardApprovalModel.getModel().predictAsync(List.of(input))
                .get(predictionTimeOut, predictionTimeUnit).get(0);

        return output.getOutputs().get(0);

    }

    @POST
    @Path("/counterfactual")
    public Counterfactual counterfactual(Payload payload) throws InterruptedException, ExecutionException, TimeoutException {
        System.out.println(payload);

        final List<Output> goal = List.of(new Output("approved", Type.BOOLEAN, new Value<>(payload.goal.output.approved), payload.goal.output.confidence));

        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();

        features.add(FeatureFactory.newNumericalFeature("AMT_INCOME_TOTAL", payload.input.income.value));
        constraints.add(payload.input.income.constrain);
        featureBoundaries.add(FeatureDomain.numerical(payload.input.income.min, payload.input.income.max));

        features.add(FeatureFactory.newNumericalFeature("CNT_CHILDREN", payload.input.children.value));
        constraints.add(payload.input.children.constrain);
        featureBoundaries.add(FeatureDomain.numerical(payload.input.children.min, payload.input.children.max));

        features.add(FeatureFactory.newNumericalFeature("DAYS_EMPLOYED", payload.input.daysEmployed.value));
        constraints.add(payload.input.daysEmployed.constrain);
        featureBoundaries.add(FeatureDomain.numerical(payload.input.daysEmployed.min, payload.input.daysEmployed.max));

        features.add(FeatureFactory.newNumericalFeature("AGE", payload.input.age.value));
        constraints.add(payload.input.age.constrain);
        featureBoundaries.add(FeatureDomain.numerical(payload.input.age.min, payload.input.age.max));

        features.add(FeatureFactory.newBooleanFeature("FLAG_OWN_CAR", payload.input.ownCar.value));
        constraints.add(payload.input.ownCar.constrain);
        featureBoundaries.add(null);

        features.add(FeatureFactory.newBooleanFeature("FLAG_OWN_REALTY", payload.input.ownRealty.value));
        constraints.add(payload.input.ownRealty.constrain);
        featureBoundaries.add(null);

        features.add(FeatureFactory.newBooleanFeature("FLAG_WORK_PHONE", payload.input.workPhone.value));
        constraints.add(payload.input.ownRealty.constrain);
        featureBoundaries.add(null);

        final DataDomain dataDomain = new DataDomain(featureBoundaries);

        PredictionInput input = new PredictionInput(features);
        PredictionOutput output = CreditCardApprovalModel.getModel().predictAsync(List.of(input))
                .get(predictionTimeOut, predictionTimeUnit).get(0);

        final Prediction prediction = new Prediction(input, output);
        final SolverConfig solverConfig = CounterfactualConfigurationFactory
                .builder().withScoreCalculationCountLimit(steps).build();
        final CounterfactualExplainer counterfactualExplainer =
                CounterfactualExplainer
                        .builder(goal, constraints, dataDomain)
                        .withSolverConfig(solverConfig)
                        .build();

        List<CounterfactualEntity> counterfactualEntities = counterfactualExplainer.explainAsync(prediction, CreditCardApprovalModel.getModel())
                .get(predictionTimeOut, predictionTimeUnit);

        Counterfactual cf = new Counterfactual();
        cf.features = counterfactualEntities.stream().map(CounterfactualEntity::asFeature).collect(Collectors.toList());
        cf.prediction = null;

        PredictionInput inputPred = new PredictionInput(cf.features);
        PredictionOutput outputPred = CreditCardApprovalModel.getModel().predictAsync(List.of(inputPred)).get(predictionTimeOut, predictionTimeUnit).get(0);

        cf.prediction = outputPred.getOutputs().get(0);

        return cf;
    }
}
