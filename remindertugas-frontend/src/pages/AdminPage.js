import React, { useEffect, useState } from 'react';
import { Table, Button, Modal, Input, Select, Form } from 'antd';
import { EditOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { validateSession, isAdmin, decryptSession, clearSession } from '../utils/Session';
import { BASE_URL } from '../utils/Constants';
import apiRequest from '../utils/Api';
import { toast, ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';

const { Option } = Select;

const AdminPage = () => {
    const [users, setUsers] = useState([]);
    const [roles, setRoles] = useState([]);
    const [allUsers, setAllUsers] = useState([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [isEdit, setIsEdit] = useState(false);
    const [selectedUser, setSelectedUser] = useState(null);
    const [loading, setLoading] = useState(false);
    const [form] = Form.useForm();
    const navigate = useNavigate();
    const userName = decryptSession('name');
    const token = decryptSession('token');
    const [isDeleteModalVisible, setIsDeleteModalVisible] = useState(false);
    const [userToDelete, setUserToDelete] = useState(null);

    useEffect(() => {
        const fetchUsers = async () => {
            validateSession();
            if (!isAdmin()) {
                navigate('/user');
            }

            const response = await apiRequest('GET', `${BASE_URL}/users`, null, {
                Authorization: `Bearer ${token}`
            });

            if (response.status === 200) {
                if (response.data.success) {
                    const userData = response.data.data.map((user, index) => ({
                        key: user.id,
                        no: index + 1,
                        name: user.name,
                        username: user.username,
                        phoneNumber: user.phoneNumber,
                        role: user.roles.roleName,
                        roleId: user.roles.id,
                    }));
                    setUsers(userData);
                    setAllUsers(userData);
                } else {
                    toast.error('Failed to fetch users:', response.data.message);
                }
            } else {
                alert('An error occurred. Please try again');
                navigate('/');
            }
        };

        const fetchRoles = async () => {
            const response = await apiRequest('GET', `${BASE_URL}/roles`, null, {
                Authorization: `Bearer ${token}`
            });

            if (response.status === 200) {
                if (response.data.success) {
                    setRoles(response.data.data);
                } else {
                    toast.error('Failed to fetch roles:', response.data.message);
                }
            } else {
                alert('An error occurred. Please try again');
                navigate('/');
            }
        };

        fetchUsers();
        fetchRoles();
    }, [navigate, token]);

    const showModal = () => {
        setIsModalVisible(true);
    };

    const showDeleteModal = (user) => {
        setUserToDelete(user);
        setIsDeleteModalVisible(true);
    };

    const handleAddUser = () => {
        form.resetFields();
        setIsEdit(false);
        showModal();
    };

    const handleEditUser = (user) => {
        form.setFieldsValue(user);
        setSelectedUser(user);
        setIsEdit(true);
        showModal();
    };

    const handleCancelDelete = () => {
        setIsDeleteModalVisible(false);
    };

    const handleDeleteUser = async () => {
        const response = await apiRequest('DELETE', `${BASE_URL}/users/${userToDelete.key}`, null, {
            Authorization: `Bearer ${token}`
        });

        if (response.status === 200) {
            if (response.data.success) {
                setIsDeleteModalVisible(false);
                setLoading(true);
                toast.success(response.data.message);
                setTimeout(() => {
                    window.location.reload();
                }, 2000);
            } else {
                toast.error(response.data.message);
            }
        } else {
            alert('An error occurred. Please try again');
            navigate('/');
        }
    };

    const handleCancel = () => {
        setIsModalVisible(false);
    };

    const handleOk = async () => {
        const values = await form.validateFields();

        if (isEdit) {
            const response = await apiRequest('PUT', `${BASE_URL}/users/${selectedUser.key}`, {
                ...values,
                roles: { id: values.roleId },
            }, {
                Authorization: `Bearer ${token}`
            });

            if (response.status === 200) {
                if (response.data.success) {
                    setIsModalVisible(false);
                    setLoading(true);
                    toast.success(response.data.message);
                    setTimeout(() => {
                        window.location.reload();
                    }, 2000);
                } else {
                    toast.error(response.data.message);
                }
            } else {
                alert('An error occurred. Please try again');
                navigate('/');
            }
        } else {
            const response = await apiRequest('POST', `${BASE_URL}/users`, {
                ...values,
                roles: { id: values.roleId },
            }, {
                Authorization: `Bearer ${token}`
            });

            if (response.status === 200) {
                if (response.data.success) {
                    setIsModalVisible(false);
                    setLoading(true);
                    toast.success(response.data.message);
                    setTimeout(() => {
                        window.location.reload();
                    }, 2000);
                } else {
                    toast.error(response.data.message);
                }
            } else {
                alert('An error occurred. Please try again');
                navigate('/');
            }
        }
    };

    const columns = [
        {
            title: 'No',
            dataIndex: 'no',
            key: 'no',
        },
        {
            title: 'Nama',
            dataIndex: 'name',
            key: 'name',
        },
        {
            title: 'Nomor Telepon',
            dataIndex: 'phoneNumber',
            key: 'phoneNumber',
        },
        {
            title: 'Peran',
            dataIndex: 'role',
            key: 'role',
        },
        {
            title: 'Aksi',
            key: 'actions',
            render: (text, record) => (
                <div>
                    <Button icon={<EditOutlined />} onClick={() => handleEditUser(record)} />
                    <Button icon={<DeleteOutlined />} onClick={() => showDeleteModal (record)} />
                </div>
            ),
        },
    ];

    const handleSearchChange = (e) => {
        const value = e.target.value.toLowerCase();
        const filteredUsers = allUsers.filter(user =>
            user.name.toLowerCase().includes(value) ||
            user.phoneNumber.toLowerCase().includes(value) ||
            user.role.toLowerCase().includes(value)
        );
        setUsers(filteredUsers);
    };

    const handleLogout = () => {
        alert("Logout berhasil");
        clearSession();
        navigate('/');
    };

    return (
        <div className="flex h-screen">
            <ToastContainer />
            {loading && (
                <div className="my--overlay">
                </div>
            )}
            <aside className="w-64 bg-telegram-primary text-white flex flex-col justify-between h-full">
                <div className="ml-4 text-xl">
                    <h1>HAI, {userName.toUpperCase()}</h1>
                </div>

                <div className="mt-auto">
                    <Button type="link" className="p-6 w-full text-white bg-red-400" onClick={handleLogout}>Logout</Button>
                </div>
            </aside>
            <main className="flex-1 p-4">
                <header className="flex justify-between items-center mb-4">
                    <h1 className="text-2xl font-bold">User</h1>
                    <Button type="primary" icon={<PlusOutlined />} onClick={handleAddUser}>Tambah User</Button>
                </header>
                <Input
                    placeholder="Cari..."
                    style={{ marginBottom: 20, width: 300 }}
                    onChange={handleSearchChange}
                />
                <Table columns={columns} dataSource={users} pagination={{ pageSize: 10 }} />
            </main>

            <Modal title={isEdit ? "Edit Pengguna" : "Tambah Pengguna"} open={isModalVisible} onOk={handleOk} onCancel={handleCancel}>
                <Form form={form} layout="vertical">
                    <Form.Item name="name" label="Nama" rules={[{ required: true, message: 'Silakan masukkan nama!' }]}>
                        <Input placeholder="Masukkan nama" style={{ height: '40px' }} />
                    </Form.Item>
                    <Form.Item name="username" label="Username" rules={[{ required: true, message: 'Silakan masukkan username!' }]}>
                        <Input placeholder="Masukkan username" style={{ height: '40px' }} />
                    </Form.Item>
                    <Form.Item name="phoneNumber" label="Nomor Telepon" rules={[{ required: true, message: 'Silakan masukkan nomor telepon!' }]}>
                        <Input placeholder="Masukkan nomor telepon" style={{ height: '40px' }} />
                    </Form.Item>
                    <Form.Item name="roleId" label="Peran" rules={[{ required: true, message: 'Silakan pilih peran!' }]}>
                        <Select placeholder="Pilih Peran" style={{ height: '40px' }}>
                            {roles.map(role => (
                                <Option key={role.id} value={role.id}>{role.roleName}</Option>
                            ))}
                        </Select>
                    </Form.Item>
                </Form>
            </Modal>

            <Modal title="Konfirmasi Hapus" open={isDeleteModalVisible} onOk={handleDeleteUser} onCancel={handleCancelDelete}>
                <p>Apakah anda yakin menghapus data {userToDelete?.name} ini?</p>
            </Modal>
        </div>
    );
};

export default AdminPage;
