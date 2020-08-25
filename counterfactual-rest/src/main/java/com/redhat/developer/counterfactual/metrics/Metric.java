package com.redhat.developer.counterfactual.metrics;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Metric {
  @JsonProperty public Double age;
  @JsonProperty public Integer income;

  @JsonProperty public Integer children;

  @JsonProperty public Integer daysEmployed;

  @JsonProperty public Boolean ownRealty;

  @JsonProperty public Boolean workPhone;

  @JsonProperty public Boolean ownCar;

  @JsonProperty public Boolean approved;

  @JsonProperty public Double softScore;
  @JsonProperty public Double hardScore;

  public Double getAge() {
    return age;
  }

  public void setAge(Double age) {
    this.age = age;
  }

  public Integer getIncome() {
    return income;
  }

  public void setIncome(Integer income) {
    this.income = income;
  }

  public Integer getChildren() {
    return children;
  }

  public void setChildren(Integer children) {
    this.children = children;
  }

  public Integer getDaysEmployed() {
    return daysEmployed;
  }

  public void setDaysEmployed(Integer daysEmployed) {
    this.daysEmployed = daysEmployed;
  }

  public Boolean getOwnRealty() {
    return ownRealty;
  }

  public void setOwnRealty(Boolean ownRealty) {
    this.ownRealty = ownRealty;
  }

  public Boolean getWorkPhone() {
    return workPhone;
  }

  public void setWorkPhone(Boolean workPhone) {
    this.workPhone = workPhone;
  }

  public Boolean getOwnCar() {
    return ownCar;
  }

  public void setOwnCar(Boolean ownCar) {
    this.ownCar = ownCar;
  }

  public Boolean getApproved() {
    return approved;
  }

  public void setApproved(Boolean approved) {
    this.approved = approved;
  }

  public Double getSoftScore() {
    return softScore;
  }

  public void setSoftScore(Double softScore) {
    this.softScore = softScore;
  }

  @Override
  public String toString() {
    return "Metric{"
        + "age="
        + age
        + ", income="
        + income
        + ", children="
        + children
        + ", daysEmployed="
        + daysEmployed
        + ", ownRealty="
        + ownRealty
        + ", workPhone="
        + workPhone
        + ", ownCar="
        + ownCar
        + ", approved="
        + approved
        + ", softScore="
        + softScore
        + '}';
  }
}
