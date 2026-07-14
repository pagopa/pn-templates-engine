<#-- Note: All the if conditions are required for correct spaces in layout between paragraphs -->
Ciao ${recipient.denomination},

${body.primaryContent}
<#if hasAttachment>

Per avere maggiori informazioni **prendi visione degli allegati**, che possono fornirti dettagli importanti. Ma ricorda: saranno disponibili online per un periodo limitato, quindi salvali sul tuo dispositivo.
</#if>
<#if hasPayment>

Puoi effettuare il pagamento direttamente sull'app IO premendo **Paga**. In alternativa, puoi utilizzare l'**avviso allegato** per saldare l'importo tramite tutti i canali abilitati a pagoPA.
</#if>

In ogni caso, qualora avessi bisogno di assistenza, **contatta ${sender.denomination} attraverso i suoi canali ufficiali**.
