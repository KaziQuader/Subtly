export const getServerUrl = () => {
    return "localhost:8080/api/v1/audio"
}

export const post = async (url, body, pathVariable, form) => {
    let response;
    if (!form) {
        if (!body) return null;
        else if (pathVariable) {
            url = url + "/" + pathVariable;
        }
        response = await fetch(url, { method: "POST", body: JSON.stringify(body) });
    }
    else
        response = await fetch(url, { method: "POST", body: form });

    const responseBody = await response.json();
    const responseStatus = response.status;
    return { responseBody, responseStatus };

}

export const put = async (url, body, pathVariable) => {
    if (!body && !pathVariable) return null;

    if (pathVariable)
        url = url + "/" + pathVariable
    if (body)
        body = JSON.stringify(body);
    else body = null;

    const response = await fetch(url, { method: "PUT", body: body });
    const responseBody = await response.json();
    const responseStatus = response.status;
    return { responseBody, responseStatus };
}

export const get = async (url, body, pathVariable) => {
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