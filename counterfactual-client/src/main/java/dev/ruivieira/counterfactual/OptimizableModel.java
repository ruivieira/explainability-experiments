package dev.ruivieira.counterfactual;

import com.redhat.developer.model.*;
import dev.ruivieira.counterfactual.models.BMIModel;
import org.apache.commons.math3.analysis.MultivariateFunction;
import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OptimizableModel implements MultivariateFunction {

  private final List<FeatureType> featureTypes;
  private final int N;
  private final Model model;
  private final Feature goal;
  private final double step;
  private final double[] initialGuess;
  private final double lambda;
  private final int maxIterations;
  private final double relativeThreshold;
  private final double absoluteThreshold;
  private List<Feature> counterfactual;

  public OptimizableModel(List<FeatureType> featureTypes, Model model, Feature goal) {
    this(featureTypes, model, goal, 1.0, 0.2, 10000);
  }

  public OptimizableModel(
      List<FeatureType> featureTypes,
      Model model,
      Feature goal,
      double lambda,
      double step,
      int maxIterations) {
    this(featureTypes, model, goal, lambda, step, maxIterations, 1e-10, 1e-30);
  }

  public OptimizableModel(
      List<FeatureType> featureTypes,
      Model model,
      Feature goal,
      double lambda,
      double step,
      int maxIterations,
      double relativeThreshold,
      double absoluteThreshold) {

    this.featureTypes = featureTypes;
    this.N = featureTypes.size();
    this.model = model;
    this.goal = goal;
    this.step = step;
    this.lambda = lambda;
    this.maxIterations = maxIterations;
    this.relativeThreshold = relativeThreshold;
    this.absoluteThreshold = absoluteThreshold;

    // build optimizer
    SimplexOptimizer optimizer =
        new SimplexOptimizer(this.relativeThreshold, this.absoluteThreshold);

    // build initial points
    this.initialGuess =
        featureTypes.stream()
            .filter(featureType -> !featureType.isConstrained())
            .map(featureType -> featureType.getValue().asNumber())
            .mapToDouble(Double::doubleValue)
            .toArray();
    final double[] steps = Arrays.stream(this.initialGuess).map(x -> this.step).toArray();

    System.out.println(Arrays.toString(initialGuess));
    System.out.println(Arrays.toString(steps));

    PointValuePair optimum =
        optimizer.optimize(
            new MaxEval(this.maxIterations),
            new ObjectiveFunction(this),
            GoalType.MINIMIZE,
            new InitialGuess(initialGuess),
            new NelderMeadSimplex(steps));

    this.counterfactual = this.buildFeatures(optimum.getFirst());
  }

  public static double[] featureToArray(List<Feature> features) {
    return features.stream()
        .map(f -> f.getValue().asNumber())
        .mapToDouble(Double::doubleValue)
        .toArray();
  }

  public static void main(String[] args) throws JAXBException, SAXException, IOException {
    BMIModel model = new BMIModel();

    final List<FeatureType> context = new ArrayList<>();

    context.add(new FeatureType("height", Type.NUMBER, new Value<>(174.0), true));
    context.add(new FeatureType("weight", Type.NUMBER, new Value<>(92.0), false));

    final Feature goal = FeatureFactory.newNumericalFeature("bmi", 22.0);

    OptimizableModel optimizer = new OptimizableModel(context, model, goal, 1.0, 0.2, 10000);

    System.out.println(CounterfactualUtils.getCounterfactual(optimizer.getCounterfactual(), goal));
  }

  private List<Feature> buildFeatures(double[] values) {

    final List<Feature> features = new ArrayList<>();
    int index = 0;

    for (int i = 0; i < N; i++) {
      final FeatureType feature = featureTypes.get(i);
      if (feature.isConstrained()) {
        features.add(
            FeatureFactory.newNumericalFeature(feature.getName(), feature.getValue().asNumber()));
      } else {
        features.add(FeatureFactory.newNumericalFeature(feature.getName(), values[index]));
        index++;
      }
    }
    return features;
  }

  @Override
  public double value(double[] point) {
    final List<Feature> features = buildFeatures(point);

    final List<PredictionInput> inputs = new ArrayList<>();
    inputs.add(new PredictionInput(features));

    final List<PredictionOutput> predictions = model.predict(inputs);

    final double predictionDistance =
        Math.pow(
            this.goal.getValue().asNumber()
                - predictions.get(0).getOutputs().get(0).getValue().asNumber(),
            2);

    final double inputDistance = Measures.manhattan(initialGuess, featureToArray(features));

    return lambda * predictionDistance + inputDistance;
  }

  public List<Feature> getCounterfactual() {
    return counterfactual;
  }
}
