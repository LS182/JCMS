import { useEffect, useState, type ReactNode } from "react";
import { Github, ArrowUpRight, Mail, MapPin, Menu, X } from "lucide-react";
import { Link, Route, Routes, useLocation } from "react-router-dom";
import { fetchPortfolio } from "./api";
import type { PortfolioResponse, Project } from "../../shared/schemas";

function Shell({ children }: { children: ReactNode }) {
  const [open, setOpen] = useState(false);
  const location = useLocation();
  useEffect(() => setOpen(false), [location.pathname]);
  return (
    <div className="min-h-screen bg-[#f7f5ef] text-[#15221f]">
      <header className="sticky top-0 z-10 border-b border-[#15221f]/10 bg-[#f7f5ef]/90 backdrop-blur">
        <div className="mx-auto flex max-w-6xl items-center justify-between px-6 py-5">
          <Link className="font-mono text-lg font-bold tracking-tight" to="/">sirbon.dev</Link>
          <button className="rounded-lg p-2 md:hidden" onClick={() => setOpen(!open)} aria-label="Toggle navigation">
            {open ? <X size={20} /> : <Menu size={20} />}
          </button>
          <nav className={`${open ? "flex" : "hidden"} absolute left-0 right-0 top-full flex-col gap-4 border-b border-[#15221f]/10 bg-[#f7f5ef] px-6 py-5 md:static md:flex md:flex-row md:border-0 md:bg-transparent md:p-0`}>
            <Link className="text-sm hover:text-[#cf5c36]" to="/">Home</Link>
            <Link className="text-sm hover:text-[#cf5c36]" to="/work">Work</Link>
            <a className="text-sm hover:text-[#cf5c36]" href="#contact">Contact</a>
          </nav>
        </div>
      </header>
      <main>{children}</main>
      <footer id="contact" className="border-t border-[#15221f]/10">
        <div className="mx-auto flex max-w-6xl flex-col gap-4 px-6 py-10 text-sm text-[#15221f]/70 md:flex-row md:items-center md:justify-between">
          <span>Built with care. Content is configured, not fabricated.</span>
          <div className="flex gap-4">
            <a href="mailto:hello@example.com" aria-label="Email"><Mail size={18} /></a>
            <a href="https://github.com/" aria-label="GitHub"><Github size={18} /></a>
          </div>
        </div>
      </footer>
    </div>
  );
}

function ProjectCard({ project }: { project: Project }) {
  return (
    <article className="group rounded-2xl border border-[#15221f]/10 bg-white/50 p-6 transition hover:-translate-y-1 hover:border-[#cf5c36]/50">
      <div className="mb-10 flex items-start justify-between gap-4">
        <span className="font-mono text-xs uppercase tracking-[0.2em] text-[#cf5c36]">Project</span>
        {project.url && <a href={project.url} aria-label={`Open ${project.title}`}><ArrowUpRight size={20} /></a>}
      </div>
      <h3 className="mb-3 text-2xl font-semibold">{project.title}</h3>
      <p className="mb-5 text-[#15221f]/70">{project.summary}</p>
      <div className="flex flex-wrap gap-2">{project.tags.map((tag) => <span className="rounded-full bg-[#15221f]/5 px-3 py-1 text-xs" key={tag}>{tag}</span>)}</div>
    </article>
  );
}

function Home({ data }: { data: PortfolioResponse }) {
  const featured = data.projects.filter((project) => project.featured && project.published);
  return (
    <>
      <section className="mx-auto grid max-w-6xl gap-12 px-6 pb-24 pt-24 md:grid-cols-[1.3fr_1fr] md:items-end md:pt-36">
        <div>
          <p className="mb-6 font-mono text-sm uppercase tracking-[0.25em] text-[#cf5c36]">Independent software maker</p>
          <h1 className="max-w-3xl text-5xl font-semibold leading-[0.98] tracking-[-0.04em] md:text-7xl">{data.profile.headline}</h1>
        </div>
        <div className="max-w-md space-y-6 text-lg text-[#15221f]/70">
          <p>{data.profile.bio}</p>
          <div className="flex items-center gap-2 text-sm"><MapPin size={16} />{data.profile.location}</div>
          <a className="inline-flex items-center gap-2 font-medium text-[#cf5c36] hover:underline" href="#contact">Start a conversation <ArrowUpRight size={16} /></a>
        </div>
      </section>
      <section className="mx-auto max-w-6xl px-6 pb-28">
        <div className="mb-8 flex items-end justify-between"><h2 className="text-3xl font-semibold">Selected work</h2><Link className="text-sm hover:text-[#cf5c36]" to="/work">View all →</Link></div>
        <div className="grid gap-5 md:grid-cols-2">{featured.map((project) => <ProjectCard key={project.id} project={project} />)}</div>
      </section>
    </>
  );
}

function Work({ data }: { data: PortfolioResponse }) {
  return <section className="mx-auto max-w-6xl px-6 py-20"><p className="mb-5 font-mono text-sm uppercase tracking-[0.25em] text-[#cf5c36]">Archive</p><h1 className="mb-12 text-5xl font-semibold tracking-[-0.04em]">Work</h1><div className="grid gap-5 md:grid-cols-2">{data.projects.filter((project) => project.published).map((project) => <ProjectCard key={project.id} project={project} />)}</div></section>;
}

export default function App() {
  const [data, setData] = useState<PortfolioResponse | null>(null);
  useEffect(() => { fetchPortfolio().then(setData); }, []);
  if (!data) return <div className="grid min-h-screen place-items-center bg-[#f7f5ef] font-mono text-sm">Loading portfolio…</div>;
  return <Shell><Routes><Route path="/" element={<Home data={data} />} /><Route path="/work" element={<Work data={data} />} /></Routes></Shell>;
}
