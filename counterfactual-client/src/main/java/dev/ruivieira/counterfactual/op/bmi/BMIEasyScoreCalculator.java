package dev.ruivieira.counterfactual.op.bmi;

import com.redhat.developer.model.*;
import dev.ruivieira.counterfactual.models.BMIModel;
import dev.ruivieira.counterfactual.Measures;
import dev.ruivieira.counterfactual.OptimizableModel;
import org.optaplanner.core.api.score.buildin.hardsoftbigdecimal.HardSoftBigDecimalScore;
import org.optaplanner.core.impl.score.director.easy.EasyScoreCalculator;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class BMIEasyScoreCalculator implements EasyScoreCalculator<BMISolution> {

    private double bmi = 25.0;
    private double height = 170.0;
    private double weight = 50.0;
    private Model model;
    private double[] initialGuess;

    public BMIEasyScoreCalculator() {
        super();
        this.initialGuess = new double[]{this.height, this.weight};
        try {
            this.model = new BMIModel();
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
    }

    public void setBmi(double bmi) {
    System.out.println("Setting goal to " + bmi);
        this.bmi = bmi;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    @Override
    public HardSoftBigDecimalScore calculateScore(BMISolution BMISolution) {

        final List<Feature> features = new ArrayList<>();
        final BMIEntity bmi = BMISolution.getTaskList().get(0);
        final Feature height = FeatureFactory.newNumericalFeature("height", bmi.getHeight());
        final Feature weight = FeatureFactory.newNumericalFeature("weight", bmi.getWeight());
        features.add(height);
        features.add(weight);
        final List<PredictionInput> inputs = new ArrayList<>();
        inputs.add(new PredictionInput(features));

        final List<PredictionOutput> predictions = model.predict(inputs);

        final double predictionDistance = Math.pow(this.bmi - predictions.get(0).getOutputs().get(0).getValue().asNumber(), 2);

        final double inputDistance = Measures.manhattan(new double[]{this.height, this.weight}, OptimizableModel.featureToArray(features));

        double hardScore = 0.0;

        if (bmi.getHeight() != this.height) {
            hardScore =- 1;
        }

        return HardSoftBigDecimalScore.of(BigDecimal.valueOf(hardScore), BigDecimal.valueOf(-Math.abs(predictionDistance + inputDistance)));
    }
}
