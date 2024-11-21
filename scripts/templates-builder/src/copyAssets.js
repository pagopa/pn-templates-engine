const fs = require("fs-extra");
const path = require("node:path");
const { BASE_SOURCE_DIR, BASE_OUTPUT_DIR } = require("./utils");
const log = require("./logger");

async function copyAssets() {
  log.info("...copying assets folder");

  try {
    const fontDir = path.join(BASE_SOURCE_DIR, "fonts");
    const imagesDir = path.join(BASE_SOURCE_DIR, "images");
    const stylesDir = path.join(BASE_SOURCE_DIR, "styles");
    const templatesDir = path.join(BASE_SOURCE_DIR, "templates");

    await fs.copy(fontDir, path.join(BASE_OUTPUT_DIR, "fonts"));
    await fs.copy(imagesDir, path.join(BASE_OUTPUT_DIR, "images"));
    await fs.copy(stylesDir, path.join(BASE_OUTPUT_DIR, "styles"));

    // Copy only files with .txt extension in templatesDir into the output directory
    const files = await fs.readdir(templatesDir, { withFileTypes: true });
    const fileNames = files
      .filter((file) => path.extname(file.name) === ".txt")
      .map((file) => file.name);
    for (const fileName of fileNames) {
      await fs.copy(
        path.join(templatesDir, fileName),
        path.join(BASE_OUTPUT_DIR, "templates", fileName)
      );
    }

    log.info("Assets copied!");
  } catch (err) {
    log.error("Error copying assets");
    throw err;
  }
}

module.exports = copyAssets;
