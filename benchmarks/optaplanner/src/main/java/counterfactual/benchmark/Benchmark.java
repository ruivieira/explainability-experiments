package counterfactual.benchmark;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.kie.kogito.explainability.Config;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualConfigurationFactory;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualExplainer;
import org.kie.kogito.explainability.local.counterfactual.CounterfactualResult;
import org.kie.kogito.explainability.model.DataDomain;
import org.kie.kogito.explainability.model.Feature;
import org.kie.kogito.explainability.model.FeatureDomain;
import org.kie.kogito.explainability.model.FeatureFactory;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.Prediction;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;

public class Benchmark {

    final static long predictionTimeOut = 10L;
    final static TimeUnit predictionTimeUnit = TimeUnit.MINUTES;


    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        final Model model = Model.getModel();

        final List<Feature> testFeatures = List.of(
                FeatureFactory.newNumericalFeature("Age", 21.0),
                FeatureFactory.newNumericalFeature("Debt", 0.1),
                FeatureFactory.newNumericalFeature("YearsEmployed", 0.1),
                FeatureFactory.newNumericalFeature("Income", 100.0)
        );

        final List<PredictionInput> inputs = List.of(new PredictionInput(testFeatures));

        System.out.println(model.predict(inputs));

        // Find counterfactual
        final List<Output> goal = List.of(new Output("Approved", Type.NUMBER, new Value<>(1.0), 0.0d));

        List<Feature> features = new LinkedList<>();
        List<FeatureDomain> featureBoundaries = new LinkedList<>();
        List<Boolean> constraints = new LinkedList<>();
        features.add(FeatureFactory.newNumericalFeature("Age", 21.0));
        featureBoundaries.add(FeatureDomain.numerical(18.0, 65.0));
        constraints.add(false);
        features.add(FeatureFactory.newNumericalFeature("Debt", 0.1));
        featureBoundaries.add(FeatureDomain.numerical(0.0, 10.0));
        constraints.add(false);
        features.add(FeatureFactory.newNumericalFeature("YearsEmployed", 0.1));
        featureBoundaries.add(FeatureDomain.numerical(0.0, 30.0));
        constraints.add(false);
        features.add(FeatureFactory.newNumericalFeature("Income", 100.0));
        featureBoundaries.add(FeatureDomain.numerical(0.0, 1000.0));
        constraints.add(false);
        final DataDomain dataDomain = new DataDomain(featureBoundaries);

        final TerminationConfig terminationConfig = new TerminationConfig().withSecondsSpentLimit(10L);
        final SolverConfig solverConfig = CounterfactualConfigurationFactory
                .builder().withTerminationConfig(terminationConfig).build();
        final CounterfactualExplainer explainer = CounterfactualExplainer
                .builder(goal, constraints, dataDomain)
                .withSolverConfig(solverConfig)
                .build();
        final PredictionInput input = new PredictionInput(features);
        PredictionOutput output = model.predictAsync(List.of(input))
                .get(Config.INSTANCE.getAsyncTimeout(), Config.INSTANCE.getAsyncTimeUnit())
                .get(0);
        Prediction prediction = new Prediction(input, output);
        final CounterfactualResult result = explainer.explainAsync(prediction, model)
                .get(predictionTimeOut, predictionTimeUnit);
        System.out.println(result.getEntities());

    }
}
