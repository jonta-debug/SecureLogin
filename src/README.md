SecureLogin

SecureLogin is a small authentication project built with Java Swing and PostgreSQL.

I started this project to get more familiar with authentication, password hashing, database interactions and two-factor authentication in Java. The goal was to build a working login system without relying on a complete authentication framework.

Features

* User registration and login
* Password hashing with Argon2id
* Email-based two-factor authentication (2FA)
* Six-digit verification codes
* Verification code expiration and attempt limits
* PostgreSQL database integration
* Java Swing user interface
* Background tasks using SwingWorker to keep the UI responsive

Tech Stack

* Java
* Java Swing
* PostgreSQL
* JDBC
* Maven
* Argon2id
* Jakarta Mail
* HMAC-SHA256

How It Works

When a user registers, their password is hashed using Argon2id before being stored in the database.

During login, the entered password is checked against the stored hash. If the credentials are correct, the application generates a six-digit verification code and sends it to the user’s email address.

The user must enter this code to complete the authentication process.

Verification codes are generated using SecureRandom. They are stored as HMAC-SHA256 values rather than plaintext and can only be used once.

Setup

Requirements

* Java 21 or newer
* Maven
* PostgreSQL
* A Gmail account with an app password for sending verification emails

Installation

1. Clone the repository.
2. Create a PostgreSQL database.
3. Configure the database connection.
4. Set up the required environment variables.
5. Build and run the project.

The application uses environment variables for sensitive configuration, including the email credentials and the secret used for verification codes.

Make sure these values are configured before starting the application.

Project Status

The basic authentication flow is working, including registration, login and email verification.

I’m currently working on improving the user interface and extending the application.

Some things I might add later:

* User sessions
* A dashboard after successful login
* Password reset functionality
* Additional account security features

Notes

This is a learning project and not intended to be used as a production-ready authentication system.

I’m building it mainly to improve my understanding of Java, databases and authentication security.