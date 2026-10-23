# Smart Pantry Manager

A Java Android application for recording pantry ingredients and finding
recipes that can be prepared using the quantities already available.

## Features

- Add, view, edit and delete pantry ingredients.
- Optional expiry dates with date validation.
- Twenty recipes seeded when the database is first created.
- Strict suggestions requiring every ingredient in sufficient quantity.
- Common ingredient plural handling.
- Kilogram-to-gram and litre-to-millilitre conversion.
- Duplicate pantry quantities combined during matching.
- A separate full recipe collection browsing mode.
- Recipe ingredients and preparation instructions.
- A persistent default-unit preference.
- Bottom navigation between pantry, recipes and settings.

## Technology

Application code: Java.
Interface: XML layouts and standard Android Views constructed in Java.
Database: SQLite through SQLiteOpenHelper.
Preferences: SharedPreferences.

SQLite was selected because the application is small, works offline and
does not require an account, external server or internet connection.

## Setup

1. Open the project folder in Android Studio.
2. Allow Gradle to synchronise and install any requested SDK components.
3. Use the included Gradle wrapper and project build configuration.
4. Select an Android device or emulator running API 24 or later.
5. Build and run the app configuration.

The project compiles against API 37. Testing can use an older supported
device, such as an Android 10 / API 29 emulator.

Recipes are seeded automatically on first database creation.
The pantry starts empty.

## Screen flow

Pantry -> Add/Edit Ingredient -> Pantry
Pantry <-> Recipes <-> Settings
Recipes -> Recipe Details -> Recipes

## Database

pantry:
id, name, quantity, unit, expiry_date

recipes:
id, name, instructions

recipe_ingredients:
id, recipe_id, name, quantity, unit

recipe_ingredients.recipe_id references recipes.id.

## Matching rules

Every required ingredient must exist in a sufficient combined quantity.
Names are trimmed, compared without case sensitivity and normalised using
explicit aliases for common plurals.

Compatible quantities are converted to grams or millilitres before
comparison. Incompatible units do not match.

Raw and cooked ingredients remain distinct. Viewing suggestions does not
consume pantry stock. Expiry dates are informational and do not currently
exclude ingredients from matching.

## Manual verification

- Empty and invalid quantities are rejected.
- Invalid calendar dates are rejected.
- Pantry records can be created, edited and deleted.
- Cancelling deletion preserves the record.
- Pantry records remain after restarting the app.
- Missing or insufficient ingredients exclude recipes from suggestions.
- Common plurals and compatible unit conversions work.
- Browsing all recipes is clearly separate from strict suggestions.
- Recipe details show all requirements and preparation instructions.
- The saved default unit is applied to new ingredients.

## Manual Testing - Android 10 (API 29)

The application was manually tested using 
the Android Studio Emulator.

The following functionality was verified:
- Creating and displaying pantry ingredients.
- Updating existing ingredient quantities.
- Deleting pantry ingredients.
- Input Validation.
- Strict recipe matching.
- Displaying recipe details.
- Navigation between application screens.
- Saving Application settings.
- SQLite data persistence after reopening.

Screenshots of the tested application screens are
included in the assignment report.
