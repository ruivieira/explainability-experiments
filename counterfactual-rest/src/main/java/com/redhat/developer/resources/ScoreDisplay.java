package com.redhat.developer.resources;

import com.redhat.developer.counterfactual.entities.CreditCardApprovalEntity;
import com.redhat.developer.counterfactual.metrics.Metrics;
import com.redhat.developer.counterfactual.solutions.Approval;
import com.redhat.developer.model.Feature;
import com.redhat.developer.model.FeatureFactory;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Path("/creditcard")
public class ScoreDisplay {

  @Inject Template plots;

  @Produces(MediaType.APPLICATION_JSON)
  @GET
  @Path("/metrics.json")
  public Map<String, Object> getMetrics() {
    Map<String, Object> result = new HashMap<>();
    result.put("metrics", Metrics.metrics);
    result.put("counterfactual", Metrics.counterfactual);
    result.put("original", Metrics.original);
    return result;
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  @Path("/calculate")
  public String calculate() {
    Metrics.metrics.clear();
    CreditCardApprovalEntity entity =
        new CreditCardApprovalEntity(30.0, 10000, 2, 300, true, false, false);
    Metrics.original = entity;

    final List<Feature> context = entity.buildFeatures();

    final Feature goal = FeatureFactory.newNumericalFeature("APPROVED", 1.0);

    Map<String, String> contextMap = new HashMap<>();

    //    contextMap.put(goal.getName(), goal.getValue().asString());

    for (Feature feature : context) {
      contextMap.put(feature.getName(), feature.getValue().asString());
    }

    UUID problemId = UUID.randomUUID();
    // Submit the problem to start solving

    SolverConfig solverConfig = SolverConfig.createFromXmlResource("SolverConfig.xml");

    solverConfig.getScoreDirectorFactoryConfig().setEasyScoreCalculatorCustomProperties(contextMap);

    SolverManager<Approval, UUID> solverManager =
        SolverManager.create(solverConfig, new SolverManagerConfig());

    Approval problem = new Approval();

    SolverJob<Approval, UUID> solverJob = solverManager.solve(problemId, problem);
    Approval solution;
    try {
      // Wait until the solving ends
      solution = solverJob.getFinalBestSolution();
    } catch (InterruptedException | ExecutionException e) {
      throw new IllegalStateException("Solving failed.", e);
    }
    Metrics.counterfactual = solution.getApprovalsList().get(0);
    return "done";
  }

  @Produces(MediaType.TEXT_HTML)
  @GET
  @Path("/plots")
  public TemplateInstance get() {
    calculate();
    return plots.instance();
  }
}
