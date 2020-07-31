package io.sitoolkit.rdg.core.domain.generator.config;

import static org.hamcrest.core.Is.is;

import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.infrastructure.TestResourceUtils;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

@Slf4j
public class IgnoreScaleTest {

  @Rule
  public TestName testName = new TestName();

  @Test
  public void test() {

    Path dstDir = TestResourceUtils.copyResDir(this, "test");

    SchemaInfo schemaInfo = SchemaInfo.read(dstDir);
    TableDef tab1 = schemaInfo.findTable("", "tab_1").get();
    TableDef tab2 = schemaInfo.findTable("", "tab_2").get();

    GeneratorConfig generatorConfig = new GeneratorConfigReader().read(dstDir);
    long tab1RowCount = generatorConfig.getRowCount(tab1);
    long tab2RowCount = generatorConfig.getRowCount(tab2);

    log.info("tab1RowCount:{}, tab2RowCount:{}", tab1RowCount, tab2RowCount);

    Assert.assertThat(tab1RowCount, is(100L));
    Assert.assertThat(tab2RowCount, is(20L));
  }
}
