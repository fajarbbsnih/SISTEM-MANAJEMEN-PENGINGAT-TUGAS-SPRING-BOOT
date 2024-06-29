import React, { useEffect, useState, useCallback } from 'react';
import { Table, Button, Modal, Input, Select, Form } from 'antd';
import { useNavigate } from 'react-router-dom';
import { validateSession, decryptSession, clearSession } from '../utils/Session';
import { BASE_URL } from '../utils/Constants';
import apiRequest from '../utils/Api';
import { toast, ToastContainer } from 'react-toastify';
import { EyeOutlined, DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import 'react-toastify/dist/ReactToastify.css';
import moment from 'moment';
import { DatePicker, TimePicker } from 'antd';

const { Option } = Select;

const UserPage = () => {
    const [reminders, setReminders] = useState([]);
    const [contacts, setContacts] = useState([]);
    const [allContacts, setAllContacts] = useState([]);
    const [isModalVisible, setIsModalVisible] = useState(false);
    const [isDetailModalVisible, setIsDetailModalVisible] = useState(false);
    const [isAddModalVisible, setIsAddModalVisible] = useState(false);
    const [activeMenu, setActiveMenu] = useState('reminders');
    const [selectedReminder, setSelectedReminder] = useState(null);
    const [form] = Form.useForm();
    const [addForm] = Form.useForm();
    const navigate = useNavigate();
    const userName = decryptSession('name');
    const token = decryptSession('token');
    const [loading, setLoading] = useState(false);
    const [isDeleteModalVisible, setIsDeleteModalVisible] = useState(false);
    const [dataToDelete, setDataToDelete] = useState(null);

    const fetchReminders = useCallback(async () => {
        const response = await apiRequest('GET', `${BASE_URL}/reminders`, null, {
            Authorization: `Bearer ${token}`
        });

        if (response.status === 200) {
            if (response.data.success) {
                const remindersData = response.data.data.map((reminder, index) => ({
                    key: reminder.id,
                    no: index + 1,
                    subject: reminder.subject,
                    reminderDateTime: reminder.reminderDateTime,
                    status: reminder.status,
                    description: reminder.description,
                    statusDescription: reminder.statusDescription,
                }));
                setReminders(remindersData);
            } else {
                toast.error(`Failed to fetch reminders: ${response.data.message}`);
            }
        } else {
            alert('An error occurred. Please try again');
            navigate('/');
        }

    }, [token, navigate]);

    const fetchContacts = useCallback(async () => {
        const response = await apiRequest('GET', `${BASE_URL}/contacts`, null, {
            Authorization: `Bearer ${token}`
        });

        if (response.status === 200) {
            if (response.data.success) {
                const contactsData = response.data.data.map((contact, index) => ({
                    key: contact.id,
                    no: index + 1,
                    name: contact.name,
                    phoneNumber: contact.phoneNumber,
                }));
                setContacts(contactsData);
                setAllContacts(contactsData);
            } else {
                toast.error(`Failed to fetch contacts: ${response.data.message}`);
            }
        } else {
            alert('An error occurred. Please try again');
            navigate('/');
        }
    }, [token, navigate]);

    useEffect(() => {
        const fetchData = async () => {
            validateSession();
            await fetchReminders();
            await fetchContacts();
        };

        fetchData();
    }, [fetchReminders, fetchContacts, navigate, token]);

    const handleLogout = () => {
        alert("Logout berhasil");
        clearSession();
        navigate('/');
    };

    const handleChangePassword = async () => {
        const values = await form.validateFields();
        const response = await apiRequest('POST', `${BASE_URL}/auth/change-password`, values, {
            Authorization: `Bearer ${token}`
        });

        if (response.status === 200) {
            if (response.data.success) {
                setIsModalVisible(false);
                setLoading(true);
                toast.success(response.data.message);
                setTimeout(() => {
                    clearSession();
                    navigate('/');
                }, 2000);
            } else {
                toast.error(response.data.message);
            }
        } else {
            alert('An error occurred. Please try again');
            navigate('/');
        }
    };

    const handleMenuClick = (menu) => {
        setActiveMenu(menu);
    };

    const handleViewDetails = (record) => {
        setSelectedReminder(record);
        setIsDetailModalVisible(true);
    };

    const handleAdd = () => {
        addForm.resetFields();
        setIsAddModalVisible(true);
    };

    const handleAddOk = async () => {
        const values = await addForm.validateFields();
        let reminderDateTimeStr="";
        if(activeMenu === 'reminders' ){
         reminderDateTimeStr = `${values.reminderDateTimeStr.format('YYYY-MM-DD')}T${values.reminderTime.format('HH:mm')}:00`;
        }
        const bodyReq = activeMenu === 'reminders' ? {
            ...values,
            reminderDateTimeStr,
        } : { ...values, };
        const endpoint = activeMenu === 'reminders' ? 'reminders' : 'contacts';
        console.log(bodyReq);
        const response = await apiRequest('POST', `${BASE_URL}/${endpoint}`, bodyReq, {
            Authorization: `Bearer ${token}`
        });

        if (response.status === 200) {
            if (response.data.success) {
                setLoading(true);
                setIsAddModalVisible(false);
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

    const handleDeleteData = async () => {
        const endpoint = activeMenu === 'reminders' ? 'reminders' : 'contacts';
        const response = await apiRequest('DELETE', `${BASE_URL}/${endpoint}/${dataToDelete.key}`, null, {
            Authorization: `Bearer ${token}`
        });

        if (response.status === 200) {
            if (response.data.success) {
                setLoading(true);
                setIsDeleteModalVisible(false);
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

    const handleCancelDelete = () => {
        setIsDeleteModalVisible(false);
    };

    const showDeleteModal = (data) => {
        setDataToDelete(data);
        setIsDeleteModalVisible(true);
    };

    const columns = activeMenu === 'reminders' ? [
        {
            title: 'No',
            dataIndex: 'no',
            key: 'no',
        },
        {
            title: 'Subjek',
            dataIndex: 'subject',
            key: 'subject',
        },
        {
            title: 'Tanggal Pengingat',
            dataIndex: 'reminderDateTime',
            key: 'reminderDateTime',
            render: (text) => moment(text).format('YYYY-MM-DD HH:mm'),
        },
        {
            title: 'Status',
            dataIndex: 'status',
            key: 'status',
        },
        {
            title: 'Aksi',
            key: 'actions',
            render: (text, record) => (
                <>
                    <Button className="bg-telegram-primary text-white mx-2" type="link" icon={<EyeOutlined />} onClick={() => handleViewDetails(record)}>Detail</Button>
                    <Button className="bg-red-400 text-white mx-2" type="link" icon={<DeleteOutlined />} onClick={() => showDeleteModal(record)}>Delete</Button>
                </>
            ),
        },
    ] : [
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
            title: 'Aksi',
            key: 'actions',
            render: (text, record) => (
                <>
                    <Button className="bg-red-400 text-white" type="link" icon={<DeleteOutlined />} onClick={() => showDeleteModal(record)}>Delete</Button>
                </>
            ),
        },
    ];

    const handleSearchChange = (e) => {
        const value = e.target.value.toLowerCase();
        const filteredContacts = allContacts.filter(contact =>
            contact.name.toLowerCase().includes(value) ||
            contact.phoneNumber.toLowerCase().includes(value)
        );
        setContacts(filteredContacts);
    };

    const dataSource = activeMenu === 'reminders' ? reminders : contacts;

    return (
        <div className="flex h-screen">
            <ToastContainer />
            {loading && (
                <div className="my--overlay">
                </div>
            )}
            <aside className="w-64 bg-telegram-primary text-white py-4 flex flex-col justify-between h-full">
                <div>
                    <div className="mb-32 ml-4 text-xl">
                        <h1>HAI, {userName.toUpperCase()}</h1>
                    </div>
                    <div className="mb-4">
                        <Button type="link" className={`p-6 my-2 w-full text-white ${activeMenu === 'reminders' ? 'font-bold bg-telegram-secondary' : 'bg-telegram-secondary-dark'}`} onClick={() => handleMenuClick('reminders')}>Reminder</Button>
                        <Button type="link" className={`p-6 my-2 w-full text-white ${activeMenu === 'contacts' ? 'font-bold bg-telegram-secondary' : 'bg-telegram-secondary-dark'}`} onClick={() => handleMenuClick('contacts')}>Kontak</Button>
                    </div>
                </div>
                <div className="mt-auto py-2">
                    <Button type="link" className="my-1 w-full text-white bg-telegram-secondary-dark" onClick={() => setIsModalVisible(true)}>Change Password</Button>
                    <Button type="link" className="my-1 w-full text-white bg-red-400" onClick={handleLogout}>Logout</Button>
                </div>
            </aside>
            <main className="flex-1 p-4 overflow-auto">
                <header className="flex justify-between items-center mb-4">
                    <h1 className="text-2xl font-bold">{activeMenu.charAt(0).toUpperCase() + activeMenu.slice(1)}</h1>
                    <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>Tambah {activeMenu.charAt(0).toUpperCase() + activeMenu.slice(1)}</Button>
                </header>

                {activeMenu === 'reminders' ? null : <Input
                    placeholder="Cari..."
                    style={{ marginBottom: 20, width: 300 }}
                    onChange={handleSearchChange}
                />}
                <Table columns={columns} dataSource={dataSource} pagination={{ pageSize: 10 }} />
            </main>

            <Modal title="Change Password" open={isModalVisible} onOk={handleChangePassword} onCancel={() => setIsModalVisible(false)}>
                <Form form={form} layout="vertical">
                    <Form.Item name="oldPassword" label="Kata Sandi Lama" rules={[{ required: true, message: 'Silakan masukkan kata sandi lama!' }]}>
                        <Input.Password placeholder="Masukkan kata sandi lama" style={{ height: '40px' }} />
                    </Form.Item>
                    <Form.Item name="newPassword" label="Kata Sandi Baru" rules={[{ required: true, message: 'Silakan masukkan kata sandi baru!' }]}>
                        <Input.Password placeholder="Masukkan kata sandi baru" style={{ height: '40px' }} />
                    </Form.Item>
                </Form>
            </Modal>

            <Modal
                title="Detail Pengingat"
                open={isDetailModalVisible}
                onCancel={() => setIsDetailModalVisible(false)}
                footer={null}
            >
                {selectedReminder && (
                    <div>
                        <p><strong>Deskripsi:</strong> {selectedReminder.description}</p>
                        <p><strong>Status Deskripsi:</strong> {selectedReminder.statusDescription || 'N/A'}</p>
                    </div>
                )}
            </Modal>

            <Modal
                title={`Tambah ${activeMenu === 'reminders' ? 'Pengingat' : 'Kontak'}`}
                open={isAddModalVisible}
                onOk={handleAddOk}
                onCancel={() => setIsAddModalVisible(false)}
            >
                <Form form={addForm} layout="vertical">
                    {activeMenu === 'reminders' ? (
                        <>
                            <Form.Item name="subject" label="Subjek" rules={[{ required: true, message: 'Silakan masukkan subjek!' }]}>
                                <Input placeholder="Masukkan subjek" style={{ height: '40px' }} />
                            </Form.Item>
                            <Form.Item name="description" label="Deskripsi" rules={[{ required: true, message: 'Silakan masukkan deskripsi!' }]}>
                                <Input placeholder="Masukkan deskripsi" style={{ height: '40px' }} />
                            </Form.Item>
                            <Form.Item name="reminderDateTimeStr" label="Tanggal Pengingat (Tahun-Bulan-Tanggal)" rules={[{ required: true, message: 'Silakan masukkan tanggal pengingat!' }]}>
                                <DatePicker placeholder="Pilih tanggal" style={{ width: '100%', height: '40px' }} />
                            </Form.Item>
                            <Form.Item name="reminderTime" label="Waktu Pengingat (Jam:Menit)" rules={[{ required: true, message: 'Silakan masukkan waktu pengingat!' }]}>
                                <TimePicker placeholder="Pilih waktu" format="HH:mm"style={{ width: '100%', height: '40px' }} />
                            </Form.Item>
                            <Form.Item name="contactIds" label="Kontak" rules={[{ required: true, message: 'Silakan pilih kontak!' }]}>
                                <Select mode="multiple" placeholder="Pilih kontak" style={{ height: '40px' }}>
                                    {allContacts.map(contact => (
                                        <Option key={contact.key} value={contact.key}>{contact.name}</Option>
                                    ))}
                                </Select>
                            </Form.Item>
                        </>
                    ) : (
                        <>
                            <Form.Item name="name" label="Nama" rules={[{ required: true, message: 'Silakan masukkan nama!' }]}>
                                <Input placeholder="Masukkan nama" style={{ height: '40px' }} />
                            </Form.Item>
                            <Form.Item name="phoneNumber" label="Nomor Telepon" rules={[{ required: true, pattern: /^[0-9\b]+$/, message: 'Nomor telepon tidak valid!' }]}>
                                <Input placeholder="Masukkan nomor telepon" style={{ height: '40px' }} />
                            </Form.Item>
                        </>
                    )}
                </Form>
            </Modal>

            <Modal title="Konfirmasi Hapus" open={isDeleteModalVisible} onOk={handleDeleteData} onCancel={handleCancelDelete}>
                <p>Apakah anda yakin menghapus data {activeMenu === 'reminders' ? dataToDelete?.subject : dataToDelete?.name} ini?</p>
            </Modal>
        </div>
    );
};

export default UserPage;