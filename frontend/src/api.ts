import { portfolioResponseSchema, type PortfolioResponse } from "../../shared/schemas";

const fallback: PortfolioResponse = {
  profile: {
    name: "Sirbon",
    headline: "Building calm, useful software for the web.",
    bio: "I design and ship thoughtful products with a bias toward clarity, accessibility, and durable foundations.",
    location: "South Africa",
    email: "hello@example.com",
    githubUrl: "https://github.com/",
    availability: "available",
  },
  projects: [
    {
      id: "demo-project",
      slug: "demo-project",
      title: "A considered starting point",
      summary: "A small, focused project with a clear purpose and a maintainable core.",
      description: "This placeholder is intentionally transparent until portfolio content is configured.",
      tags: ["TypeScript", "Design systems"],
      featured: true,
      published: true,
      sortOrder: 1,
      updatedAt: new Date(0).toISOString(),
    },
  ],
};

export async function fetchPortfolio(): Promise<PortfolioResponse> {
  try {
    const response = await fetch("/api/portfolio");
    if (!response.ok) throw new Error(`Portfolio request failed: ${response.status}`);
    return portfolioResponseSchema.parse(await response.json());
  } catch {
    return fallback;
  }
}
