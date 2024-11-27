#!/bin/sh

MAIN_RESOURCES_DIR="./src/main/resources/generated-templates-assets"
TEST_RESOURCES_DIR="./src/test/resources/generated-templates-assets"

# Remove the directories if they exist
if [ -d "$MAIN_RESOURCES_DIR" ]; then
  echo "Removing $MAIN_RESOURCES_DIR..."
  rm -rf "$MAIN_RESOURCES_DIR"
fi

if [ -d "$TEST_RESOURCES_DIR" ]; then
  echo "Removing $TEST_RESOURCES_DIR..."
  rm -rf "$TEST_RESOURCES_DIR"
fi

# Run the template builder script
echo "Running the template generation process..."
cd ./scripts/templates-builder
npm ci
npm test
npm run generate
cd ../..

# Copy the generated templates from main to test resources
if [ -d "$MAIN_RESOURCES_DIR" ]; then
  echo "Copying $MAIN_RESOURCES_DIR to $TEST_RESOURCES_DIR..."
  mkdir -p "$TEST_RESOURCES_DIR"
  cp -R "$MAIN_RESOURCES_DIR/" "$TEST_RESOURCES_DIR/"
else
  echo "Directory $MAIN_RESOURCES_DIR does not exist. Nothing to copy."
  exit 1
fi

echo "Process completed successfully!"