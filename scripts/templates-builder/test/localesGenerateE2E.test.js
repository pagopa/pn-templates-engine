const { execSync } = require("node:child_process");
const fs = require("fs-extra");
const path = require("node:path");

/**
 * Test end-to-end di migrazione: lancia realmente i comandi di build
 *   - `npm run generate`          (modalità legacy, traduzioni da i18n/)
 *   - `npm run generate-locales`  (modalità locales, traduzioni da locales/)
 * e verifica che l'HTML generato per un template gia' migrato sia IDENTICO
 * nelle due modalita'.
 *
 * Esegue la build reale: scrive in src/main/resources/generated-templates-assets
 * (artefatto gitignored). È un test transitorio, da rimuovere quando la cartella
 * i18n legacy del template verra' eliminata.
 */
const TEMPLATE = "AnalogDeliveryWorkflowFailureLegalFact";
const BUILDER_DIR = path.join(__dirname, "..");
const GENERATED_TEMPLATE_DIR = path.join(
  BUILDER_DIR,
  "../../src/main/resources/generated-templates-assets/templates",
  TEMPLATE
);

const OUTPUT_FILES = [
  `${TEMPLATE}.html`,
  `${TEMPLATE}_de.html`,
  `${TEMPLATE}_en.html`,
  `${TEMPLATE}_fr.html`,
  `${TEMPLATE}_sl.html`,
];

function readGeneratedOutputs() {
  const outputs = {};
  for (const file of OUTPUT_FILES) {
    outputs[file] = fs.readFileSync(
      path.join(GENERATED_TEMPLATE_DIR, file),
      "utf8"
    );
  }
  return outputs;
}

describe(`generate vs generate-locales (E2E) - ${TEMPLATE}`, () => {
  let legacyOutputs;
  let localesOutputs;

  beforeAll(() => {
    const run = (script) =>
      execSync(`npm run ${script}`, { cwd: BUILDER_DIR, stdio: "ignore" });

    // Prima la modalita' locales, poi la legacy: cosi' l'artefatto finale
    // lasciato sul filesystem e' quello prodotto dal comando canonico.
    run("generate-locales");
    localesOutputs = readGeneratedOutputs();

    run("generate");
    legacyOutputs = readGeneratedOutputs();
  }, 120000);

  it.each(OUTPUT_FILES)(
    "%s e' identico tra generate e generate-locales",
    (file) => {
      expect(localesOutputs[file]).toEqual(legacyOutputs[file]);
    }
  );
});
