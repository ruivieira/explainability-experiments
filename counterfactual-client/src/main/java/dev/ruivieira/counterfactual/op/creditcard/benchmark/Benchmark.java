package dev.ruivieira.counterfactual.op.creditcard.benchmark;

import org.optaplanner.benchmark.api.PlannerBenchmark;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;

public class Benchmark {

    public static void main(String[] args) {
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.createFromXmlResource(
                "benchmarkConfig.xml");
//        benchmarkFactory.buildPlannerBenchmark()
        PlannerBenchmark benchmark = benchmarkFactory.buildPlannerBenchmark();

        benchmark.benchmarkAndShowReportInBrowser();
    }
}
