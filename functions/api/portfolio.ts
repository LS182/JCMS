import { portfolioResponseSchema } from "../../shared/schemas";

const configured = {
  profile: {
    name: "Sirbon",
    headline: "Building calm, useful software for the web.",
    bio: "Configure this profile through D1 or a trusted content source before publishing.",
    location: "South Africa",
    email: "hello@example.com",
    githubUrl: "https://github.com/",
    availability: "available" as const,
  },
  projects: [],
};

export const onRequestGet = () =>
  Response.json(portfolioResponseSchema.parse(configured), {
    headers: { "Cache-Control": "public, max-age=60" },
  });
