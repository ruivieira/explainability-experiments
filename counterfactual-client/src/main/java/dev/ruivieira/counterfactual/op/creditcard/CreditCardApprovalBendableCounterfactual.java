package dev.ruivieira.counterfactual.op.creditcard;

import com.redhat.developer.model.Feature;
import com.redhat.developer.model.FeatureFactory;
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

public class CreditCardApprovalBendableCounterfactual {
  //  public static final String SOLVER_CONFIG_XML = "CreditCardApprovalBendableSolverConfig.xml";
  public static final String SOLVER_CONFIG_XML = "CCAClosestMoveBendableSolverConfig.xml";
//  public static final String SOLVER_CONFIG_XML = "CCAFilteredMoveBendableSolverConfig.xml";

  protected final CreditCardApprovalEntity solution;
  protected final Feature goal;

  private List<Feature> counterfactual;

  public CreditCardApprovalBendableCounterfactual(List<Feature> context, Feature goal) {
    this.goal = goal;

    Map<String, String> contextMap = new HashMap<>();

    //    contextMap.put(goal.getName(), goal.getValue().asString());

    for (Feature feature : context) {
      contextMap.put(feature.getName(), feature.getValue().asString());
    }
    SolverConfig solverConfig = SolverConfig.createFromXmlResource(SOLVER_CONFIG_XML);
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
    CreditCardApprovalEntity entity = new CreditCardApprovalEntity(30.0, 5000, 0, 100, true, true, true);

    final List<Feature> context = entity.buildFeatures();

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

}
