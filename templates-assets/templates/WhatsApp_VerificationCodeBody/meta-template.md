# WhatsApp_VerificationCodeBody – template di autenticazione Meta

| Nome template Meta | Categoria | Lingue |
|---|---|---|
| `send_codice_verifica` | AUTHENTICATION | it, en, de, fr, sl |

I template di categoria AUTHENTICATION hanno un corpo **predefinito da Meta** (es. IT: *"{{1}} è il tuo codice di verifica.
Per motivi di sicurezza, non condividere questo codice."*) e un pulsante **"Copia codice"** (OTP `COPY_CODE`).
Al momento dell'invio il codice va passato sia come parametro del body sia come parametro del pulsante URL:
il descrittore riporta quindi `bodyParameters` e `buttonParameters`, entrambi valorizzati con il codice.

`index*.txt` è caricato come stringa (`loadAsString: true`, come `SMS_VerificationCodeBody`): il segnaposto `%s` viene
sostituito da pn-user-attributes con `String.format`. Il `previewText` serve solo per audit e log.

Impostazioni consigliate in Meta: scadenza del codice di 15 minuti (pari a `pn.user-attributes.verificationcodettl`),
"Aggiungi raccomandazione di sicurezza" attiva.
