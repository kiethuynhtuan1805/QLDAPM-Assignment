import './AddOrder.scss'
import { useState } from 'react'
import { Button, Input, Form, message, Select } from 'antd'
import axios from 'axios'
import { useNavigate } from 'react-router-dom'

const { Option } = Select

const AddOrder = () => {
  const navigate = useNavigate()

  // State để lưu trữ thông tin form
  const [orderId, setOrderID] = useState('')
  const [userId, setUserID] = useState('')
  const [reservationId, setReservationID] = useState('')
  const [orderDate, setOrderDate] = useState('')
  const [totalPrice, setTotalPrice] = useState('')
  const [status, setStatus] = useState('pending')
  const [orderItems] = useState([])
  const [isSubmitting, setIsSubmitting] = useState(false)

  const handleSubmit = async () => {
    setIsSubmitting(true)

    const orderData = {
      orderId,
      userId,
      reservationId,
      orderDate,
      totalPrice,
      status,
      orderItems,
    }

    const accessToken = localStorage.getItem('accessToken')
    if (!accessToken) {
      throw new Error('Access token không tồn tại. Vui lòng đăng nhập.')
    }

    try {
      const response = await axios.post(
        'https://htk.sflavor-demo-app-backend.io.vn/api/v1/order-item',
        orderData,
        {
          headers: {
            Authorization: `Bearer ${accessToken}`,
          },
        }
      )
      if (response.status === 201) {
        message.success('Đơn hàng đã được thêm thành công!')
        navigate('/management/order')
      }
    } catch (error) {
      if (error.response && error.response.status === 401) {
        message.error('Phiên đăng nhập đã hết hạn. Vui lòng đăng nhập lại.')
        // Redirect hoặc clear token nếu cần
        localStorage.removeItem('accessToken')
        navigate('/login')
      } else {
        message.error('Đã xảy ra lỗi khi thêm đơn hàng.')
      }
      console.error(error)
    } finally {
      setIsSubmitting(false)
    }
  }

  return (
    <div className="add-order">
      <h2>Thêm Đơn Hàng Mới</h2>
      <Form onFinish={handleSubmit} layout="vertical">
        <Form.Item label="Mã Đơn Hàng" required>
          <Input
            value={orderId}
            onChange={(e) => setOrderID(e.target.value)}
            placeholder="Nhập mã đơn hàng"
          />
        </Form.Item>

        <Form.Item label="Mã Người Dùng" required>
          <Input
            value={userId}
            onChange={(e) => setUserID(e.target.value)}
            placeholder="Nhập mã người dùng"
          />
        </Form.Item>

        <Form.Item label="Mã Đặt Bàn">
          <Input
            value={reservationId}
            onChange={(e) => setReservationID(e.target.value)}
            placeholder="Nhập mã đặt bàn (nếu có)"
          />
        </Form.Item>

        <Form.Item label="Ngày Đặt Hàng" required>
          <Input
            type="date"
            value={orderDate}
            onChange={(e) => setOrderDate(e.target.value)}
            placeholder="Chọn ngày đặt hàng"
          />
        </Form.Item>

        <Form.Item label="Tổng Giá" required>
          <Input
            type="number"
            value={totalPrice}
            onChange={(e) => setTotalPrice(e.target.value)}
            placeholder="Nhập tổng giá"
          />
        </Form.Item>

        <Form.Item label="Trạng Thái" required>
          <Select
            value={status}
            onChange={(value) => setStatus(value)}
            placeholder="Chọn trạng thái"
          >
            <Option value="pending">Chờ xử lý</Option>
            <Option value="completed">Hoàn thành</Option>
            <Option value="cancelled">Hủy bỏ</Option>
          </Select>
        </Form.Item>

        {/* Cung cấp thông tin các món trong đơn hàng */}
        <Form.Item label="Món trong đơn hàng">{/* Logic để thêm các món vào đơn hàng */}</Form.Item>

        <Button type="primary" htmlType="submit" loading={isSubmitting}>
          {isSubmitting ? 'Đang thêm...' : 'Thêm Đơn Hàng'}
        </Button>
      </Form>
    </div>
  )
}

export default AddOrder
