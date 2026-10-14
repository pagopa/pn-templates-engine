package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.config.TemplatesParamsEnum;
import it.pagopa.pn.templatesengine.model.InformalCommunicationGeneratedParams;
import it.pagopa.pn.templatesengine.resolver.TemplateValueResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SenderLogoBase64ProcessorTest {

	@Mock
	private TemplateValueResolver templateValueResolver;

	@Mock
	private SenderLogoUrlProcessor senderLogoUrlProcessor;

	private SenderLogoBase64Processor processor;

	@BeforeEach
	void setUp() {
		processor = new SenderLogoBase64Processor(templateValueResolver, senderLogoUrlProcessor);
	}

	@Test
	void process_ShouldResolveLogoUrlAndPopulateSenderLogoBase64() {
		String paId = "9a7c1b23-46a3-489b-8ed4-398ffb32b45a";
		String expectedUrl = "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/" + paId + "/logo.png";
		String expectedBase64 = "aGVsbG8=";

		when(senderLogoUrlProcessor.buildSenderLogoUrl(paId)).thenReturn(expectedUrl);

		when(templateValueResolver.resolve(
				anyString(),
				eq(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION),
				eq(TemplatesParamsEnum.SENDER_LOGO)
		)).thenReturn(Mono.just(expectedBase64));

		InformalCommunicationGeneratedParams outParams = new InformalCommunicationGeneratedParams();

		StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, paId, outParams))
				.verifyComplete();

		assertEquals(expectedBase64, outParams.getSenderLogoBase64());
		verify(templateValueResolver).resolve(
				"TO_BASE64_RESOLVER:" + expectedUrl,
				TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION,
				TemplatesParamsEnum.SENDER_LOGO
		);
	}

	@Test
	void process_ShouldSetSenderLogoBase64ToNullWhenPaIdIsBlank() {
		when(senderLogoUrlProcessor.buildSenderLogoUrl("   ")).thenReturn(null);

		InformalCommunicationGeneratedParams outParams = new InformalCommunicationGeneratedParams();

		StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION,"   ", outParams))
				.verifyComplete();

		assertNull(outParams.getSenderLogoBase64());
		verifyNoInteractions(templateValueResolver);
	}

	@Test
	void process_ShouldSetSenderLogoBase64ToNullWhenResolverFails() {
		String paId = "9a7c1b23-46a3-489b-8ed4-398ffb32b45a";
		String expectedUrl = "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/" + paId + "/logo.png";

		when(senderLogoUrlProcessor.buildSenderLogoUrl(paId)).thenReturn(expectedUrl);

		when(templateValueResolver.resolve(
				anyString(),
				eq(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION),
				eq(TemplatesParamsEnum.SENDER_LOGO)
		)).thenReturn(Mono.error(new RuntimeException("boom")));

		InformalCommunicationGeneratedParams outParams = new InformalCommunicationGeneratedParams();

		StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, paId, outParams))
				.verifyComplete();

		assertNull(outParams.getSenderLogoBase64());
		verify(templateValueResolver).resolve(
				"TO_BASE64_RESOLVER:" + expectedUrl,
				TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION,
				TemplatesParamsEnum.SENDER_LOGO
		);
	}

	@Test
	void process_ShouldSetSenderLogoBase64ToNullWhenUrlTemplateIsMissing() {
		String paId = "9a7c1b23-46a3-489b-8ed4-398ffb32b45a";
		when(senderLogoUrlProcessor.buildSenderLogoUrl(paId)).thenReturn(null);

		InformalCommunicationGeneratedParams outParams = new InformalCommunicationGeneratedParams();

		StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION, paId, outParams))
				.verifyComplete();

		assertNull(outParams.getSenderLogoBase64());
		verifyNoInteractions(templateValueResolver);
	}
}

