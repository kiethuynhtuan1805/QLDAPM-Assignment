import './ManageOrder.scss'
import axios from 'axios'
import { useEffect, useState } from 'react'
import { Table, Tag, Button, Popconfirm } from 'antd'
import { Icon } from '@iconify/react'
import { useNavigate } from 'react-router-dom'

const ManageOrder = () => {
  const navigate = useNavigate()
  const [data, setData] = useState([])
  const [isLoading, setLoading] = useState(true)

  // Hàm lấy thông tin đơn hàng với xác thực
  const fetchOrders = async () => {
    try {
      // Lấy token từ localStorage
      const accessToken = localStorage.getItem('accessToken')
      if (!accessToken) {
        throw new Error('Access token không tồn tại. Vui lòng đăng nhập.')
      }

      // Gửi request đến API
      const response = await axios.get('https://htk.sflavor-demo-app-backend.io.vn/api/v1/orders', {
        headers: {
          Authorization: `Bearer ${accessToken}`, // Loại bỏ dấu " nếu có
        },
      })

      // Cập nhật dữ liệu
      console.log('Dữ liệu đơn hàng nhận được:', response.data.data)
      setData(response.data.data)
      setLoading(false)
    } catch (error) {
      console.error(
        'Lỗi khi lấy dữ liệu đơn hàng:',
        error.response ? error.response.data : error.message
      )
      setLoading(false)
    }
  }

  // Gọi hàm lấy dữ liệu khi component được mount
  useEffect(() => {
    fetchOrders()
  }, [])

  const columns = [
    {
      title: 'Mã đơn hàng',
      dataIndex: 'orderId',
      key: 'orderId',
    },
    {
      title: 'Mã người dùng',
      dataIndex: 'userId',
      key: 'userId',
    },
    {
      title: 'Mã đặt bàn',
      dataIndex: 'reservationId',
      key: 'reservationId',
    },
    {
      title: 'Ngày đặt',
      dataIndex: 'orderDate',
      key: 'orderDate',
      render: (text) => new Date(text).toLocaleString(), // Hiển thị ngày giờ theo định dạng địa phương
    },
    {
      title: 'Tổng giá',
      dataIndex: 'totalPrice',
      key: 'totalPrice',
      render: (text) => text.toLocaleString(), // Hiển thị tổng giá dưới dạng số
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      render: (status) =>
        status === 'active' ? (
          <Tag color="#87d068">Đang hoạt động</Tag>
        ) : (
          <Tag color="#f50">Không hoạt động</Tag>
        ),
    },
    {
      title: 'Action',
      key: 'operation',
      render: (_, record) => (
        <div>
          <Button onClick={() => navigate(`edit/${record.orderId}`)}>
            <Icon icon="typcn:edit" width={28} height={28} />
          </Button>
          <Popconfirm title="Sure to delete?" onConfirm={() => handleDelete(record)}>
            <Button>
              <Icon icon="material-symbols:delete" width={28} height={28} />
            </Button>
          </Popconfirm>
        </div>
      ),
    },
  ]

  const handleDelete = async (order) => {
    try {
      await axios.delete(`https://htk.sflavor-demo-app-backend.io.vn/api/v1/order/${order.orderId}`)
      setData(data.filter((item) => item.orderId !== order.orderId))
    } catch (error) {
      console.error('Error deleting order', error)
    }
  }

  return (
    <div className="ManageOrder">
      {/* <Button
        style={{
          backgroundColor: 'green',
          borderColor: 'green',
          color: 'white',
          float: 'right',
          marginBottom: '20px',
        }}
        onClick={() => navigate('add')}
      >
        THÊM ĐƠN HÀNG
      </Button> */}
      <Table columns={columns} dataSource={data} loading={isLoading} rowKey="orderId" />
    </div>
  )
}

export default ManageOrder
