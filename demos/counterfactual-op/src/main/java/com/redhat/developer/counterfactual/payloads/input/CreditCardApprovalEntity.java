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

package com.redhat.developer.counterfactual.payloads.input;

public class CreditCardApprovalEntity {

  public AgeVariable age;

  public IncomeVariable income;

  public ChildrenVariable children;

  public DaysEmployedVariable daysEmployed;

  public RealtyVariable ownRealty;

  public PhoneVariable workPhone;

  public CarVariable ownCar;

  public CreditCardApprovalEntity() {}

  @Override
  public String toString() {
    return "CreditCardApprovalEntity{"
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
        + '}';
  }
}
