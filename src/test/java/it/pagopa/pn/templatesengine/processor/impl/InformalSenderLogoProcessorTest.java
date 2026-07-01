package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.PnTemplatesEngineConfig;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.config.TemplatesParamsEnum;
import it.pagopa.pn.templatesengine.model.params.InformalAnalogCommunicationGeneratedParams;
import it.pagopa.pn.templatesengine.resolver.TemplateValueResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InformalSenderLogoProcessorTest {

	@Mock
	private TemplateValueResolver templateValueResolver;

	@Mock
	private PnTemplatesEngineConfig pnTemplatesEngineConfig;

	private InformalSenderLogoProcessor processor;

	@BeforeEach
	void setUp() {
		processor = new InformalSenderLogoProcessor(templateValueResolver, pnTemplatesEngineConfig);
	}

	@Test
	void process_ShouldResolveLogoUrlAndPopulateSenderLogoBase64() {
		String paId = "9a7c1b23-46a3-489b-8ed4-398ffb32b45a";
		String urlTemplate = "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/<PA_ID>/logo.png";
		String expectedUrl = "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/" + paId + "/logo.png";
		String expectedBase64 = "aGVsbG8=";

		when(pnTemplatesEngineConfig.getSenderLogoUrlTemplate()).thenReturn(urlTemplate);

		when(templateValueResolver.resolve(
				anyString(),
				eq(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION),
				eq(TemplatesParamsEnum.SENDER_LOGO_BASE64)
		)).thenReturn(Mono.just(expectedBase64));

		InformalAnalogCommunicationGeneratedParams outParams = new InformalAnalogCommunicationGeneratedParams();

		processor.process(paId, outParams);

		assertEquals(expectedBase64, outParams.getSenderLogoBase64());
		verify(templateValueResolver).resolve(
				"TO_BASE64_RESOLVER:" + expectedUrl,
				TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION,
				TemplatesParamsEnum.SENDER_LOGO_BASE64
		);
	}

	@Test
	void process_ShouldSetSenderLogoBase64ToNullWhenPaIdIsBlank() {
		InformalAnalogCommunicationGeneratedParams outParams = new InformalAnalogCommunicationGeneratedParams();

		processor.process("   ", outParams);

		assertNull(outParams.getSenderLogoBase64());
		verifyNoInteractions(templateValueResolver);
	}

	@Test
	void process_ShouldSetSenderLogoBase64ToNullWhenResolverFails() {
		String paId = "9a7c1b23-46a3-489b-8ed4-398ffb32b45a";
		String urlTemplate = "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/<PA_ID>/logo.png";
		String expectedUrl = "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/" + paId + "/logo.png";

		when(pnTemplatesEngineConfig.getSenderLogoUrlTemplate()).thenReturn(urlTemplate);

		when(templateValueResolver.resolve(
				anyString(),
				eq(TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION),
				eq(TemplatesParamsEnum.SENDER_LOGO_BASE64)
		)).thenReturn(Mono.error(new RuntimeException("boom")));

		InformalAnalogCommunicationGeneratedParams outParams = new InformalAnalogCommunicationGeneratedParams();

		processor.process(paId, outParams);

		assertNull(outParams.getSenderLogoBase64());
		verify(templateValueResolver).resolve(
				"TO_BASE64_RESOLVER:" + expectedUrl,
				TemplatesEnum.INFORMAL_ANALOG_COMMUNICATION,
				TemplatesParamsEnum.SENDER_LOGO_BASE64
		);
	}

	@Test
	void process_ShouldSetSenderLogoBase64ToNullWhenUrlTemplateIsMissing() {
		when(pnTemplatesEngineConfig.getSenderLogoUrlTemplate()).thenReturn(null);

		InformalAnalogCommunicationGeneratedParams outParams = new InformalAnalogCommunicationGeneratedParams();

		processor.process("9a7c1b23-46a3-489b-8ed4-398ffb32b45a", outParams);

		assertNull(outParams.getSenderLogoBase64());
		verifyNoInteractions(templateValueResolver);
	}
}

