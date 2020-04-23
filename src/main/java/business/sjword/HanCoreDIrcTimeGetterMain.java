package business.sjword;

import common.transer.StringDuplicateRemovalTranser;
import common.*;
import common.source.FileSource;
import common.target.FileTarget;

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
    protected List<IIteratorTranser> getTransers() {
        return Arrays.asList(new HanCoreDIrcTimeGetterTranser(),new StringDuplicateRemovalTranser("text"));
    }

    @Override
    protected IDataTarget dataTarget(Properties properties) {
        return new FileTarget(properties.getProperty("outputTextPath"),"text");
    }

    @Override
    protected IDataSource dataSource(Properties properties) {
        return new FileSource(properties.getProperty("hanLpCoreDircPath"));
    }
}
