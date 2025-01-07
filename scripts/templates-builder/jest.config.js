/** @type {import('jest').Config} */
const config = {
  setupFilesAfterEnv: ["<rootDir>/test/setupTests.js", "jest-extended/all"],
  coverageDirectory: "<rootDir>/test/coverage/",
};

module.exports = config;
