package it.pagopa.pn.templatesengine.processor.impl;

import it.pagopa.pn.templatesengine.config.PnTemplatesEngineConfig;
import it.pagopa.pn.templatesengine.config.ResolverWhitelistConfig;
import it.pagopa.pn.templatesengine.config.TemplatesEnum;
import it.pagopa.pn.templatesengine.config.TemplatesParamsEnum;
import it.pagopa.pn.templatesengine.model.InformalCommunicationGeneratedParams;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SenderLogoUrlProcessorTest {

	@Mock
	private PnTemplatesEngineConfig pnTemplatesEngineConfig;
    @Mock
    private ResolverWhitelistConfig resolverWhitelistConfig;

	private SenderLogoUrlProcessor processor;

	@BeforeEach
	void setUp() {
		processor = new SenderLogoUrlProcessor(pnTemplatesEngineConfig, resolverWhitelistConfig);
	}

	@Test
	void process_ShouldResolveLogoUrlAndPopulateSenderLogoUrl_WhenWhitelistDisabled() {
		String paId = "9a7c1b23-46a3-489b-8ed4-398ffb32b45a";
		String urlTemplate = "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/<PA_ID>/logo.png";
		String expectedUrl = "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/" + paId + "/logo.png";

		when(pnTemplatesEngineConfig.getSenderLogoUrlTemplate()).thenReturn(urlTemplate);
		when(resolverWhitelistConfig.isWhitelistEnabled(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY,
				TemplatesParamsEnum.SENDER_LOGO)).thenReturn(false);

		InformalCommunicationGeneratedParams outParams = new InformalCommunicationGeneratedParams();

		StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY, paId, outParams))
				.verifyComplete();

		assertEquals(expectedUrl, outParams.getSenderLogoUrl());
		verify(resolverWhitelistConfig, never()).isInWhitelist(any(), any(), any());
	}

	@Test
	void process_ShouldResolveLogoUrlAndPopulateSenderLogoUrl_WhenWhitelistEnabledAndUrlInWhitelist() {
		String paId = "9a7c1b23-46a3-489b-8ed4-398ffb32b45a";
		String urlTemplate = "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/<PA_ID>/logo.png";
		String expectedUrl = "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/" + paId + "/logo.png";

		when(pnTemplatesEngineConfig.getSenderLogoUrlTemplate()).thenReturn(urlTemplate);
		when(resolverWhitelistConfig.isWhitelistEnabled(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY,
				TemplatesParamsEnum.SENDER_LOGO)).thenReturn(true);
		when(resolverWhitelistConfig.isInWhitelist(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY,
				TemplatesParamsEnum.SENDER_LOGO, expectedUrl)).thenReturn(true);

		InformalCommunicationGeneratedParams outParams = new InformalCommunicationGeneratedParams();

		StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY, paId, outParams))
				.verifyComplete();

		assertEquals(expectedUrl, outParams.getSenderLogoUrl());
	}

	@Test
	void process_ShouldSetSenderLogoUrlToNullWhenWhitelistEnabledAndUrlNotInWhitelist() {
		String paId = "9a7c1b23-46a3-489b-8ed4-398ffb32b45a";
		String urlTemplate = "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/<PA_ID>/logo.png";
		String expectedUrl = "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/" + paId + "/logo.png";

		when(pnTemplatesEngineConfig.getSenderLogoUrlTemplate()).thenReturn(urlTemplate);
		when(resolverWhitelistConfig.isWhitelistEnabled(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY,
				TemplatesParamsEnum.SENDER_LOGO)).thenReturn(true);
		when(resolverWhitelistConfig.isInWhitelist(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY,
				TemplatesParamsEnum.SENDER_LOGO, expectedUrl)).thenReturn(false);

		InformalCommunicationGeneratedParams outParams = new InformalCommunicationGeneratedParams();

		StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY, paId, outParams))
				.verifyComplete();

		assertNull(outParams.getSenderLogoUrl());
	}

	@Test
	void process_ShouldSetSenderLogoUrlToNullWhenPaIdIsBlank() {
		InformalCommunicationGeneratedParams outParams = new InformalCommunicationGeneratedParams();

		StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY, "   ", outParams))
				.verifyComplete();

		assertNull(outParams.getSenderLogoUrl());
	}

	@Test
	void process_ShouldSetSenderLogoUrlToNullWhenPaIdIsNull() {
		InformalCommunicationGeneratedParams outParams = new InformalCommunicationGeneratedParams();

		StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY, null, outParams))
				.verifyComplete();

		assertNull(outParams.getSenderLogoUrl());
	}

	@Test
	void process_ShouldSetSenderLogoUrlToNullWhenUrlTemplateIsMissing() {
		when(pnTemplatesEngineConfig.getSenderLogoUrlTemplate()).thenReturn(null);

		InformalCommunicationGeneratedParams outParams = new InformalCommunicationGeneratedParams();

		StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY,
						"9a7c1b23-46a3-489b-8ed4-398ffb32b45a", outParams))
				.verifyComplete();

		assertNull(outParams.getSenderLogoUrl());
	}

	@Test
	void process_ShouldSetSenderLogoUrlToNullWhenUrlTemplateIsBlank() {
		when(pnTemplatesEngineConfig.getSenderLogoUrlTemplate()).thenReturn("   ");

		InformalCommunicationGeneratedParams outParams = new InformalCommunicationGeneratedParams();

		StepVerifier.create(processor.process(TemplatesEnum.INFORMAL_EMAIL_COMMUNICATION_BODY,
						"9a7c1b23-46a3-489b-8ed4-398ffb32b45a", outParams))
				.verifyComplete();

		assertNull(outParams.getSenderLogoUrl());
	}

	@Test
	void buildSenderLogoUrl_ShouldReplacePaIdPlaceholder() {
		String paId = "9a7c1b23-46a3-489b-8ed4-398ffb32b45a";
		String urlTemplate = "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/<PA_ID>/logo.png";
		String expectedUrl = "https://selcpcheckoutsa.z6.web.core.windows.net/institutions/" + paId + "/logo.png";

		when(pnTemplatesEngineConfig.getSenderLogoUrlTemplate()).thenReturn(urlTemplate);

		assertEquals(expectedUrl, processor.buildSenderLogoUrl(paId));
	}

	@Test
	void buildSenderLogoUrl_ShouldReturnNullWhenPaIdIsNull() {
		assertNull(processor.buildSenderLogoUrl(null));
	}

	@Test
	void buildSenderLogoUrl_ShouldReturnNullWhenPaIdIsBlank() {
		assertNull(processor.buildSenderLogoUrl("  "));
	}
}
