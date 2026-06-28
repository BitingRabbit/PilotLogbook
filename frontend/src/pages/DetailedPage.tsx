import { useState, useEffect, useCallback } from 'react'
import { useLocation } from 'react-router-dom'
import Navbar from '../components/Navbar'
import FlightMap from '../components/FlightMap'
import FlightFormModal from '../components/flight-form/FlightFormModal'
import { PageShell } from '../components/ui/PageShell'
import { Alert } from '../components/ui/Alert'
import { extractErrorMessage } from '../utils/apiError'
import FlightSidebar from '../components/detailed/FlightSidebar'
import FlightHeader from '../components/detailed/FlightHeader'
import FlightDetails from '../components/detailed/FlightDetails'
import AircraftDetails from '../components/detailed/AircraftDetails'
import AirportsSection from '../components/detailed/AirportsSection'
import WeatherSection from '../components/detailed/WeatherSection'
import { getFlights, deleteFlight } from '../api/flights'
import type { FlightResponse } from '../types/flight'

export default function DetailedPage() {
  const location = useLocation()
  const preselectedFlightId: number | undefined = (location.state as { flightId?: number } | null)?.flightId

  const [flights, setFlights] = useState<FlightResponse[]>([])
  const [loading, setLoading] = useState(true)
  const [selected, setSelected] = useState<FlightResponse | null>(null)
  const [showModal, setShowModal] = useState(false)
  const [deleting, setDeleting] = useState(false)
  const [deleteError, setDeleteError] = useState<string | null>(null)
  const [editingFlight, setEditingFlight] = useState<FlightResponse | null>(null)
  const [sidebarOpen, setSidebarOpen] = useState(false)


  const fetchFlights = useCallback(async () => {
    setLoading(true)
    try {
      const data = await getFlights()
      setFlights(data)
      if (data.length > 0) {
        // keep current selection on refetch; only pick a default the first time
        setSelected(prev => {
          if (prev) return prev
          const target = preselectedFlightId ? data.find(f => f.id === preselectedFlightId) : undefined
          return target ?? data[0]
        })
      }
    } catch {
      setFlights([])
    } finally {
      setLoading(false)
    }
  }, [preselectedFlightId])

  // eslint-disable-next-line react-hooks/set-state-in-effect
  useEffect(() => { fetchFlights() }, [fetchFlights])

  const refreshSelected = useCallback(async () => {
    if (!selected) return
    try {
      const data = await getFlights()
      setFlights(data)
      const updated = data.find(f => f.id === selected.id)
      if (updated) setSelected(updated)
    } catch (err) {
      console.warn('refresh failed', err)
    }
  }, [selected])

  const handleDelete = useCallback(async () => {
    if (!selected) return
    if (!window.confirm(`Delete flight ${selected.originAirport.icao} → ${selected.destinationAirport.icao}? This cannot be undone.`)) return
    setDeleting(true)
    setDeleteError(null)
    try {
      await deleteFlight(selected.id)
      setFlights(prev => prev.filter(f => f.id !== selected.id))
      setSelected(null)
    } catch (e) {
      setDeleteError(extractErrorMessage(e, 'Failed to delete flight.'))
    } finally {
      setDeleting(false)
    }
  }, [selected])

  return (
    <PageShell>
      <Navbar onNewFlight={() => setShowModal(true)} />

      <div className="flex flex-1 min-h-0">
        <FlightSidebar flights={flights} loading={loading} selectedId={selected?.id}
                       onSelect={setSelected} open={sidebarOpen} onClose={() => setSidebarOpen(false)}
                       onOpen={() => setSidebarOpen(true)} />

        <main className="flex-1 overflow-y-auto">
          {!selected ? (
            <div className="h-full flex items-center justify-center text-zinc-600">
              <p className="text-sm">Select a flight to view details.</p>
            </div>
          ) : (
            <div className="max-w-4xl mx-auto p-6 space-y-6">
              <FlightHeader flight={selected} deleting={deleting} onDelete={handleDelete}
                            onEdit={() => setEditingFlight(selected)} />
              {deleteError && <Alert tone="error">{deleteError}</Alert>}
              <FlightMap flights={[selected]} height="h-[280px]" />
              <FlightDetails flight={selected} />
              <AircraftDetails flight={selected} />
              <AirportsSection flight={selected} />
              <WeatherSection flight={selected} onRefresh={refreshSelected} />
            </div>
          )}
        </main>
      </div>

      {showModal && (
        <FlightFormModal
          onClose={() => setShowModal(false)}
          onSuccess={() => { setShowModal(false); fetchFlights() }}
        />
      )}

      {editingFlight && (
        <FlightFormModal
          flight={editingFlight}
          onClose={() => setEditingFlight(null)}
          onSuccess={() => { setEditingFlight(null); refreshSelected() }}
        />
      )}
    </PageShell>
  )
}
