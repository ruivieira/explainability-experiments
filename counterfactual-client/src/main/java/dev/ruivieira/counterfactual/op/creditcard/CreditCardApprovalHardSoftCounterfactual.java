package dev.ruivieira.counterfactual.op.creditcard;

import com.redhat.developer.model.*;
import com.redhat.developer.xai.LocalExplainer;
import dev.ruivieira.counterfactual.CounterfactualUtils;
import dev.ruivieira.counterfactual.models.CreditCardApprovalModel;
import dev.ruivieira.counterfactual.op.creditcard.entities.CreditCardApprovalEntity;
import dev.ruivieira.counterfactual.op.creditcard.solution.CreditCardApprovalHardSoftSolution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.solver.SolverConfig;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CreditCardApprovalHardSoftCounterfactual implements LocalExplainer<Counterfactual> {
  public static final String SOLVER_CONFIG_XML = "CreditCardApprovalHardSoftSolverConfig.xml";
  private static final Logger LOGGER = Logger.getLogger(CreditCardApprovalHardSoftCounterfactual.class.getName());
  private final Feature goal;
  private final CreditCardApprovalEntity solution;
  private List<Feature> counterfactual;

  public CreditCardApprovalHardSoftCounterfactual(List<Feature> context, Feature goal) {
    this.goal = goal;
    SolverConfig solverConfig = SolverConfig.createFromXmlResource(SOLVER_CONFIG_XML);

    Map<String, String> contextMap = new HashMap<>();

//    contextMap.put(goal.getName(), goal.getValue().asString());

    for (Feature feature : context) {
      contextMap.put(feature.getName(), feature.getValue().asString());
    }

    solverConfig.getScoreDirectorFactoryConfig().setEasyScoreCalculatorCustomProperties(contextMap);

    final SolverFactory<CreditCardApprovalHardSoftSolution> solverFactory = SolverFactory.create(solverConfig);

    Solver<CreditCardApprovalHardSoftSolution> solver = solverFactory.buildSolver();

    CreditCardApprovalHardSoftSolution unsolvedSolution = new CreditCardApprovalHardSoftSolution();

    CreditCardApprovalHardSoftSolution solved = solver.solve(unsolvedSolution);

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

  public static void main(String[] args) throws JAXBException, SAXException, IOException {

    CreditCardApprovalEntity entity = new CreditCardApprovalEntity(30.0, 5000, 0, 100, true, true, true);

    final List<Feature> context = entity.buildFeatures();

    final Feature goal = FeatureFactory.newNumericalFeature("APPROVED", 1.0);

    CreditCardApprovalModel model = new CreditCardApprovalModel();

    CreditCardApprovalHardSoftCounterfactual counterfactual = new CreditCardApprovalHardSoftCounterfactual(context, goal);
    System.out.println(CounterfactualUtils.getCounterfactual(counterfactual.getCounterfactual(), goal));


    List<PredictionInput> inputs = new ArrayList<>();
    PredictionInput input = new PredictionInput(counterfactual.getCounterfactual());
    inputs.add(input);
    List<PredictionOutput> outputs = model.predict(inputs);
    PredictionOutput output = outputs.get(0);
    System.out.println("Prediction: " + output.getOutputs().get(0).getValue());

    inputs = new ArrayList<>();
    input = new PredictionInput(context);
    inputs.add(input);
    outputs = model.predict(inputs);
    output = outputs.get(0);
    System.out.println("Original: " + output.getOutputs().get(0).getValue());

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
