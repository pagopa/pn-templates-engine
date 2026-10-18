# NotificationAARForWhatsAppAnalog – template da registrare su Meta

Fonte di verità del testo approvato da Meta per il messaggio di cortesia WhatsApp in modalità **ANALOG**
(nessun domicilio digitale: se il destinatario legge entro 120 ore, la raccomandata non viene spedita).
Ogni modifica va riportata anche nel `previewText` di `index.txt` e sottomessa a Meta.

| Nome template Meta | Categoria | Lingua | Destinatario |
|---|---|---|---|
| `send_avviso_notifica_analogica` | UTILITY | it | Persona fisica |
| `send_avviso_notifica_analogica_pg` | UTILITY | it | Persona giuridica |

## send_avviso_notifica_analogica (body)

```
Hai ricevuto una notifica da {{1}} con Codice IUN {{2}}. Per leggerla, accedi con SPID o CIE al sito di SEND - Servizio Notifiche Digitali.
```

## send_avviso_notifica_analogica_pg (body)

```
La tua impresa ha ricevuto una notifica da {{1}} con Codice IUN {{2}}. Per leggerla, accedi con SPID o CIE al sito di SEND - Servizio Notifiche Digitali.
```

Parametri: `{{1}}` = denominazione dell'ente mittente, `{{2}}` = IUN.
