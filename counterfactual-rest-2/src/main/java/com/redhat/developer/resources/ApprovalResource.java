package com.redhat.developer.resources;

import com.redhat.developer.counterfactual.entities.CreditCardApprovalEntity;
import com.redhat.developer.counterfactual.metrics.Facts;
import com.redhat.developer.counterfactual.metrics.ScoreBreakdown;
import com.redhat.developer.counterfactual.models.CreditCardApprovalModel;
import com.redhat.developer.counterfactual.solutions.Approval;
import com.redhat.developer.model.Feature;
import com.redhat.developer.model.PredictionInput;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;
import org.optaplanner.core.api.score.constraint.ConstraintMatch;
import org.optaplanner.core.api.score.constraint.Indictment;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverJob;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactory;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

@Path("/creditcard")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class ApprovalResource {

  @POST
  @Path("/prediction")
  public Map<String, ?> prediction(CreditCardApprovalEntity entity) {
    final List<Feature> context = entity.buildFeatures();

    PredictionInput input = new PredictionInput(context);
    List<PredictionInput> inputs = new ArrayList<>();
    inputs.add(input);

    var output = CreditCardApprovalModel.getModel().predict(inputs);

    return output;
  }

  private Approval calculateCounterfactual(CreditCardApprovalEntity entity) {
    Facts.input = entity;

    UUID problemId = UUID.randomUUID();
    SolverConfig solverConfig = SolverConfig.createFromXmlResource("SolverConfig.xml");

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

  @POST
  @Path("/counterfactual")
  public Approval counterfactual(CreditCardApprovalEntity entity) {

    return calculateCounterfactual(entity);
  }

  @POST
  @Path("/breakdown")
  public ScoreBreakdown breakdown(CreditCardApprovalEntity entity) {

    Facts.input = entity;

    UUID problemId = UUID.randomUUID();
    SolverConfig solverConfig = SolverConfig.createFromXmlResource("SolverConfig.xml");

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

    ScoreBreakdown scoreBreakdown = new ScoreBreakdown();

    SolverFactory<Approval> solverFactory = SolverFactory.create(solverConfig);
    ScoreDirectorFactory<Approval> scoreDirectorFactory = solverFactory.getScoreDirectorFactory();
    ScoreDirector<Approval> guiScoreDirector = scoreDirectorFactory.buildScoreDirector();
    guiScoreDirector.setWorkingSolution(solution);
    //      Collection<ConstraintMatchTotal> constraintMatchTotals =
    //              guiScoreDirector.getConstraintMatchTotals();
    //      for (ConstraintMatchTotal constraintMatchTotal : constraintMatchTotals) {
    //        String constraintName = constraintMatchTotal.getConstraintName();
    //        // The score impact of that constraint
    //        BendableBigDecimalScore totalScore =
    //                (BendableBigDecimalScore) constraintMatchTotal.getScore();
    //        System.out.println(totalScore.toString());
    //        for (ConstraintMatch constraintMatch : constraintMatchTotal.getConstraintMatchSet()) {
    //          List<Object> justificationList = constraintMatch.getJustificationList();
    //          BendableBigDecimalScore score = (BendableBigDecimalScore)
    // constraintMatch.getScore();
    //          System.out.println(score.toString());
    //        }
    //      }

    Map<Object, Indictment> indictmentMap = guiScoreDirector.getIndictmentMap();
    for (CreditCardApprovalEntity process : solution.getApprovalsList()) {
      Indictment indictment = indictmentMap.get(process);
      if (indictment == null) {
        continue;
      }

      System.out.println(indictment.toString());
      // The score impact of that planning entity
      BendableBigDecimalScore totalScore = (BendableBigDecimalScore) indictment.getScore();

      for (ConstraintMatch constraintMatch : indictment.getConstraintMatchSet()) {
        String constraintName = constraintMatch.getConstraintName();
        BendableBigDecimalScore score = (BendableBigDecimalScore) constraintMatch.getScore();
        scoreBreakdown.constraints.put(constraintName, constraintMatch.getScore().toString());
      }
    }
    return scoreBreakdown;
  }
}
