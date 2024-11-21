const path = require("node:path");
const {
  getOutputTemplateName,
  getOutputFileName,
  loadTranslations,
  BASE_SOURCE_DIR,
} = require("../src/utils");

describe("getOutputTemplateName", () => {
  it.each([
    ["Some_Other_Template", "Some_Other_Template"],
    ["Mail_VerificationCode", "emailbody"],
    ["PEC_VerificationCodeMessage", "pecbody"],
    ["PEC_ValidationContactsSuccessMessage", "pecbodyconfirm"],
    ["PEC_ValidationContactsRejectMessage", "pecbodyreject"],
  ])(
    "should return the renamed template name for %s",
    (input, expected) => {
      expect(getOutputTemplateName(input)).toBe(expected);
    }
  );
});

describe("getOutputFileName", () => {
  it.each([
    ["TestTemplateName", undefined, "TestTemplateName"],
    ["TestTemplateName", "it", "TestTemplateName"],
    ["TestTemplateName", "en", "TestTemplateName_en"],
    ["TestTemplateName", "fr", "TestTemplateName_fr"],
    ["Mail_VerificationCode", "en", "emailbody_en"],
    ["PEC_VerificationCodeMessage", "fr", "pecbody_fr"],
  ])(
    "should return the correct output file name for template %s with language %s",
    (templateName, lang, expected) => {
      expect(getOutputFileName(templateName, lang)).toBe(expected);
    }
  );
});

describe("loadTranslations", () => {
  it.each([
    [
      "de",
      { it_title: "Titolo", it_body: "Corpo", title: "Titel", body: "Körper" },
    ],
    [
      "fr",
      { it_title: "Titolo", it_body: "Corpo", title: "Titre", body: "Corps" },
    ],
  ])(
    "should return right translations for a folder template for language %s",
    async (lang, expectedTranslations) => {
      const translations = await loadTranslations(
        lang,
        path.join(BASE_SOURCE_DIR, "templates", "TemplateTestI18n")
      );
      expect(translations).toEqual(expectedTranslations);
    }
  );
});
