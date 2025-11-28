const copyTextTemplates = require("../src/copyTextTemplates");
const fs = require("fs-extra");
const path = require("node:path");
const { BASE_OUTPUT_DIR } = require("../src/utils");

const clearOutputTemplates = () => {
  fs.emptyDirSync(path.join(BASE_OUTPUT_DIR, "templates"));
};

describe("copyTextTemplates", () => {
  beforeAll(async () => {
    clearOutputTemplates();
    await copyTextTemplates();
  });

  it("should copy text template WITHOUT additional languages", async () => {
    const textPath = path.join(
      BASE_OUTPUT_DIR,
      "templates",
      "TemplateTestTxt",
      "TemplateTestTxt.txt"
    );

    expect(fs.pathExistsSync(textPath)).toEqual(true);

    const textContent = await fs.readFile(textPath, "utf8");
    expect(textContent).toContain("only test in .txt");
  });
  
  it.each(["it", "de", "fr", "sl"])("should copy text template WITH %s language", async (lang) => {
    const textPath = path.join(
      BASE_OUTPUT_DIR,
      "templates",
      "TemplateTestTxtWithLang",
      `TemplateTestTxtWithLang${lang === 'it' ? '' : `_${lang}`}.txt`
    );

    expect(fs.pathExistsSync(textPath)).toEqual(true);

    const textContent = await fs.readFile(textPath, "utf8");
    expect(textContent).toContain(`text ${lang}`);
  });

});

describe("copyTextTemplates error", () => {
  it("should throw an error if fs.readdir fails", async () => {
    jest.spyOn(fs, "readdir").mockImplementationOnce(() => {
      throw new Error("Mocked error");
    });
  
    await expect(copyTextTemplates()).rejects.toThrow(
      "Error copying text templates"
    );
  
    fs.readdir.mockRestore();
  });
})