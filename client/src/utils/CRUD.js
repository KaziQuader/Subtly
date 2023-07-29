const getServerUrl = () => {
    return "localhost:8080/api/v1/audio"
}

const post = async (url, body, pathVariable) => {
    if (!body) return null;
    else if (pathVariable) {
        url = url + "/" + pathVariable;
    }
    const response = await fetch(url, { method: "POST", body: JSON.stringify(body) });
    const responseBody = await response.json();
    const responseStatus = response.status;
    return { responseBody, responseStatus };
}

const put = async (url, body, pathVariable) => {
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

const get = async (url, body, pathVariable) => {
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