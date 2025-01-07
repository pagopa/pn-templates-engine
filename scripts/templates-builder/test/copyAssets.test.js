const copyAssets = require("../src/copyAssets");
const fs = require("fs-extra");
const path = require("node:path");
const { BASE_OUTPUT_DIR } = require("../src/utils");

const clearOutputAssets = () => {
  fs.emptyDirSync(path.join(BASE_OUTPUT_DIR, "fonts"));
  fs.emptyDirSync(path.join(BASE_OUTPUT_DIR, "styles"));
  fs.emptyDirSync(path.join(BASE_OUTPUT_DIR, "images"));
};

describe("copyAssets", () => {
  beforeAll(async () => {
    clearOutputAssets();
    await copyAssets();
  });

  it.each([["fonts"], ["images"], ["styles"]])(
    "should copy assets %s with all files",
    async (assetPath) => {
      const assetExists = await fs.pathExists(
        path.join(BASE_OUTPUT_DIR, assetPath)
      );

      const files = await fs.readdir(path.join(BASE_OUTPUT_DIR, assetPath));
      expect(files.length).toBeGreaterThan(0);

      expect(assetExists).toEqual(true);
    }
  );

  it("should throw an error if copying fails", async () => {
    const copySpy = jest.spyOn(fs, "copy").mockImplementation(() => {
      throw new Error("Copy failed");
    });
  
    await expect(copyAssets()).rejects.toThrow("Error copying assets");
    copySpy.mockRestore();
  });
});