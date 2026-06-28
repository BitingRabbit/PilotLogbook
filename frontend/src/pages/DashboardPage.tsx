import { useState, useEffect, useCallback, useRef } from 'react'
import { useNavigate } from 'react-router-dom'
import Navbar from '../components/Navbar'
import FlightMap, { MapLegend } from '../components/FlightMap'
import WeatherBlock from '../components/WeatherBlock'
import FlightFormModal from '../components/flight-form/FlightFormModal'
import RecentFlightsPanel from '../components/dashboard/RecentFlightsPanel'
import { PageShell } from '../components/ui/PageShell'
import { getDashboardFlights } from '../api/flights'
import type { FlightResponse } from '../types/flight'

export default function DashboardPage() {
  const navigate = useNavigate()
  const [flights, setFlights] = useState<FlightResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [showModal, setShowModal] = useState(false)
  const [filterDep, setFilterDep] = useState('')
  const [filterDest, setFilterDest] = useState('')
  const [filterMonth, setFilterMonth] = useState<number | undefined>()
  const debounceRef = useRef<ReturnType<typeof setTimeout> | null>(null)

  const fetchFlights = useCallback(async (dep: string, dest: string, month: number | undefined) => {
    setLoading(true)
    try {
      const data = await getDashboardFlights({
        dep: dep.trim().toUpperCase() || undefined,
        dest: dest.trim().toUpperCase() || undefined,
        month,
      })
      setFlights(data)
    } catch {
      setFlights([])
    } finally {
      setLoading(false)
    }
  }, [])

  useEffect(() => {
      // Debounce API calls while the user is adjusting filters
    if (debounceRef.current) clearTimeout(debounceRef.current)
    debounceRef.current = setTimeout(() => fetchFlights(filterDep, filterDest, filterMonth), 400)
    return () => { if (debounceRef.current) clearTimeout(debounceRef.current) }
  }, [filterDep, filterDest, filterMonth, fetchFlights])

  return (
    <PageShell>
      <Navbar onNewFlight={() => setShowModal(true)} />

      <main className="flex-1 max-w-screen-2xl w-full mx-auto px-4 py-6 space-y-6">
        <div className="grid grid-cols-1 lg:grid-cols-[1fr_360px] gap-6 min-h-[400px]">
          <div className="flex flex-col gap-2 min-h-0">
            {loading ? (
              <div className="flex-1 min-h-[400px] rounded-md border border-zinc-800 bg-zinc-900/40 animate-pulse" />
            ) : (
              <FlightMap flights={flights} height="flex-1 min-h-[400px]" />
            )}
            <MapLegend />
          </div>

          <div className="self-start w-full">
            <RecentFlightsPanel
              flights={flights}
              loading={loading}
              filterDep={filterDep}
              filterDest={filterDest}
              filterMonth={filterMonth}
              onDepChange={setFilterDep}
              onDestChange={setFilterDest}
              onMonthChange={setFilterMonth}
              onSelectFlight={f => navigate('/flights', { state: { flightId: f.id } })}
              onNewFlight={() => setShowModal(true)}
            />
          </div>
        </div>

        <WeatherBlock />
      </main>

      {showModal && (
        <FlightFormModal
          onClose={() => setShowModal(false)}
          onSuccess={() => { setShowModal(false); fetchFlights(filterDep, filterDest, filterMonth) }}
        />
      )}
    </PageShell>
  )
}
