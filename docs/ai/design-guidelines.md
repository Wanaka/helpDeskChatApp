# Design Guidelines

## Core Principle

All designs must follow the official Android Material 3 design system. Use the colors, typography, shapes, and spacing defined by Material 3 — do not invent custom values when a Material token already exists.

## Colors

- Use `MaterialTheme.colorScheme.*` tokens exclusively — never hardcode hex values.
- The color scheme is defined in `ui/theme/Color.kt` and `ui/theme/Theme.kt`. All changes to the palette happen there.
- Prefer semantic tokens (`primary`, `onPrimary`, `surface`, `onSurface`, `outline`, etc.) over direct color references so the app automatically supports light and dark themes.

## Typography

- Use `MaterialTheme.typography.*` styles — `displayLarge`, `headlineMedium`, `bodyLarge`, `bodySmall`, `labelMedium`, etc.
- Do not define custom `TextStyle` objects unless no Material token fits.

## Shapes

- Use `MaterialTheme.shapes.*` — `small`, `medium`, `large`, `extraLarge`.
- Prefer `RoundedCornerShape` with Material-standard radii (4dp, 8dp, 12dp, 16dp, 28dp) when a custom shape is needed.

## Spacing

- Use multiples of 4dp for all padding, margin, and spacing values.
- Common values: 4dp, 8dp, 12dp, 16dp, 24dp, 32dp.

## Components

- Prefer Material 3 built-in components (`Button`, `OutlinedTextField`, `Card`, `TopAppBar`, `NavigationBar`, etc.) before building custom ones.
- When building custom components, wrap them in the `ui/common/components/` folder and make them stateless and reusable.

## References

- Material 3 design system: https://m3.material.io
- Compose Material 3 components: https://developer.android.com/jetpack/compose/designsystems/material3
