package counterfactual.benchmark;

import org.dmg.pmml.FieldName;
import org.jpmml.evaluator.*;
import org.kie.kogito.explainability.model.Output;
import org.kie.kogito.explainability.model.PredictionInput;
import org.kie.kogito.explainability.model.PredictionOutput;
import org.kie.kogito.explainability.model.PredictionProvider;
import org.kie.kogito.explainability.model.Type;
import org.kie.kogito.explainability.model.Value;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Model implements PredictionProvider {

    private static Model model = null;
    private final List<? extends InputField> inputFields;
    private final List<? extends TargetField> targetFields;
    private final Evaluator evaluator;

    public Model() throws IOException, SAXException, JAXBException {

        evaluator = new LoadingModelEvaluatorBuilder().load(new File("./models/model.pmml")).build();

        evaluator.verify();

        inputFields = evaluator.getInputFields();

        System.out.println("Input fields: " + inputFields);

        targetFields = evaluator.getTargetFields();

        System.out.println("Target field(s): " + targetFields);
    }

    public static Model getModel() {
        if (model == null) {
            try {
                model = new Model();
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

        InputField debtField = inputFields.get(1);
        FieldName debtName = debtField.getName();
        FieldValue debtValue =
                debtField.prepare(inputs.get(0).getFeatures().get(1).getValue().asNumber());
        arguments.put(debtName, debtValue);

        InputField yearsField = inputFields.get(2);
        FieldName yearsName = yearsField.getName();
        FieldValue yearsValue =
                yearsField.prepare(inputs.get(0).getFeatures().get(2).getValue().asNumber());
        arguments.put(yearsName, yearsValue);

        InputField incomeField = inputFields.get(3);
        FieldName incomeName = incomeField.getName();
        FieldValue incomeValue =
                incomeField.prepare(inputs.get(0).getFeatures().get(3).getValue().asNumber());
        arguments.put(incomeName, incomeValue);


        Map<FieldName, ?> results = evaluator.evaluate(arguments);
        Map<String, ?> resultRecord = EvaluatorUtil.decodeAll(results);
        return resultRecord;
    }

    @Override
    public CompletableFuture<List<PredictionOutput>> predictAsync(List<PredictionInput> inputs) {
        return CompletableFuture.supplyAsync(() -> {
            final Map<String, ?> prediction = predict(inputs);

            final List<Output> outputs = List.of(
                    new Output("Approved", Type.NUMBER, new Value<Integer>((Integer) prediction.get("Approved")), 1.0)
            );

            final PredictionOutput output = new PredictionOutput(outputs);
            return List.of(output);
        }
        );
    }
}
