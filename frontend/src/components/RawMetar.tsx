interface RawMetarProps {
  raw: string
}

export default function RawMetar({ raw }: RawMetarProps) {
  return (
    <pre className="text-xs text-amber-300/90 bg-zinc-950 border border-zinc-800 rounded p-3 font-mono whitespace-pre-wrap break-all">
      {raw}
    </pre>
  )
}
