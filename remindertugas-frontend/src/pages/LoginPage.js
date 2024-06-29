import React, { useState } from 'react';
import apiRequest from '../utils/Api';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import { encryptSession } from '../utils/Session';
import { useNavigate } from 'react-router-dom';
import { BASE_URL } from '../utils/Constants';

const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();

        const loginData = {
            username,
            password,
        };

        const response = await apiRequest('POST', `${BASE_URL}/auth/login`, loginData);

        if (response.data.success) {
            toast.success(response.data.message);
            encryptSession('token', response.data.data.token);
            encryptSession('name', response.data.data.name);
            encryptSession('roleName', response.data.data.roleName);

            if (response.data.data.roleName === 'Admin') {
                navigate('/admin');
            } else {
                navigate('/user');
            }
        } else {
            toast.error(response ? response.data.message : 'An error occurred. Please try again.');
        }
    };

    return (
        <div className="flex items-center justify-center min-h-screen bg-telegram-primary">
            <ToastContainer />
            <div className="flex flex-col items-center">
                <div className="text-center mb-10">
                    <h1 className="text-4xl font-bold text-white">Selamat Datang di</h1>
                    <h2 className="text-3xl font-semibold text-white">Reminder Tugas</h2>
                    <h3 className="text-2xl font-medium text-white">Kelompok 2, Pemrograman 2</h3>
                </div>
                <form className="bg-white p-10 rounded-lg shadow-lg w-full max-w-md" onSubmit={handleSubmit}>
                    <h2 className="text-3xl font-bold mb-6 text-center">Login</h2>
                    <div className="mb-4">
                        <label className="block text-gray-700 text-lg mb-2">Username</label>
                        <input
                            type="text"
                            className="w-full px-4 py-2 border rounded-lg text-lg focus:outline-none focus:ring-2 focus:ring-telegram-secondary"
                            value={username}
                            onChange={(e) => setUsername(e.target.value)}
                            required
                        />
                    </div>
                    <div className="mb-6">
                        <label className="block text-gray-700 text-lg mb-2">Password</label>
                        <input
                            type="password"
                            className="w-full px-4 py-2 border rounded-lg text-lg focus:outline-none focus:ring-2 focus:ring-telegram-secondary"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                        />
                    </div>
                    <button
                        type="submit"
                        className="w-full bg-telegram-secondary text-white py-2 rounded-lg text-lg hover:bg-telegram-secondary-dark transition duration-200"
                    >
                        Login
                    </button>
                </form>
            </div>
        </div>
    );
};

export default LoginPage;
