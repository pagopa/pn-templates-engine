package it.pagopa.pn.templatesengine.processor;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

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
        when(firstProcessor.process(any(TemplatesEnum.class), anyString(), any(StringBuilder.class)))
                .thenAnswer(invocation -> {
                    String target = invocation.getArgument(1);
                    StringBuilder out = invocation.getArgument(2);
                    out.append(target.toUpperCase());
                    return Mono.empty();
                });

        when(secondProcessor.process(any(TemplatesEnum.class), anyString(), any(StringBuilder.class)))
                .thenAnswer(invocation -> {
                    String target = invocation.getArgument(1);
                    StringBuilder out = invocation.getArgument(2);
                    out.append("-").append(target.length());
                    return Mono.empty();
                });

        registry.registerChain(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, TestModel.class, StringBuilder::new)
                .add(firstProcessor, TestModel::value)
                .add(secondProcessor, TestModel::value);

        // Act & Assert
        StepVerifier.create(registry.executeProcessors(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, new TestModel("ciao")))
                .assertNext(out -> {
                    StringBuilder sb = (StringBuilder) out;
                    assertEquals("CIAO-4", sb.toString());

                    InOrder inOrder = inOrder(firstProcessor, secondProcessor);
                    inOrder.verify(firstProcessor).process(eq(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY), eq("ciao"), eq(sb));
                    inOrder.verify(secondProcessor).process(eq(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY), eq("ciao"), eq(sb));
                })
                .verifyComplete();
    }

    @Test
    void executeProcessors_ShouldSkipProcessorWhenMappedTargetIsNull() {
        // Arrange
        registry.registerChain(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, TestModel.class, AtomicInteger::new)
                .add(nullableProcessor, model -> null);

        // Act & Assert
        StepVerifier.create(registry.executeProcessors(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, new TestModel(null)))
                .assertNext(out -> {
                    assertEquals(0, ((AtomicInteger) out).get());
                    verifyNoInteractions(nullableProcessor);
                })
                .verifyComplete();
    }

    @Test
    void executeProcessors_ShouldDoNothingWhenNoProcessorIsRegistered() {
        // Act & Assert
        StepVerifier.create(registry.executeProcessors(TemplatesEnum.MAIL_VERIFICATION_CODE_BODY, new TestModel("ciao")))
                .verifyComplete();

        verifyNoInteractions(firstProcessor, secondProcessor, nullableProcessor);
    }

    private record TestModel(String value) {
    }
}

