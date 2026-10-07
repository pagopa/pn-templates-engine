# Architettura interna

Il microservizio è organizzato attorno a tre controller REST: `TemplateApiController`, che espone gli endpoint di generazione documentale definiti nella specifica OpenAPI; `QrCodeApiController`, che genera QR code in Base64; e `HealthCheckApiController`, che traduce lo stato di `HealthEndpoint` in una risposta HTTP `200` o `500`.

La logica di generazione è concentrata in `TemplateService`, che usa `TemplateConfig` per selezionare il template corretto in base al tipo richiesto e alla lingua, applicando il fallback su `defaultLanguage` quando il file per la lingua richiesta non è disponibile. Prima del rendering, il servizio invoca `TemplateProcessorRegistry` per eseguire l'eventuale chain di processor associata al template. La classe `DocumentCompositionImpl` esegue il rendering del template con FreeMarker e, nel caso dei PDF, converte l'HTML risultante in PDF tramite OpenHTMLtoPDF e Jsoup.

`TemplateProcessorRegistry` associa un `TemplatesEnum` a una `TemplateProcessorChain` tipizzata, composta da una factory dell'output generato e da uno o più processor eseguiti in ordine. La configurazione delle chain è centralizzata in `TemplateProcessorRegistryConfig`: per i template delle comunicazioni bonarie analogiche, email e PEC vengono eseguiti `MarkdownToHtmlProcessor`, che converte i campi Markdown del body in HTML tramite CommonMark, e `SenderLogoProcessor`, che costruisce il valore del logo dell'ente mittente usando il resolver configurato per il template. I parametri calcolati dai processor vengono iniettati da `DocumentCompositionImpl` nella variabile FreeMarker `GENERATED`, così i template possono usarli come campi generati separati dal payload originale.

Per i template che configurano parametri risolvibili, in particolare `NotificationAar`, `NotificationAarRaddAlt` e `InformalAnalogCommunication`, il flusso applica la risoluzione del campo `senderLogoBase64`. `TemplateValueResolver` legge la configurazione dei resolver da `TemplateConfig`, applica le regole `enabled`, `bypassAllWithNull`, `returnNullOnError` e `whitelistEnabled`, e usa `ResolverWhitelistConfig` per caricare le whitelist da Parameter Store tramite `CachedParameterStoreConsumer`.

Quando il resolver è attivo, `ToBase64Resolver` usa `UrlResolver` per scaricare la risorsa remota via `WebClient`, applica il timeout definito in `PnTemplatesEngineConfig` e converte il contenuto binario in Data URL Base64 con MIME type rilevato tramite Apache Tika. `SenderLogoProcessor` costruisce l'URL del logo sostituendo l'identificativo del mittente nel placeholder `<PA_ID>` configurato in `pn.templates-engine.senderLogoUrlTemplate`; se la whitelist è abilitata, il download avviene solo per URL presenti nei parametri configurati.

I sorgenti dei template risiedono in `templates-assets/templates`. La pipeline Node contenuta in `scripts/templates-builder` genera gli asset compilati in `src/main/resources/generated-templates-assets`, mentre `build-templates.sh` rigenera gli stessi asset e li copia anche in `src/test/resources/generated-templates-assets` per l'esecuzione dei test. Durante la build Maven, il plugin OpenAPI rigenera inoltre le interfacce server a partire da `docs/openapi/pn-internal-templates-v1.yaml` nella fase `generate-resources`.

- [Come aggiungere un template](aggiunta_template.md)
- [Diagramma architetturale](diagrams/DiagrammaArchitetturale.png)
- [Sorgente del diagramma](diagrams/DiagrammaArchitetturale.excalidraw)

