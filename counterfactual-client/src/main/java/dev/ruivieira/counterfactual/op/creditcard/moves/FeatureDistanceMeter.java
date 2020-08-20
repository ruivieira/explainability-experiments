package dev.ruivieira.counterfactual.op.creditcard.moves;

import dev.ruivieira.counterfactual.op.creditcard.entities.CreditCardApprovalEntity;
import org.optaplanner.core.impl.heuristic.selector.common.nearby.NearbyDistanceMeter;

public class FeatureDistanceMeter implements NearbyDistanceMeter<CreditCardApprovalEntity, CreditCardApprovalEntity> {
    @Override
    public double getNearbyDistance(CreditCardApprovalEntity o, CreditCardApprovalEntity d) {
        double distance = 0.0;

        distance += o.getAge() - d.getAge();
        distance += o.getIncome() - d.getIncome();
        distance += o.getChildren() - d.getChildren();
        distance += o.getDaysEmployed() - d.getDaysEmployed();
        distance += (o.getOwnRealty() ? 1.0 : 0.0) - (d.getOwnRealty() ? 1.0 : 0.0);
        distance += (o.getWorkPhone() ? 1.0 : 0.0) - (d.getWorkPhone() ? 1.0 : 0.0);
        distance += (o.getOwnCar() ? 1.0 : 0.0) - (d.getOwnCar() ? 1.0 : 0.0);

        return Math.abs(distance);
    }
}
