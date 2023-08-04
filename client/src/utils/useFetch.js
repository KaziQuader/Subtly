import { useState, useEffect } from 'react'

const useFetch = (url) => {
    const [data, setData] = useState(null);
    const [isPending, setIsPending] = useState(true)
    const [error, setError] = useState(null)
    useEffect(() => {
        const abortController = new AbortController()
        console.log("here")

        fetch(url, { signal: abortController.signal })
            .then((result) => {

                if (!result.ok) {
                    throw Error('Could not fetch the data for that resource')
                }
                return result.json()
            })
            .then(
                data => {

                    setData(data)
                    setIsPending(false)
                    setError(null)
                }
            ).catch((err) => {
                if (err.name === 'AbortError') {
                    console.log('fetch aborted')
                } else {
                    setError(err.message)
                    setIsPending(false)
                }
            });


        return () => abortController.abort()
    }, [url])
    return { data, isPending, error }
}
export default useFetch