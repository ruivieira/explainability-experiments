package dev.ruivieira.counterfactual;

import com.redhat.developer.model.*;
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
    private PointValuePair optimum;

    public PointValuePair getOptimum() {
        return optimum;
    }

    public OptimizableModel(List<FeatureType> featureTypes, Model model, Feature goal) {
        this.featureTypes = featureTypes;
        this.N = featureTypes.size();
        this.model = model;
        this.goal = goal;

        // build optimizer
        SimplexOptimizer optimizer = new SimplexOptimizer(1e-10, 1e-30);

        // build initial points
        final double[] initialGuess = featureTypes.stream()
                .filter(featureType -> !featureType.isConstrained())
                .map(featureType -> featureType.getValue().asNumber())
                .mapToDouble(Double::doubleValue)
                .toArray();
        final double[] steps = Arrays
                .stream(initialGuess).map(x -> 0.2)
                .toArray();

        System.out.println(Arrays.toString(initialGuess));
        System.out.println(Arrays.toString(steps));

        this.optimum =
                optimizer.optimize(
                        new MaxEval(10000),
                        new ObjectiveFunction(this),
                        GoalType.MINIMIZE,
                        new InitialGuess(initialGuess),
                        new NelderMeadSimplex(steps));
    }

    private List<Feature> buildFeatures(double[] values) {

        final List<Feature> features = new ArrayList<>();
        int index = 0;

        for (int i = 0 ; i < N ; i++) {
            final FeatureType feature = featureTypes.get(i);
            if (feature.isConstrained()) {
                features.add(FeatureFactory.newNumericalFeature(feature.getName(), feature.getValue().asNumber()));
            } else {
                features.add(FeatureFactory.newNumericalFeature(feature.getName(), values[index]));
                index++;
            }
        }
        return features;
    }

    public String getCounterfactual() {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        builder.append("In order to have a ").append(goal.getName()).append(" of ").append(goal.getValue().asNumber());
        builder.append(", you would need to have ");
        for (FeatureType feature : featureTypes) {
            if (feature.isConstrained()) {
                builder.append(" current ").append(feature.getName());
            } else {
                builder.append(feature.getName()).append(" of ").append(optimum.getFirst()[index]);
                index++;
            }
            builder.append(", ");
        }
    return builder.toString();
    }

    @Override
    public double value(double[] point) {
        final List<Feature> features = buildFeatures(point);

        final List<PredictionInput> inputs = new ArrayList<>();
        inputs.add(new PredictionInput(features));

        final List<PredictionOutput> predictions = model.predict(inputs);

        return Math.abs(this.goal.getValue().asNumber() - predictions.get(0).getOutputs().get(0).getValue().asNumber());
    }

  public static void main(String[] args) throws JAXBException, SAXException, IOException {
      BMIModel model = new BMIModel();

      final List<FeatureType> context = new ArrayList<>();

      context.add(new FeatureType("height",
              Type.NUMBER,
              new Value<>(174.0),
              true));
      context.add(new FeatureType("weight",
              Type.NUMBER,
              new Value<>(92.0),
              false));

      final Feature goal = FeatureFactory.newNumericalFeature("bmi", 25.0);

      OptimizableModel optimizer = new OptimizableModel(context, model, goal);

      System.out.println(optimizer.getCounterfactual());
  }
}