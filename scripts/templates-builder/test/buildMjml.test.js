const path = require("node:path");
const { BASE_SOURCE_DIR } = require("../src/utils");
const buildMjml = require("../src/buildMjml");
const fs = require("fs-extra");

describe("processMJMLFiles", () => {
  beforeAll(async () => {
    await buildMjml();
  });

  afterEach(() => {
    jest.restoreAllMocks();
  });


  it("should process MJML files and generate correct index.html", async () => {
    const indexHtml = path.join(
      BASE_SOURCE_DIR,
      "templates/TemplateTestEmail",
      "index.html"
    );

    // Check if the generated index.html file contains the expected content
    const indexHtmlContent = fs.readFileSync(indexHtml, "utf8");
    expect(indexHtmlContent).toContain("<!doctype html>");
    expect(indexHtmlContent).toContain("<title>Title template email</title>");
    expect(indexHtmlContent).toContain("<p>Paragraph template email</p>");
  });

  it("should skip processing 'partials' directories", async () => {
    const partialsHtml = path.join(
      BASE_SOURCE_DIR,
      "templates",
      "partials.html"
    );

    const partialsHtmlExists = await fs.pathExists(partialsHtml);
    expect(partialsHtmlExists).toEqual(false);
  });

  it("should throw an error if fs.readdir fails", async () => {
    jest.spyOn(fs, "readdir").mockImplementationOnce(() => {
      throw new Error("Mocked error");
    });
  
    await expect(buildMjml()).rejects.toThrow(
      "Error processing mjml"
    );
  
    fs.readdir.mockRestore();
  });
});