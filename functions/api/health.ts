export const onRequestGet = () =>
  Response.json({ ok: true, service: "sirbon.dev", timestamp: new Date().toISOString() });
