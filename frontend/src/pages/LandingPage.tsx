import { Link } from 'react-router-dom'
import { Plane, ArrowRight } from 'lucide-react'

const FEATURES = [
  { label: 'Flight log',       desc: 'Per-flight times, routes, aircraft, remarks.' },
  { label: 'Live weather',     desc: 'Live and historical METAR for any ICAO.' },
  { label: 'Snapshots',        desc: 'Departure and arrival weather captured automatically.' },
  { label: 'Map view',         desc: 'See every leg you have flown on one map.' },
]

export default function LandingPage() {
  return (
    <div className="min-h-dvh bg-zinc-950 text-zinc-100 flex flex-col">
      <header className="border-b border-zinc-800">
        <div className="max-w-screen-md mx-auto px-6 h-14 flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Plane size={16} className="text-amber-500" />
            <span className="font-mono text-sm">pilotlogbook</span>
          </div>
          <Link
            to="/email"
            className="text-sm text-zinc-400 hover:text-amber-400 transition-colors"
          >
            Sign in
          </Link>
        </div>
      </header>

      <main className="flex-1">
        <section className="max-w-screen-md mx-auto px-6 py-20">
          <p className="font-mono text-xs uppercase tracking-wider text-amber-500 mb-4">
            Digital logbook for pilots
          </p>
          <h1 className="text-4xl md:text-5xl text-zinc-100 mb-4 leading-tight">
            Log your flights.<br />
            Track your hours.
          </h1>
          <p className="text-zinc-400 max-w-md mb-8">
            A no-nonsense logbook with route maps and METAR snapshots. Built for private and commercial pilots.
          </p>

          <Link
            to="/email"
            className="inline-flex items-center gap-2 px-4 py-2 rounded-md bg-amber-500 text-zinc-950 text-sm font-medium hover:bg-amber-400 transition-colors"
          >
            Get started
            <ArrowRight size={14} />
          </Link>
        </section>

        <section className="max-w-screen-md mx-auto px-6 pb-20">
          <h2 className="text-xs uppercase tracking-wider text-zinc-500 mb-4">Features</h2>
          <dl className="grid grid-cols-1 sm:grid-cols-2 gap-x-8 gap-y-5">
            {FEATURES.map(f => (
              <div key={f.label} className="border-l border-zinc-800 pl-4">
                <dt className="text-sm text-zinc-200 mb-0.5">{f.label}</dt>
                <dd className="text-sm text-zinc-500">{f.desc}</dd>
              </div>
            ))}
          </dl>
        </section>
      </main>

      <footer className="border-t border-zinc-800 py-4 text-center text-xs text-zinc-600">
        <span className="font-mono">DHBW Ravensburg · WebEng</span>
      </footer>
    </div>
  )
}
