import CryptoJS from 'crypto-js';

const secretKey = '1234567890123456';

export const encryptSession = (key, value) => {
    const encryptedValue = CryptoJS.AES.encrypt(value, secretKey).toString();
    sessionStorage.setItem(key, encryptedValue);
};

export const decryptSession = (key) => {
    const encryptedValue = sessionStorage.getItem(key);
    if (!encryptedValue) return null;

    const bytes = CryptoJS.AES.decrypt(encryptedValue, secretKey);
    return bytes.toString(CryptoJS.enc.Utf8);
};

export const validateSession = () => {
    try {
        const token = decryptSession('token');
        const name = decryptSession('name');
        const roleName = decryptSession('roleName');
        
        if (!token || !name || !roleName) {
            throw new Error('Invalid session');
        }
    } catch (error) {
        console.error('Session validation error:', error);
        sessionStorage.clear();
        throw new Error('Session has been tampered with or is invalid.');
    }
};

export const isAdmin = () => {
    const roleName = decryptSession('roleName');
    return roleName === 'Admin';
};

export const clearSession = () => {
    sessionStorage.clear();
};