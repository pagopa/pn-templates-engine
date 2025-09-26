# pn-templates-engine

To generate templates
```bash
    ./build-templates.sh
```


To start application run

```bash
    ./mvnw clean install
```


To generate templates and start application run
```bash
     ./build-templates.sh && ./mvnw spring-boot:run
```

## Design of dima Postel

Put this html in <main> tag of aar document

```html
<!-- Dima postalizzatore -->
<div style="height: 96mm; width: 210mm; border-bottom: 1px dotted red; position:absolute; box-sizing: border-box">
    <div style="height: 23mm; width: 65mm;  position:absolute; top: 14mm; left: 8mm;   border: 1px dotted blue;  box-sizing: border-box">
        <div style="position: absolute; top: 3mm; left: 24mm; font-size: 12px; line-height: 1;">
            In caso di mancato recapito<br/>restituire a:<br/>POSTEL - PAGOPA<br/>VIA AUGUSTO ERBA 15<br/>20055 MELZO, MI
        </div>
    </div>
    <div style="height: 32mm; width: 75mm;  position:absolute; top: 55mm; left: 10mm;  border: 1px dotted green; box-sizing: border-box"></div>
    <div style="height: 43mm; width: 100mm; position:absolute; top: 44mm; left: 100mm; border: 1px dotted red;   box-sizing: border-box"></div>
    <div style="height: 19mm; width: 100mm; position:absolute; top: 44mm; left: 100mm; box-sizing: border-box; border-bottom: 1px dotted red;"></div>
</div>

<!-- linee pieghe in 3 -->
<div style="height: 99mm; width:210mm; position: absolute; top: 99mm; border-bottom: 2px dotted purple; border-top: 2px dotted purple; box-sizing: border-box"></div>
```