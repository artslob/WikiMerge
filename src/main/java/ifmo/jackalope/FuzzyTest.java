package ifmo.jackalope;

import org.simmetrics.StringMetric;
import org.simmetrics.StringMetricBuilder;
import org.simmetrics.metrics.CosineSimilarity;
import org.simmetrics.simplifiers.Simplifiers;
import org.simmetrics.tokenizers.Tokenizers;


public class FuzzyTest {

    private final StringMetric metric;

    FuzzyTest() {
        metric = StringMetricBuilder.
                with(new CosineSimilarity<String>())
                .simplify(Simplifiers.toLowerCase())
                .tokenize(Tokenizers.whitespace())
                .build();
    }

    public float test_fuzzy_equality(String s1, String s2) {
        return metric.compare(s1, s2);
    }

    public static void main(String[] args) {
        String str1 = "СТРОКА МОЯ";
        String str2 = "а я моя строка и";

        StringMetric metric = StringMetricBuilder.
                with(new CosineSimilarity<>())
                .simplify(Simplifiers.toLowerCase())
                .tokenize(Tokenizers.whitespace())
                .build();

        float result = metric.compare(str1, str2);
        System.out.println(result);
    }
}
