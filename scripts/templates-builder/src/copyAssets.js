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

    await fs.copy(fontDir, path.join(BASE_OUTPUT_DIR, "fonts"));
    await fs.copy(imagesDir, path.join(BASE_OUTPUT_DIR, "images"));
    await fs.copy(stylesDir, path.join(BASE_OUTPUT_DIR, "styles"));

    log.info("Assets copied!");
  } catch (err) {
    log.error("Error copying assets");
    throw err;
  }
}

module.exports = copyAssets;
