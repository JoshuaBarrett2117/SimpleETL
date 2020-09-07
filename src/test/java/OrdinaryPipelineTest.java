import com.code.common.utils.Assert;
import org.junit.jupiter.api.Test;
import pipeline.OrdinaryPipeline;
import pipeline.Pipeline;
import pipeline.PipelineContext;

import java.util.Arrays;

public class OrdinaryPipelineTest {
    @Test
    public void test() {
        Pipeline<?> pipeline =
                OrdinaryPipeline.getInstance(
                        Arrays.asList(new DemoPipeline("1"), new DemoPipeline("2"), new DemoPipeline("3")));
        Assert.assertEquals("1->2->3", pipeline.toString());
    }

    private static final class DemoPipeline extends OrdinaryPipeline<String> {

        public DemoPipeline(String name) {
            super(name);
        }

        @Override
        public void process(PipelineContext ctx, String s) {
            // TODO
        }
    }
}