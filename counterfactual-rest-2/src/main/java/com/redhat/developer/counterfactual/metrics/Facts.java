package com.redhat.developer.counterfactual.metrics;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.redhat.developer.counterfactual.entities.CreditCardApprovalEntity;

import java.util.ArrayList;
import java.util.List;

public class Facts {

  @JsonProperty(value = "metrics")
  public static final List<Metric> metrics = new ArrayList<>();

  @JsonProperty public static CreditCardApprovalEntity counterfactual;

  @JsonProperty public static CreditCardApprovalEntity input;
}
