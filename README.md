# Telegram CV Generator Bot

This is a Telegram bot built with Java that helps users create a professional PDF CV step by step.

## Features

- Collects user data step by step via Telegram messages
- Generates a clean, formatted PDF CV automatically
- Sends the generated CV to the user
- Built with Java 17, Spring Boot, and TelegramBots API
- Uses iText PDF for PDF generation

## Bot Flow

1. User starts the bot with `/start`
2. Bot asks for user info in steps:
   - Full Name
   - lindedin link
   - Email
   - Phone Number
   - Job Title
   - Work Experience
   - Education
   - Skills
   - Languages
   - Military status
     
3. After all data is collected, the bot generates a PDF CV
4. Bot sends the PDF back to the user

## Technologies

- Java 21
- TelegramBots API
- iText 7 PDF library
- Maven

