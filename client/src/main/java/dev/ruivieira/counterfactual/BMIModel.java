package dev.ruivieira.counterfactual;

import com.redhat.developer.model.Value;
import com.redhat.developer.model.*;
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

public class BMIModel implements Model {

  private final List<? extends InputField> inputFields;
  private final List<? extends TargetField> targetFields;
  private final Evaluator evaluator;

  public BMIModel() throws IOException, SAXException, JAXBException {

    evaluator = new LoadingModelEvaluatorBuilder().load(new File("../models/bmi.pmml")).build();

    evaluator.verify();

    inputFields = evaluator.getInputFields();

    System.out.println("Input fields: " + inputFields);

    targetFields = evaluator.getTargetFields();

    System.out.println("Target field(s): " + targetFields);
  }

  @Override
  public List<PredictionOutput> predict(List<PredictionInput> inputs) {
    Map<FieldName, FieldValue> arguments = new LinkedHashMap<>();

    InputField heightField = inputFields.get(0);
    FieldName heightName = heightField.getName();
    FieldValue inputValue = heightField.prepare(inputs.get(0).getFeatures().get(0).getValue().asNumber());
    arguments.put(heightName, inputValue);

    InputField weightField = inputFields.get(1);
    FieldName weightName = weightField.getName();
    FieldValue inputValue2 = weightField.prepare(inputs.get(0).getFeatures().get(1).getValue().asNumber());
    arguments.put(weightName, inputValue2);

    Map<FieldName, ?> results = evaluator.evaluate(arguments);
    Map<String, ?> resultRecord = EvaluatorUtil.decodeAll(results);

    final List<PredictionOutput> result = new ArrayList<>();

    final List<Output> outputs = new ArrayList<>();

    final Output output = new Output("bmi", Type.NUMBER, new Value<Double>((Double) resultRecord.get("bmi")), 0.0);

    outputs.add(output);

    PredictionOutput predictionOutput = new PredictionOutput(outputs);

    result.add(predictionOutput);

    return result;
  }

  @Override
  public DataDistribution getDataDistribution() {
    return null;
  }

  @Override
  public PredictionInput getInputShape() {
    return null;
  }

  @Override
  public PredictionOutput getOutputShape() {
    return null;
  }
}
