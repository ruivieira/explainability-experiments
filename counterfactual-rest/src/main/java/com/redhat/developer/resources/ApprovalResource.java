package com.redhat.developer.resources;

import com.redhat.developer.counterfactual.entities.CreditCardApprovalEntity;
import com.redhat.developer.counterfactual.solutions.Approval;
import com.redhat.developer.model.Feature;
import com.redhat.developer.model.FeatureFactory;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Path("/creditcard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApprovalResource {
  @POST
  @Path("/counterfactual")
  public Approval counterfactual(CreditCardApprovalEntity entity) {

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
    return solution;
  }
}
