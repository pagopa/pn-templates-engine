const generateHtmlTemplates = require("../src/generateHtmlTemplates");
const fs = require("fs-extra");
const path = require("node:path");
const { BASE_OUTPUT_DIR } = require("../src/utils");

const clearOutputTemplates = () => {
  fs.emptyDirSync(path.join(BASE_OUTPUT_DIR, "templates"));
};

describe("generateHtmlTemplates", () => {
  beforeAll(async () => {
    clearOutputTemplates();
    await generateHtmlTemplates();
  });

  it("should generate html templates WITHOUT additional languages", async () => {
    const htmlPath = path.join(
      BASE_OUTPUT_DIR,
      "templates",
      "TemplateTestEmail",
      "TemplateTestEmail.html"
    );

    expect(fs.pathExistsSync(htmlPath)).toEqual(true);

    const indexHtmlContent = await fs.readFile(htmlPath, "utf8");
    expect(indexHtmlContent).toContain("<title>Title template email</title>");
    expect(indexHtmlContent).toContain("<p>Paragraph template email</p>");
  });

  it("should generate html templates WITH additional languages", async () => {
    // IT
    const itPath = path.join(
      BASE_OUTPUT_DIR,
      "templates",
      "TemplateTestI18n",
      "TemplateTestI18n.html"
    );

    expect(fs.pathExistsSync(itPath)).toEqual(true);

    const itHtmlContent = await fs.readFile(itPath, "utf8");
    expect(itHtmlContent).toContain("<title>Titolo</title>");
    expect(itHtmlContent).toContain("<p>Corpo</p>");

    // DE
    const dePath = path.join(
      BASE_OUTPUT_DIR,
      "templates",
      "TemplateTestI18n",
      "TemplateTestI18n_de.html"
    );

    expect(fs.pathExistsSync(dePath)).toEqual(true);

    const deHtmlContent = await fs.readFile(dePath, "utf8");
    expect(deHtmlContent).toContain("<title>Titel • Titolo</title>");
    expect(deHtmlContent).toContain("<p>Körper • Corpo</p>");

    // FR
    const frPath = path.join(
      BASE_OUTPUT_DIR,
      "templates",
      "TemplateTestI18n",
      "TemplateTestI18n_fr.html"
    );

    expect(fs.pathExistsSync(frPath)).toEqual(true);

    const frHtmlContent = await fs.readFile(frPath, "utf8");
    expect(frHtmlContent).toContain("<title>Titre • Titolo</title>");
    expect(frHtmlContent).toContain("<p>Corps • Corpo</p>");
  });

  it("should generate html templates WITH additional languages and specific for it", async () => {
    const itPath = path.join(
      BASE_OUTPUT_DIR,
      "templates",
      "TemplateTestI18nWithIt",
      "TemplateTestI18nWithIt.html"
    );

    expect(fs.pathExistsSync(itPath)).toEqual(true);

    const itHtmlContent = await fs.readFile(itPath, "utf8");
    expect(itHtmlContent).toContain("<title>Il mio titolo diverso</title>");
    expect(itHtmlContent).toContain("<h1>Il mio body diverso</h1>");

    const dePath = path.join(
      BASE_OUTPUT_DIR,
      "templates",
      "TemplateTestI18nWithIt",
      "TemplateTestI18nWithIt_de.html"
    );

    expect(fs.pathExistsSync(dePath)).toEqual(true);

    const deHtmlContent = await fs.readFile(dePath, "utf8");
    expect(deHtmlContent).toContain("<title>Titel • Titolo</title>");
    expect(deHtmlContent).toContain("<p>Körper • Corpo</p>");
  });

  it("should generate template from .txt file", async () => {
    const htmlPath = path.join(
      BASE_OUTPUT_DIR,
      "templates",
      "TemplateTestTxt",
      "TemplateTestTxt.txt"
    );

    expect(fs.pathExistsSync(htmlPath)).toEqual(true);

    const indexHtmlContent = await fs.readFile(htmlPath, "utf8");
    expect(indexHtmlContent).toContain("only test in .txt");
  });
  
  it("should throw an error if fs.readdir fails", async () => {
    jest.spyOn(fs, "readdir").mockImplementationOnce(() => {
      throw new Error("Mocked error");
    });
  
    await expect(generateHtmlTemplates()).rejects.toThrow(
      "Error generating html templates"
    );
  
    fs.readdir.mockRestore();
  });
});