package com.redhat.developer.counterfactual.models;

import com.redhat.developer.counterfactual.entities.CreditCardApprovalEntity;
import com.redhat.developer.model.PredictionInput;
import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.*;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CreditCardApprovalModel {

  private static CreditCardApprovalModel model = null;
  private final List<? extends InputField> inputFields;
  private final List<? extends TargetField> targetFields;
  private final Evaluator evaluator;

  public CreditCardApprovalModel() throws IOException, SAXException, JAXBException {

    evaluator = new LoadingModelEvaluatorBuilder().load(new File("../models/loan.pmml")).build();

    evaluator.verify();

    inputFields = evaluator.getInputFields();

    System.out.println("Input fields: " + inputFields);

    targetFields = evaluator.getTargetFields();

    System.out.println("Target field(s): " + targetFields);
  }

  public static CreditCardApprovalModel getModel() {
    if (model == null) {
      try {
        model = new CreditCardApprovalModel();
      } catch (IOException | SAXException | JAXBException e) {
        e.printStackTrace();
      }
    }
    return model;
  }

  public Map<String, ?> predict(List<PredictionInput> inputs) {
    Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();

    InputField ageField = inputFields.get(0);
    FieldName ageName = ageField.getName();
    FieldValue inputValue =
        ageField.prepare((Double) inputs.get(0).getFeatures().get(0).getValue().asNumber());
    arguments.put(ageName, inputValue);

    InputField incomeField = inputFields.get(1);
    FieldName incomeName = incomeField.getName();
    FieldValue incomeValue =
        incomeField.prepare(inputs.get(0).getFeatures().get(1).getValue().asNumber());
    arguments.put(incomeName, incomeValue);

    InputField childrenField = inputFields.get(2);

    FieldName childrenName = childrenField.getName();
    FieldValue childrenValue =
        childrenField.prepare(inputs.get(0).getFeatures().get(2).getValue().asNumber());
    arguments.put(childrenName, childrenValue);

    InputField daysEmployedField = inputFields.get(3);
    FieldName daysEmployedName = daysEmployedField.getName();
    FieldValue daysEmployedValue =
        daysEmployedField.prepare(inputs.get(0).getFeatures().get(3).getValue().asNumber());
    arguments.put(daysEmployedName, daysEmployedValue);

    InputField ownRealtyField = inputFields.get(4);
    FieldName ownRealtyName = ownRealtyField.getName();
    FieldValue ownRealtyValue =
        ownRealtyField.prepare(inputs.get(0).getFeatures().get(4).getValue().asNumber());
    arguments.put(ownRealtyName, ownRealtyValue);

    InputField workPhoneField = inputFields.get(5);
    FieldName workPhoneName = workPhoneField.getName();
    FieldValue workPhoneValue =
        workPhoneField.prepare(inputs.get(0).getFeatures().get(5).getValue().asNumber());
    arguments.put(workPhoneName, workPhoneValue);

    InputField ownCarField = inputFields.get(6);
    FieldName ownCarName = ownCarField.getName();
    FieldValue ownCarValue =
        ownCarField.prepare(inputs.get(0).getFeatures().get(6).getValue().asNumber());
    arguments.put(ownCarName, ownCarValue);

    Map<FieldName, ?> results = evaluator.evaluate(arguments);
    Map<String, ?> resultRecord = EvaluatorUtil.decodeAll(results);
    //    System.out.println(resultRecord);
    return resultRecord;
  }

  public Map<String, ?> predict(CreditCardApprovalEntity entity) {
    final List<PredictionInput> inputs = new ArrayList<>();
    inputs.add(new PredictionInput(entity.buildFeatures()));
    return predict(inputs);
  }
}
