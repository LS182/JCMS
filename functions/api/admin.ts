type AdminContext = { request: Request; env?: { ADMIN_TOKEN?: string } };

export const onRequestGet = ({ request, env }: AdminContext) => {
  const configuredToken = env?.ADMIN_TOKEN;
  if (!configuredToken) {
    return Response.json({ error: "Admin authentication is not configured" }, { status: 503 });
  }
  if (request.headers.get("Authorization") !== `Bearer ${configuredToken}`) {
    return Response.json({ error: "Unauthorized" }, { status: 401 });
  }
  return Response.json({ message: "Admin boundary is active. Configure GitHub OAuth before use." });
};
