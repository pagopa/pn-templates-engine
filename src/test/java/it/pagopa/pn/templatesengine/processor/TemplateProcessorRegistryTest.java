package it.pagopa.pn.templatesengine.processor;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class TemplateProcessorRegistryTest {

    @Mock
    private TemplateModelProcessor<String, StringBuilder> firstProcessor;

    @Mock
    private TemplateModelProcessor<String, StringBuilder> secondProcessor;

    @Mock
    private TemplateModelProcessor<String, AtomicInteger> nullableProcessor;

    private TemplateProcessorRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new TemplateProcessorRegistry();
    }

    @Test
    void executeProcessors_ShouldRunRegisteredProcessorsInOrder() {
        // Arrange
        doAnswer(invocation -> {
            String target = invocation.getArgument(0);
            StringBuilder out = invocation.getArgument(1);
            out.append(target.toUpperCase());
            return null;
        }).when(firstProcessor).process(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(StringBuilder.class));

        doAnswer(invocation -> {
            String target = invocation.getArgument(0);
            StringBuilder out = invocation.getArgument(1);
            out.append("-").append(target.length());
            return null;
        }).when(secondProcessor).process(org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(StringBuilder.class));

        registry.register(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, firstProcessor, TestModel::value);
        registry.register(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, secondProcessor, TestModel::value);

        StringBuilder out = new StringBuilder();

        // Act
        registry.executeProcessors(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, new TestModel("ciao"), out);

        // Assert
        assertEquals("CIAO-4", out.toString());

        InOrder inOrder = inOrder(firstProcessor, secondProcessor);
        inOrder.verify(firstProcessor).process("ciao", out);
        inOrder.verify(secondProcessor).process("ciao", out);
    }

    @Test
    void executeProcessors_ShouldSkipProcessorWhenMappedTargetIsNull() {
        // Arrange
        AtomicInteger counter = new AtomicInteger();
        registry.register(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, nullableProcessor, model -> null);

        // Act
        registry.executeProcessors(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, new TestModel(null), counter);

        // Assert
        assertEquals(0, counter.get());
        verifyNoInteractions(nullableProcessor);
    }

    @Test
    void executeProcessors_ShouldDoNothingWhenNoProcessorIsRegistered() {
        // Arrange
        StringBuilder out = new StringBuilder("initial");

        // Act
        registry.executeProcessors(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, new TestModel("ciao"), out);

        // Assert
        assertEquals("initial", out.toString());
        verifyNoInteractions(firstProcessor, secondProcessor, nullableProcessor);
    }

    private record TestModel(String value) {
    }
}


