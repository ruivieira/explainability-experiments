/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.developer.counterfactual.solutions;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.redhat.developer.counterfactual.entities.CounterfactualEntity;
import com.redhat.developer.counterfactual.models.Model;
import org.kie.kogito.explainability.model.Feature;
import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.buildin.bendablebigdecimal.BendableBigDecimalScore;

import java.util.List;

@PlanningSolution
public class CounterfactualSolution {

  @PlanningEntityCollectionProperty public List<CounterfactualEntity> entities;

  @JsonIgnore
  private Feature goal;

  @JsonIgnore
  private Model model;

  private BendableBigDecimalScore score;

  protected CounterfactualSolution() {
  }

  public CounterfactualSolution(
          List<CounterfactualEntity> entities,
          Model model,
          Feature goal) {
    this.entities = entities;
    this.model = model;
    this.goal = goal;

  }
  @PlanningScore(bendableHardLevelsSize = 2, bendableSoftLevelsSize = 1)
  public BendableBigDecimalScore getScore() {
    return score;
  }

  public void setScore(BendableBigDecimalScore score) {
    this.score = score;
  }

  public Model getModel() {
    return model;
  }

  public Feature getGoal() {
    return goal;
  }
}
