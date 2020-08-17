package dev.ruivieira.counterfactual.op.creditcard.scoring;

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
        final List<PredictionInput> inputs = new ArrayList<>();
        inputs.add(new PredictionInput(entity.buildFeatures()));
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
