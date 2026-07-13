## Come aggiungere un nuovo template

### 1. Creare il template

Creare una cartella sotto
[`templates-assets/templates`](../../templates-assets/templates) con il nome del nuovo template.

All'interno inserire un file `index.html` e creare un'altra cartella con nome il `i18n`. Dentro andranno inseriti i file json per i template di lingua.

Si avrà una struttura come segue:
```
template-assets/
    templates/
        NewTemplate/
            index.html
            i18n/
                IT.json
                DE.json
```
---

### 2. Aggiungere endpoint OpenAPI

Nel file [`pn-internal-templates-v1.yaml`](../openapi/pn-internal-templates-v1.yaml) aggiungere il nuovo endpoint per la creazione del documento richiesto.

Ad esempio:
```yaml
/templates-engine-private/v1/templates/<nome-template>:
  put:
    summary: NomeTemplate
    description: Genera un documento basato sul template NomeTemplate.
    operationId: nomeTemplate
    tags:
      - Template
    parameters:
      - in: header
        name: x-language
        description: Lingua del documento
        required: true
        schema:
          $ref: 'schemas-params.yaml#/components/schemas/LanguageEnum'
    requestBody:
      description: Parametri necessari per generare il documento
      required: true
      content:
        application/json:
          schema:
            $ref: 'schemas-params.yaml#/components/schemas/NomeTemplate'
    responses:
      '200':
        $ref: 'schemas-responses.yaml#/components/responses/SuccessResponseHTML'
      '400':
        $ref: 'schemas-responses.yaml#/components/responses/FailResponseBadRequest'
      '500':
        $ref: 'schemas-responses.yaml#/components/responses/FailResponseInternalServerError'
   ```

### 3. Aggiungere schema request

Nel file [`schemas-params.yaml`](../openapi/schemas-params.yaml) aggiungere lo schema del body della richiesta.

Ad esempio:
```yaml
NomeTemplate:
  type: object
  required:
    - field1
  properties:
    field1:
      type: string
```

### 4. Configurare  application.yaml
Nel progetto sono presenti due file application.yaml, vanno aggiornati entrambi con il nuovo template.

Ad esempio:
```yaml
templates:
  nomeTemplate:
    input:
      IT: "NomeTemplate/NomeTemplate.html"
      DE: "NomeTemplate/NomeTemplate_de.html"
```

### 5. Aggiornare TemplatesEnum.java

Alla enum [`TemplatesEnum.java`](../../src/main/java/it/pagopa/pn/templatesengine/config/TemplatesEnum.java) aggiungere il nuovo template.

Ad esempio:
```java
NOME_TEMPLATE("nomeTemplate")
```

### 6. Implementare controller

Infine nella classe [`TemplateApiController.java`](../../src/main/java/it/pagopa/pn/templatesengine/rest/TemplateApiController.java) aggiungere l'override al metodo dell'interfaccia per la chiamata al nuovo endpoint.

Ad esempio:
```java
@Override
public Mono<ResponseEntity<String>> nomeTemplate(
        LanguageEnum xLanguage,
        Mono<NomeTemplate> request,
        ServerWebExchange exchange) {
     return templateService.executeTemplate(template, xLanguage, request)
            .map(result -> ResponseEntity.ok().body(result));
}
```

### 7. Registrare processor opzionali

Se il template richiede parametri calcolati prima del rendering, aggiungere una chain in [`TemplateProcessorRegistryConfig.java`](../../src/main/java/it/pagopa/pn/templatesengine/config/TemplateProcessorRegistryConfig.java).

I processor disponibili implementano `TemplateModelProcessor` e ricevono:

- il template in elaborazione;
- una porzione del model estratta tramite mapper;
- un oggetto output popolato progressivamente dalla chain.

Ad esempio, le comunicazioni bonarie usano `MarkdownToHtmlProcessor` per convertire il body Markdown in HTML e `SenderLogoProcessor` per costruire il valore del logo del mittente usando il resolver configurato per il template:

```java
registry.registerChain(TemplatesEnum.NOME_TEMPLATE, NomeTemplate.class, NomeTemplateGeneratedParams::new)
        .add(markdownToHtmlProcessor, NomeTemplate::getBody)
        .add(senderLogoProcessor, model -> model.getSender().getId());
```

La classe `NomeTemplateGeneratedParams` deve esporre i campi valorizzati dai processor, implementando le interfacce di output richieste dai processor usati. I parametri prodotti dalla chain vengono resi disponibili nel template FreeMarker sotto la variabile `GENERATED`.

Esempio di utilizzo nel template:

```ftl
${GENERATED.primaryContentHtml}
${GENERATED.secondaryContentHtml}
${GENERATED.senderLogoBase64}
```

Se il template non richiede elaborazioni preliminari, non è necessario registrare alcuna chain: `TemplateService` procederà direttamente con il rendering del payload originale.

