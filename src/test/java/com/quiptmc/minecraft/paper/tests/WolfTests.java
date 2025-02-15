package com.quiptmc.minecraft.paper.tests;

import dev.watchwolf.tester.AbstractTest;
import dev.watchwolf.tester.TesterConnector;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;

@ExtendWith(WolfTests.class)
public class WolfTests extends AbstractTest {

    @Override
    public String getConfigFile() {
        return "src/test/resources/config.yml";
    }

    @ParameterizedTest
    @ArgumentsSource(WolfTests.class)
    public void testTest(TesterConnector connector) {
//        connector.test();
    }
}
