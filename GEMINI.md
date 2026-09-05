# Antigravity Project Rules & Standards

## Strict Secret Protection & Zero Exposure Rule

1. **Zero Secret Git & GitHub Commits**:
   - NEVER commit, push, or stage any secret, API key, token, private credential, service role key, keystore, or password to Git, GitHub, or any remote repository.
   - All secret files (`.env`, `.env.*`, `*.keystore`, `*.jks`, `service-account*.json`, private keys) MUST remain strictly ignored in `.gitignore`.
   - Before executing `git commit` or `git add`, always verify that untracked or staged files do not contain credentials, secret tokens, or private environment files.

2. **Zero In-Code Secret Exposure**:
   - NEVER hardcode, embed, or expose secrets, passwords, private keys, database connection strings with credentials, or API tokens directly in source code or UI code.
   - Sensitive values must strictly be accessed via secure environment variables (`.env`), system environment, or encrypted keyrings.

3. **Explicit User Permission Required**:
   - ALWAYS ASK FIRST: If you ever need to introduce, configure, or use any secret, token, or credential, you MUST explicitly ask the user for confirmation and permission first.
