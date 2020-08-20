//package dev.ruivieira.counterfactual.op.contraints;
//
//import com.redhat.developer.model.PredictionOutput;
//import dev.ruivieira.counterfactual.models.CreditCardApprovalModel;
//import dev.ruivieira.counterfactual.op.creditcard.entities.CreditCardApprovalEntity;
//import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
//import org.optaplanner.core.api.score.stream.Constraint;
//import org.optaplanner.core.api.score.stream.ConstraintFactory;
//import org.optaplanner.core.api.score.stream.ConstraintProvider;
//import org.xml.sax.SAXException;
//
//import javax.xml.bind.JAXBException;
//import java.io.IOException;
//import java.util.List;
//
//public class CCApprovalConstraints implements ConstraintProvider {
//
//    private CreditCardApprovalModel model;
//
//    public CCApprovalConstraints() {
//
//        try {
//            this.model = new CreditCardApprovalModel();
//        } catch (IOException | SAXException | JAXBException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    @Override
//    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
//        return new Constraint[0];
//    }
//
//    private Constraint penalizeEveryShift(ConstraintFactory factory) {
//        return factory.from(CreditCardApprovalEntity.class)
//                .filter(ccc -> {
//                    final List<PredictionOutput> predictions = model.predict(ccc);
//                    return predictions.get(0).getOutputs().get(0).getValue().asNumber() == 1;
//                })
//                .penalize("Penalize a shift", HardSoftScore.);
//    }
//}
