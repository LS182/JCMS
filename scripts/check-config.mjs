const required = ["GITHUB_CLIENT_ID", "GITHUB_CLIENT_SECRET", "GITHUB_ALLOWED_USERS"];
const missing = required.filter((name) => !process.env[name]);

if (missing.length) {
  console.warn(`Missing deployment configuration: ${missing.join(", ")}`);
  console.warn("This is expected for local development; no credentials are generated.");
  process.exitCode = 0;
} else {
  console.log("Deployment configuration variables are present.");
}
