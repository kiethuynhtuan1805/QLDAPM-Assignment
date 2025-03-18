import './ManageInvoice.scss'
import axios from 'axios'
import { useEffect, useState } from 'react'
import { Table, Button, Modal, Space, message } from 'antd'
import { EyeOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'

const ManageInvoice = () => {
  const [invoices, setInvoices] = useState([])
  const [isLoading, setLoading] = useState(true)
  const [isModalVisible, setModalVisible] = useState(false)
  const [selectedInvoice, setSelectedInvoice] = useState(null)

  // Hàm lấy hóa đơn với xác thực
  const fetchInvoices = async () => {
    try {
      // Lấy token từ localStorage
      const accessToken = localStorage.getItem('accessToken')
      if (!accessToken) {
        throw new Error('Access token không tồn tại. Vui lòng đăng nhập.')
      }

      // Gửi request đến API
      const response = await axios.get(
        'https://htk.sflavor-demo-app-backend.io.vn/api/v1/invoices',
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      )

      // Cập nhật dữ liệu hóa đơn
      console.log('Dữ liệu hóa đơn nhận được:', response.data.data)
      setInvoices(response.data.data)
      setLoading(false)
    } catch (error) {
      console.error(
        'Lỗi khi lấy dữ liệu hóa đơn:',
        error.response ? error.response.data : error.message
      )
      message.error('Lỗi khi lấy dữ liệu hóa đơn')
      setLoading(false)
    }
  }

  // Gọi hàm fetchInvoices khi component mount
  useEffect(() => {
    fetchInvoices()
  }, [])

  // Hàm xử lý xem chi tiết hóa đơn
  const showInvoiceDetails = (invoice) => {
    setSelectedInvoice(invoice)
    setModalVisible(true)
  }

  // Đóng modal
  const handleModalClose = () => {
    setModalVisible(false)
  }

  // Cột bảng hóa đơn
  const columns = [
    {
      title: 'Invoice ID',
      dataIndex: 'invoiceId',
      key: 'invoice_ID',
    },
    {
      title: 'Order ID',
      dataIndex: 'orderId',
      key: 'order_ID',
    },
    {
      title: 'Total Amount',
      dataIndex: 'total_amount',
      key: 'total_amount',
      render: (text) => `$${parseFloat(text).toLocaleString()}`,
    },
    {
      title: 'Invoice Date',
      dataIndex: 'invoice_date',
      key: 'invoice_date',
      render: (text) => new Date(text).toLocaleString(),
    },
    {
      title: 'Action',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Button
            type="primary"
            icon={<EyeOutlined />} // Biểu tượng xem
            onClick={() => showInvoiceDetails(record)}
          ></Button>
          <Button
            type="primary"
            icon={<EditOutlined />} // Biểu tượng chỉnh sửa
          ></Button>
          <Button
            type="primary"
            icon={<DeleteOutlined />} // Biểu tượng xóa
            onClick={() => handleDelete(record)}
          ></Button>
        </Space>
      ),
    },
  ]

  // Hàm xóa hóa đơn
  const handleDelete = async (invoice) => {
    try {
      const accessToken = localStorage.getItem('accessToken')
      if (!accessToken) {
        throw new Error('Access token không tồn tại. Vui lòng đăng nhập.')
      }

      await axios.delete(
        `https://htk.sflavor-demo-app-backend.io.vn/api/v1/invoice/${invoice.invoice_ID}`,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      )

      // Cập nhật danh sách sau khi xóa
      setInvoices(invoices.filter((item) => item.invoice_ID !== invoice.invoice_ID))
      message.success('Hóa đơn đã được xóa thành công!')
    } catch (error) {
      console.error('Lỗi khi xóa hóa đơn:', error.response ? error.response.data : error.message)
      message.error('Lỗi khi xóa hóa đơn')
    }
  }

  return (
    <div className="ManageInvoice">
      <Table columns={columns} dataSource={invoices} loading={isLoading} rowKey="invoice_ID" />

      {/* Modal xem chi tiết hóa đơn */}
      <Modal
        title="Invoice Details"
        open={isModalVisible}
        onCancel={handleModalClose}
        footer={[
          <Button key="close" onClick={handleModalClose}>
            Close
          </Button>,
        ]}
      >
        {selectedInvoice && (
          <ul>
            {selectedInvoice.orderItems.map((item, index) => (
              <li key={item.orderItemId || index}>
                <p>
                  <strong>Dish ID:</strong> {item.dishId}
                </p>
                <p>
                  <strong>Quantity:</strong> {item.quantity}
                </p>
                <p>
                  <strong>Special Requests:</strong> {item.specialRequests}
                </p>
              </li>
            ))}
          </ul>
        )}
      </Modal>
    </div>
  )
}

export default ManageInvoice
