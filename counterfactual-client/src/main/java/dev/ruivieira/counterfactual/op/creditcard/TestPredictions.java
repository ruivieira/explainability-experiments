package dev.ruivieira.counterfactual.op.creditcard;

import com.redhat.developer.model.Feature;
import com.redhat.developer.model.PredictionInput;
import com.redhat.developer.model.PredictionOutput;
import dev.ruivieira.counterfactual.models.CreditCardApprovalModel;
import dev.ruivieira.counterfactual.op.creditcard.entities.CreditCardApprovalEntity;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TestPredictions {

    public static void main(String[] args) throws JAXBException, SAXException, IOException {
        CreditCardApprovalModel model = new CreditCardApprovalModel();

        CreditCardApprovalEntity entity = new CreditCardApprovalEntity(51.0, 200000, 0, 100, true, true, true);

        final List<Feature> context = entity.buildFeatures();

        List<PredictionInput> inputs = new ArrayList<>();
        PredictionInput input = new PredictionInput(context);
        inputs.add(input);
        List<PredictionOutput> outputs = model.predict(inputs);
        PredictionOutput output = outputs.get(0);
        System.out.println("Original: " + output.getOutputs().get(0).getValue());
    }
}
