package dev.ruivieira.counterfactual.op.creditcard.filters;

import dev.ruivieira.counterfactual.models.CreditCardApprovalModel;
import dev.ruivieira.counterfactual.op.creditcard.entities.CreditCardApprovalEntity;
import dev.ruivieira.counterfactual.op.creditcard.scoring.CreditCardApprovalHardSoftScoreCalculator;
import dev.ruivieira.counterfactual.op.creditcard.solution.CreditCardApprovalBendableSolution;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;

public class OnlyApprovedFilter implements SelectionFilter<CreditCardApprovalBendableSolution, CreditCardApprovalEntity> {

    private static Logger logger = LoggerFactory.getLogger(CreditCardApprovalHardSoftScoreCalculator.class);

    private CreditCardApprovalModel model;

    public OnlyApprovedFilter() {
        try {
            this.model = new CreditCardApprovalModel();
        } catch (IOException | SAXException | JAXBException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean accept(ScoreDirector<CreditCardApprovalBendableSolution> scoreDirector, CreditCardApprovalEntity entity) {
        System.out.println(entity.toString());
        var prediction = model.predict(entity);
//        logger.debug("age = {}, income = {}, children = {}, days = {}, realties = {}, phone = {}, car = {}",
//                entity.getAge(), entity.getIncome(), entity.getChildren(), entity.getDaysEmployed(),
//                entity.getOwnRealty(), entity.getWorkPhone(), entity.getOwnCar());

        return prediction.get(0).getOutputs().get(0).getValue().asNumber() == 1.0;
    }
}
