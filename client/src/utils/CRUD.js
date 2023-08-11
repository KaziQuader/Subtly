export const getServerUrl = () => {
    return "http://localhost:8080/api/v1/audio"
}

export const post = async (url, pathVariable, body) => {
    if (!body) return null;
    else if (pathVariable)
        url = url + "/" + pathVariable;
    body = body instanceof FormData ? body : JSON.stringify(body)

    let response = await fetch(url, { method: "POST", body: body });
    const responseBody = await response.json();
    const responseStatus = response.status;
    return { responseBody, responseStatus };
}

export const put = async (url, pathVariable, body) => {
    if (!body && !pathVariable) return null;

    if (pathVariable)
        url = url + "/" + pathVariable
    if (body)
        body = JSON.stringify(body);
    else body = null;

    const response = await fetch(url, {
        method: "PUT", body: body, headers: {
            'Content-Type': 'application/json'
        }
    });
    const responseBody = await response.json();
    const responseStatus = response.status;
    return { responseBody, responseStatus };
}

export const get = async (url, pathVariable, body) => {
    if (pathVariable)
        url = url + "/" + pathVariable
    if (body)
        body = JSON.stringify(body);
    else body = null;

    const response = await fetch(url, { method: "GET", body: body });
    const responseBody = await response.json();
    const responseStatus = response.status;
    return { responseBody, responseStatus };
}