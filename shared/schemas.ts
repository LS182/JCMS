import { z } from "zod";

export const projectSchema = z.object({
  id: z.string().min(1),
  slug: z.string().regex(/^[a-z0-9-]+$/),
  title: z.string().min(1).max(120),
  summary: z.string().min(1).max(300),
  description: z.string().min(1),
  url: z.string().url().optional(),
  repositoryUrl: z.string().url().optional(),
  tags: z.array(z.string().min(1)).max(12),
  featured: z.boolean(),
  published: z.boolean(),
  sortOrder: z.number().int(),
  updatedAt: z.string().datetime(),
});

export const profileSchema = z.object({
  name: z.string().min(1),
  headline: z.string().min(1),
  bio: z.string().min(1),
  location: z.string().min(1),
  email: z.string().email(),
  githubUrl: z.string().url(),
  availability: z.enum(["available", "limited", "unavailable"]),
});

export const portfolioResponseSchema = z.object({
  profile: profileSchema,
  projects: z.array(projectSchema),
});

export type Project = z.infer<typeof projectSchema>;
export type Profile = z.infer<typeof profileSchema>;
export type PortfolioResponse = z.infer<typeof portfolioResponseSchema>;
