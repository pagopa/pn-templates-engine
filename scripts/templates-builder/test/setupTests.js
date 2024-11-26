const path = require("node:path");
const utils = require("../src/utils");

const MOCK_OUTPUT_DIR = path.join(__dirname, "./mock/output");
const MOCK_SOURCE_DIR = path.join(__dirname, "./mock/source");

jest.replaceProperty(utils, "BASE_SOURCE_DIR", MOCK_SOURCE_DIR);
jest.replaceProperty(utils, "BASE_OUTPUT_DIR", MOCK_OUTPUT_DIR);