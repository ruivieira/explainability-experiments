package dev.ruivieira.counterfactual.op.bmi;

import com.redhat.developer.model.Feature;
import com.redhat.developer.model.FeatureFactory;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BMICounterFactual {
  public static final String SOLVER_CONFIG_XML = "BMISolverConfig.xml";
  private final Feature goal;
  private final BMIEntity solution;

  public BMICounterFactual(List<Feature> context, Feature goal) {
    this.goal = goal;
    SolverConfig solverConfig = SolverConfig.createFromXmlResource(SOLVER_CONFIG_XML);

    Map<String, String> contextMap = new HashMap<>();

    contextMap.put(goal.getName(), goal.getValue().asString());

    for (Feature feature : context) {
      contextMap.put(feature.getName(), feature.getValue().asString());
    }

    solverConfig.getScoreDirectorFactoryConfig().setEasyScoreCalculatorCustomProperties(contextMap);

    final SolverFactory<BMISolution> solverFactory = SolverFactory.create(solverConfig);

    Solver<BMISolution> solver = solverFactory.buildSolver();

    BMISolution unsolvedBMISolution = new BMISolution();

    BMISolution solved = solver.solve(unsolvedBMISolution);

    solution = solved.getTaskList().get(0);
  }

  public String getCounterfactual() {
    StringBuilder builder = new StringBuilder();
    builder.append("In order to have a BMI of ").append(goal.getValue().asString()).append(", ");
    builder.append("you need a height of ").append(solution.getHeight()).append(" and ");
    builder.append("a weight of ").append(solution.getWeight()).append(".");
    return builder.toString();
  }

  public static void main(String[] args) {
    final List<Feature> context = new ArrayList<>();
    context.add(FeatureFactory.newNumericalFeature("height", 174.0));
    context.add(FeatureFactory.newNumericalFeature("weight", 92.0));

    final Feature goal = FeatureFactory.newNumericalFeature("bmi", 22.0);

    BMICounterFactual conterfactual = new BMICounterFactual(context, goal);

    System.out.println(conterfactual.getCounterfactual());
  }
}
