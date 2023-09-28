# File Explorer

## Overview

This project is a File Explorer application built using Kotlin and JavaFX. It provides a user-friendly interface to browse files and directories on your local file system. The application allows for various file operations such as renaming, moving, and deleting files or directories. It also supports viewing the content of selected files based on their types.

## Features

### File and Directory Listing
- **Alphabetical Sorting**: Files and directories are displayed in alphabetical order for easy navigation.
- **Test Folder**: The application starts by displaying the contents of a test folder, providing a sandbox environment for safe testing.

### Keyboard and Mouse Navigation
- **Multiple Methods**: Navigate through the file system using keyboard shortcuts, toolbar buttons, or mouse clicks.
- **Status Line**: Always displays the path and filename of the currently selected item for better user awareness.

### File Operations
- **Rename**: Easily rename files or directories with a prompt for the new name. Invalid names are flagged with an error message.
- **Move**: Move files or directories to different locations through a File Chooser dialog box.
- **Delete**: Safely delete files or directories after a confirmation prompt.

### File Preview
- **Text and Image Support**: Preview the contents of text files and view images directly within the application.
- **Unsupported Types**: For unsupported or unreadable files, appropriate messages are displayed.

### Status Bar
- **Detailed Information**: The status bar at the bottom provides detailed information about the selected file, including its full path and filename.

### Resizable and Usable UI
- **Ease-of-Use**: The UI is designed for ease-of-use and can be resized according to user preference.
- **Toolbar and Menu**: A feature-rich toolbar and menu system for easy navigation and file manipulation.

## Technical Stack

- **Programming Language**: Kotlin 1.8
- **Framework**: JavaFX 18
- **Build Tool**: Gradle
- **IDE**: IntelliJ 2023.1.1

## Prerequisites

- OpenJDK 17
- Kotlin 1.8 or later
- JavaFX 18

## How to Run
1. Download the code.
2. Open it in any IDE you prefer.
3. Build the project using Gradle.
4. Run it!

## Testing

The application has been tested in a sandbox environment to ensure that file operations are safe and do not affect important system files. The test directory structure is included in the project.

