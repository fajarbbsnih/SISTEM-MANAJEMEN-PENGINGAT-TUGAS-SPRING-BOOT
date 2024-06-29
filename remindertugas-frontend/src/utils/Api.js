import axios from 'axios';

const apiRequest = async (method, url, body = null, customHeaders = {}) => {
    const headers = {
        'Content-Type': 'application/json',
        ...customHeaders,
    };

    console.log("===============");
    console.log(method);
    console.log(url);
    console.log(body);
    console.log(headers);
    console.log("===============");

    try {
        const response = await axios({
            method,
            url,
            data: body,
            headers,
        });
        console.log(response.data);
        return response;
    } catch (error) {
        return error.response ? error.response.data : { success: false, message: 'An error occurred' };
    }
};

export default apiRequest;