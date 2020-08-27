package business.algorithm.sjword;

import common.AbstractMain;
import common.IDataSource;
import common.IDataTarget;
import common.IIteratorTranslator;
import common.source.text.TextFileSource;
import common.target.TextFileTarget;
import common.translator.StringDuplicateRemovalTranslator;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author by liufei
 * @Description
 * @Date 2020/3/31 11:07
 */
public class HanCoreDIrcTimeGetterMain extends AbstractMain {

    public static void main(String[] args) {
        new HanCoreDIrcTimeGetterMain().deal(null, null);
    }

    @Override
    protected List<IIteratorTranslator> getTranslators() {
        return Arrays.asList(new HanCoreDIrcTimeGetterTranslator(), new StringDuplicateRemovalTranslator("text"));
    }

    @Override
    protected IDataTarget buildDataTarget(Properties properties) {
        return new TextFileTarget(properties.getProperty("outputTextPath"), "text");
    }

    @Override
    protected IDataSource buildDataSource(Properties properties) {
        return new TextFileSource(properties.getProperty("hanLpCoreDircPath"));
    }
}
