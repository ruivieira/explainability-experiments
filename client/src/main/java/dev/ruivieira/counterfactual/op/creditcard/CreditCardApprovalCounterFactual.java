package dev.ruivieira.counterfactual.op.creditcard;

import com.redhat.developer.model.Feature;
import com.redhat.developer.model.FeatureFactory;
import dev.ruivieira.counterfactual.op.bmi.BMIEntity;
import dev.ruivieira.counterfactual.op.bmi.BMISolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreditCardApprovalCounterFactual {
  public static final String SOLVER_CONFIG_XML = "CreditCardApprovalSolverConfig.xml";
  private final Feature goal;
  private final CreditCardApprovalEntity solution;

  public CreditCardApprovalCounterFactual(List<Feature> context, Feature goal) {
    this.goal = goal;
    SolverConfig solverConfig = SolverConfig.createFromXmlResource(SOLVER_CONFIG_XML);

    Map<String, String> contextMap = new HashMap<>();

//    contextMap.put(goal.getName(), goal.getValue().asString());

    for (Feature feature : context) {
      contextMap.put(feature.getName(), feature.getValue().asString());
    }

    solverConfig.getScoreDirectorFactoryConfig().setEasyScoreCalculatorCustomProperties(contextMap);

    final SolverFactory<CreditCardApprovalSolution> solverFactory = SolverFactory.create(solverConfig);

    Solver<CreditCardApprovalSolution> solver = solverFactory.buildSolver();

    CreditCardApprovalSolution unsolvedBMISolution = new CreditCardApprovalSolution();

    CreditCardApprovalSolution solved = solver.solve(unsolvedBMISolution);

    solution = solved.creditCardApprovals.get(0);
  }

  public String getCounterfactual() {
    StringBuilder builder = new StringBuilder();

    builder.append("In order to have an approved credit card, ");
    builder.append("you need: an age of ").append(solution.getAge()).append(", ");
    builder.append("an income of $").append(solution.getIncome()/1000.0).append("k, ");
    builder.append(solution.getChildren()).append(" children, ");
    builder.append("to be employed for ").append(solution.getDaysEmployed()).append(" days, ");
    if (solution.getOwnRealty()) {
      builder.append("to have your own house, ");
    }
    if (solution.getWorkPhone()) {
      builder.append("to have your own work phone, ");
    }
    if (solution.getOwnCar()) {
      builder.append("to have your own car.");
    }

    return builder.toString();
  }

  public static void main(String[] args) {
    final List<Feature> context = new ArrayList<>();

//    final Feature age = FeatureFactory.newNumericalFeature("age", 25);
//    final Feature income = FeatureFactory.newNumericalFeature("income", 40000);
//    final Feature children = FeatureFactory.newNumericalFeature("children", 3);
//    final Feature daysEmployed = FeatureFactory.newNumericalFeature("daysEmployed", 100);
//    final Feature ownRealty = FeatureFactory.newNumericalFeature("ownRealty", 0);
//    final Feature workPhone = FeatureFactory.newNumericalFeature("workPhone", 0);
//    final Feature ownCar = FeatureFactory.newNumericalFeature("ownCar", 0);

    final Feature age = FeatureFactory.newNumericalFeature("age", 51);
    final Feature income = FeatureFactory.newNumericalFeature("income", 40000);
    final Feature children = FeatureFactory.newNumericalFeature("children", 0);
    final Feature daysEmployed = FeatureFactory.newNumericalFeature("daysEmployed", 900);
    final Feature ownRealty = FeatureFactory.newNumericalFeature("ownRealty", 0);
    final Feature workPhone = FeatureFactory.newNumericalFeature("workPhone", 0);
    final Feature ownCar = FeatureFactory.newNumericalFeature("ownCar", 0);


    context.add(age);
    context.add(income);
    context.add(children);
    context.add(daysEmployed);
    context.add(ownRealty);
    context.add(workPhone);
    context.add(ownCar);

    final Feature goal = FeatureFactory.newNumericalFeature("APPROVED", 1.0);

    CreditCardApprovalCounterFactual conterfactual = new CreditCardApprovalCounterFactual(context, goal);

    System.out.println(conterfactual.getCounterfactual());
  }
}
