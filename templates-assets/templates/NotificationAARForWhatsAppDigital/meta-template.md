# NotificationAARForWhatsAppDigital – template da registrare su Meta

Fonte di verità del testo approvato da Meta per il messaggio di cortesia WhatsApp in modalità **DIGITAL**.
Il file `index.txt` produce il descrittore (`templateName`, `language`, `bodyParameters`, `previewText`) usato da pn-ec:
**qualsiasi modifica al testo va fatta qui, in `index.txt` (previewText) e sottomessa a Meta** prima del rilascio.

| Nome template Meta | Categoria | Lingua | Destinatario |
|---|---|---|---|
| `send_avviso_notifica_digitale` | UTILITY | it | Persona fisica |
| `send_avviso_notifica_digitale_pg` | UTILITY | it | Persona giuridica |

## send_avviso_notifica_digitale (body)

```
Hai ricevuto una notifica SEND da {{1}} con Codice IUN {{2}}. Per leggerla, accedi a SEND - Servizio Notifiche Digitali. La notifica risulterà legalmente consegnata a te dopo 7 giorni dalla ricezione.
```

## send_avviso_notifica_digitale_pg (body)

```
La tua impresa ha ricevuto una notifica SEND da {{1}} con Codice IUN {{2}}. Per leggerla, accedi a SEND - Servizio Notifiche Digitali. La notifica risulterà legalmente consegnata dopo 7 giorni dalla ricezione.
```

Parametri: `{{1}}` = denominazione dell'ente mittente, `{{2}}` = IUN.
Esempi per la revisione Meta: `{{1}}` = "Comune di Milano", `{{2}}` = "ABCD-EFGH-IJKL-202610-M-1".
