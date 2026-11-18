import { describe, expect, it } from "vitest";
import { portfolioResponseSchema } from "../../shared/schemas";

describe("portfolio contract", () => {
  it("accepts the configured public shape", () => {
    expect(() => portfolioResponseSchema.parse({
      profile: {
        name: "Sirbon",
        headline: "A headline",
        bio: "A bio",
        location: "South Africa",
        email: "hello@example.com",
        githubUrl: "https://github.com/",
        availability: "available",
      },
      projects: [],
    })).not.toThrow();
  });
});
