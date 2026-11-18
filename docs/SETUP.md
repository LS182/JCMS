# Sirbon.dev setup

The Sirbon.dev application is an additive web monorepo layer. The existing
Android and Spring Boot JCMS projects remain unchanged.

## Local frontend

```bash
npm install
npm run dev
```

The frontend uses a transparent fallback dataset while `/api/portfolio` is not
available. Replace the placeholder profile and project records before launch.

## Cloudflare Pages Functions and D1

1. Apply `migrations/0001_init.sql` to a D1 database.
2. Bind the database as `DB` in the Pages project configuration.
3. Replace `functions/api/portfolio.ts` with a D1-backed reader using the
   shared schemas.
4. Configure GitHub OAuth at deploy time; never commit client secrets.
5. Protect every admin mutation with a verified GitHub identity and an
   allowlist of repository owners.

No Cloudflare account operations, deployments, OAuth credentials, or private
repository data are included in this repository.

## Configuration checklist

- `GITHUB_CLIENT_ID` and `GITHUB_CLIENT_SECRET` are deployment secrets.
- `GITHUB_ALLOWED_USERS` is a comma-separated allowlist.
- `DB` is the D1 binding name expected by the Pages Functions.
- Replace `hello@example.com` and the placeholder GitHub URL with approved
  public content.
