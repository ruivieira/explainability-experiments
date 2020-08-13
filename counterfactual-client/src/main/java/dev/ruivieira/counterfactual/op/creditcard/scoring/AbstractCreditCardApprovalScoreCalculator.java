package dev.ruivieira.counterfactual.op.creditcard.scoring;

import com.redhat.developer.model.Feature;
import com.redhat.developer.model.FeatureFactory;
import com.redhat.developer.model.Model;
import com.redhat.developer.model.PredictionInput;
import dev.ruivieira.counterfactual.models.CreditCardApprovalModel;
import dev.ruivieira.counterfactual.op.creditcard.entities.CreditCardApprovalEntity;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCreditCardApprovalScoreCalculator {
    private Double age;
    private Double income;
    private Double children;
    private Double daysEmployed;
    private Integer ownRealty;
    private Integer workPhone;
    private Integer ownCar;
    private Model model;

    public AbstractCreditCardApprovalScoreCalculator() {
        try {
            this.model = new CreditCardApprovalModel();
        } catch (IOException | SAXException | JAXBException e) {
            e.printStackTrace();
        }
    }

    public Double getAge() {
        return age;
    }

    public void setAge(Double age) {
        this.age = age;
    }

    public Double getIncome() {
        return income;
    }

    public void setIncome(Double income) {
        this.income = income;
    }

    public Double getChildren() {
        return children;
    }

    public void setChildren(Double children) {
        this.children = children;
    }

    public Double getDaysEmployed() {
        return daysEmployed;
    }

    public void setDaysEmployed(Double daysEmployed) {
        this.daysEmployed = daysEmployed;
    }

    public Integer getOwnRealty() {
        return ownRealty;
    }

    public void setOwnRealty(Integer ownRealty) {
        this.ownRealty = ownRealty;
    }

    public Integer getWorkPhone() {
        return workPhone;
    }

    public void setWorkPhone(Integer workPhone) {
        this.workPhone = workPhone;
    }

    public Integer getOwnCar() {
        return ownCar;
    }

    public void setOwnCar(Integer ownCar) {
        this.ownCar = ownCar;
    }

    public Model getModel() {
        return model;
    }

    protected List<PredictionInput> buildPredictionInputs(CreditCardApprovalEntity entity) {
        final List<Feature> features = new ArrayList<>();

        final Feature age = FeatureFactory.newNumericalFeature("AGE", entity.getAge().doubleValue());
        final Feature income =
                FeatureFactory.newNumericalFeature("AMT_INCOME_TOTAL", entity.getIncome().doubleValue());
        final Feature children =
                FeatureFactory.newNumericalFeature("CNT_CHILDREN", entity.getChildren().doubleValue());
        final Feature daysEmployed =
                FeatureFactory.newNumericalFeature("DAYS_EMPLOYED", entity.getDaysEmployed().doubleValue());
        final Feature ownRealty =
                FeatureFactory.newNumericalFeature(
                        "FLAG_OWN_REALTY", entity.getOwnRealty() ? 1 : 0);
        final Feature workPhone =
                FeatureFactory.newNumericalFeature(
                        "FLAG_WORK_PHONE", entity.getWorkPhone() ? 1 : 0);
        final Feature ownCar =
                FeatureFactory.newNumericalFeature("FLAG_OWN_CAR", entity.getOwnCar() ? 1 : 0);

        features.add(children);
        features.add(daysEmployed);
        features.add(age);
        features.add(income);
        features.add(workPhone);
        features.add(ownRealty);
        features.add(ownCar);

        final List<PredictionInput> inputs = new ArrayList<>();
        inputs.add(new PredictionInput(features));
        return inputs;
    }

    protected double[] buildInputArray() {
        return new double[] {
                        this.getAge(),
                        this.getIncome(),
                        this.getChildren(),
                        this.getDaysEmployed(),
                        this.getOwnRealty(),
                        this.getWorkPhone(),
                        this.getWorkPhone()
                };
    }

    protected double[] buildSolutionData(CreditCardApprovalEntity entity) {
        return
                new double[] {
                        entity.getAge(),
                        entity.getIncome(),
                        entity.getChildren(),
                        entity.getDaysEmployed(),
                        entity.getOwnRealty() ? 1.0 : 0.0,
                        entity.getWorkPhone() ? 1.0 : 0.0,
                        entity.getOwnCar() ? 1.0 : 0.0
                };
    }
}
