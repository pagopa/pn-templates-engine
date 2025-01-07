const build = require("../src/script");
const buildMjml = require("../src/buildMjml");
const copyAssets = require("../src/copyAssets");
const generateHtmlTemplate = require("../src/generateHtmlTemplates");

jest.mock("./../src/buildMjml");
jest.mock("./../src/copyAssets");
jest.mock("./../src/generateHtmlTemplates");

describe("build", () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it("should execute copyAssets, buildMjml, and generateHtmlTemplate in order", async () => {
    await build();

    expect(copyAssets).toHaveBeenCalledTimes(1);
    expect(buildMjml).toHaveBeenCalledTimes(1);
    expect(generateHtmlTemplate).toHaveBeenCalledTimes(1);

    expect(copyAssets).toHaveBeenCalledBefore(buildMjml);
    expect(buildMjml).toHaveBeenCalledBefore(generateHtmlTemplate);
  });
});