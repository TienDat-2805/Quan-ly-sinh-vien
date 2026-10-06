import { useCallback, useEffect, useState } from 'react';
export function useResource<T>(loader: () => Promise<T>, key = '') {
  const [data, setData] = useState<T | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [version, setVersion] = useState(0);
  const reload = useCallback(() => setVersion(v => v + 1), []);
  useEffect(() => {
    let alive = true;
    setLoading(true); setError('');
    loader().then(result => { if (alive) setData(result); }).catch((reason: unknown) => { if (alive) setError(reason instanceof Error ? reason.message : 'Unable to load records.'); }).finally(() => { if (alive) setLoading(false); });
    return () => { alive = false; };
    // The resource key intentionally controls when the request is repeated.
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [key, version]);
  return { data, loading, error, reload };
}
export function useDebounced(value: string, delay = 250) {
  const [debounced, setDebounced] = useState(value);
  useEffect(() => { const timer = setTimeout(() => setDebounced(value), delay); return () => clearTimeout(timer); }, [value, delay]);
  return debounced;
}
