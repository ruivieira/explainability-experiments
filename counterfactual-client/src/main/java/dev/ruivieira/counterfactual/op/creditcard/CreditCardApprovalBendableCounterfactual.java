package dev.ruivieira.counterfactual.op.creditcard;

import com.redhat.developer.model.*;
import com.redhat.developer.xai.LocalExplainer;
import dev.ruivieira.counterfactual.CounterfactualUtils;
import dev.ruivieira.counterfactual.op.creditcard.entities.CreditCardApprovalEntity;
import dev.ruivieira.counterfactual.op.creditcard.solution.CreditCardApprovalBendableSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CreditCardApprovalBendableCounterfactual implements LocalExplainer<Counterfactual> {
  public static final String SOLVER_CONFIG_XML = "CreditCardApprovalBendableSolverConfig.xml";
  private static final Logger LOGGER = Logger.getLogger(CreditCardApprovalBendableCounterfactual.class.getName());
  private final Feature goal;
  private final CreditCardApprovalEntity solution;
  private List<Feature> counterfactual;

  public CreditCardApprovalBendableCounterfactual(List<Feature> context, Feature goal) {
    this.goal = goal;
    SolverConfig solverConfig = SolverConfig.createFromXmlResource(SOLVER_CONFIG_XML);

    Map<String, String> contextMap = new HashMap<>();

//    contextMap.put(goal.getName(), goal.getValue().asString());

    for (Feature feature : context) {
      contextMap.put(feature.getName(), feature.getValue().asString());
    }

    solverConfig.getScoreDirectorFactoryConfig().setEasyScoreCalculatorCustomProperties(contextMap);

    final SolverFactory<CreditCardApprovalBendableSolution> solverFactory = SolverFactory.create(solverConfig);

    Solver<CreditCardApprovalBendableSolution> solver = solverFactory.buildSolver();

    CreditCardApprovalBendableSolution unsolvedSolution = new CreditCardApprovalBendableSolution();

    CreditCardApprovalBendableSolution solved = solver.solve(unsolvedSolution);

    solution = solved.getApprovalsList().get(0);

    // create feature counterfactuals
    counterfactual = new ArrayList<>();
    final Feature age = FeatureFactory.newNumericalFeature("age", solution.getAge());
    final Feature income = FeatureFactory.newNumericalFeature("income", solution.getIncome());
    final Feature children = FeatureFactory.newNumericalFeature("children", solution.getChildren());
    final Feature daysEmployed = FeatureFactory.newNumericalFeature("daysEmployed", solution.getDaysEmployed());
    final Feature ownRealty = FeatureFactory.newBooleanFeature("ownRealty", solution.getOwnRealty());
    final Feature workPhone = FeatureFactory.newBooleanFeature("workPhone", solution.getWorkPhone());
    final Feature ownCar = FeatureFactory.newBooleanFeature("ownCar", solution.getOwnCar());

    counterfactual.add(age);
    counterfactual.add(income);
    counterfactual.add(children);
    counterfactual.add(daysEmployed);
    counterfactual.add(ownRealty);
    counterfactual.add(workPhone);
    counterfactual.add(ownCar);
  }

  public static void main(String[] args) {
    final List<Feature> context = new ArrayList<>();

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

    CreditCardApprovalBendableCounterfactual counterfactual = new CreditCardApprovalBendableCounterfactual(context, goal);
    System.out.println(CounterfactualUtils.getCounterfactual(counterfactual.getCounterfactual(), goal));

  }

  public List<Feature> getCounterfactual() {
    return counterfactual;
  }

  public CreditCardApprovalEntity getSolution() {
    return solution;
  }

  @Override
  public Counterfactual explain(Prediction prediction, Model model) {
    return null;
  }
}
