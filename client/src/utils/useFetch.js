import { useState, useEffect } from 'react'

const useFetch = (url) => {
    const [data, setData] = useState(null);
    const [isPending, setIsPending] = useState(true)
    const [error, setError] = useState(null)
    useEffect(() => {
        const abortController = new AbortController()
        fetch(url, { signal: abortController.signal })
            .then((result) => {

                if (!result.ok) {
                    throw Error('Could not fetch the data for that resource')
                }
                return result.json()
            })
            .then(
                json => {
                    setData(json)
                    setIsPending(false)
                    setError(null)
                }
            ).catch((err) => {
                if (err.name !== 'AbortError') {
                    setError(err.message)
                    setIsPending(false)
                }
            });


        return () => abortController.abort()
    }, [url])
    return { data, isPending, error }
}
export default useFetch