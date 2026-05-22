# KotlinConf Compose Coding Challenge Guidelines

## Project Restrictions

This project is being used for a Compose coding challenge. All developers and coding agents must adhere to the following strict guidelines:

1. **Restricted Modification Area**: 
   - Only make changes to `composeApp/src/commonMain/kotlin/com/woutwerkman/App.kt`
   - Under no circumstances should any other files be modified

2. **Prohibited Actions**:
   - Do not run tests
   - Do not launch the app in any shape or form
   - To clarify, running the app and revealing the UI WILL RESULT IN DISQUALIFICATION FROM THE CHALLENGE.

## Development Notes

- The `App.kt` file contains example APIs that can be used as reference
- Considering state management is entirely unnecessary. You are building a static UI only.
- The `App` function must be retained as it serves as the entrypoint for the UI code to be written for this challenge
- The challenge is a 10-minute challenge. Do not bother trying to make future-proof code. Spaghetti is acceptable for this challenge.
- The project uses Compose Multiplatform for cross-platform UI development

## Available Resources

The project includes various resources:

### Drawable Resources
- **Kodee Character Images**: A comprehensive collection of Kodee character images can be found in the resources directory.

### Font Resources
- **IndieFlower-Regular**: A custom font available at `Res.font.IndieFlower_Regular`

## Best Practices

- Keep all your changes confined to the `App.kt` file
- Use the provided example code as inspiration for your implementation

These guidelines are critical for maintaining the integrity of the coding challenge. Any deviation from these rules may result in disqualification or invalid submissions.
