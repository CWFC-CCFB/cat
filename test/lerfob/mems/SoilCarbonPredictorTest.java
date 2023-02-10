package lerfob.mems;

import org.junit.Assert;
import org.junit.Test;
import repicea.math.Matrix;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
public class SoilCarbonPredictorTest {

    @Test
    public void IterationBalanceTest() {
        SoilCarbonPredictor predictor = new SoilCarbonPredictor(false);

        SoilCarbonPredictorCompartment compartments = new SoilCarbonPredictorCompartment(1.0);

        SoilCarbonPredictorInput inputs = new SoilCarbonPredictorInput(1, 0.0, 0.45, 0.2, 5.58, 1.21, 47.8, 7.62);
        SoilCarbonPredictorCompartment cChange = predictor.predictCStockChanges(compartments, inputs);

        double sum = cChange.getSum();
        Assert.assertEquals("The resulting balance should be near zero", 0d, sum, 1.0E-12);
    }

    @Test
    public void IterationPerformanceTest() {
        int nbIterations = 10 * 365;

        Instant before, after;

        SoilCarbonPredictor predictor = new SoilCarbonPredictor(false);

        SoilCarbonPredictorCompartment compartments = new SoilCarbonPredictorCompartment(1.0);

        SoilCarbonPredictorInput inputs = new SoilCarbonPredictorInput(1, 10.0, 0.45, 0.2, 5.58, 1.21, 47.8, 7.62);

        before = Instant.now();

        SoilCarbonPredictorCompartment cChange = new SoilCarbonPredictorCompartment(0.0);

        for (int i = 0; i < nbIterations; i++)
        {
            cChange = predictor.predictCStockChanges(compartments, inputs);
            compartments.add(cChange);
        }

        after = Instant.now();

        long elapsed = ChronoUnit.MILLIS.between(before,after);
        System.out.println("Performance test executed " + nbIterations + " iterations in " + elapsed + " milliseconds");
        Assert.assertTrue("Performance test should execute a full 10 year cycle in less than 100 ms", elapsed < 100);
    }
}
