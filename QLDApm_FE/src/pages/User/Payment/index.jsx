import './Payment.scss'

import { Button, Card, Divider, List, message } from 'antd'
import { formatCurrency } from 'components/CommonFunction/formatCurrency'
import httpClient from 'lib/httpClient'
import { useEffect, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'

export default function Payment() {
  const navigate = useNavigate()
  const location = useLocation()
  const [timeoutId, setTimeoutId] = useState(null)

  const { dishes, ...reservation } = JSON.parse(localStorage.getItem('currReservation'))
  const { orderId } = location.state || {}

  const handlePayment = async () => {
    try {
      const response = await httpClient.put(`/payment/orderId/${orderId}`, {
        paymentMethod: 'cash',
        paymentStatus: 'paid',
      })

      if (reservation && response.orderId === reservation.id) {
        localStorage.removeItem('currReservation')
      }

      const prevRes = JSON.parse(localStorage.getItem('prevReservation')) || []
      localStorage.setItem(
        'prevReservation',
        JSON.stringify(prevRes.filter((item) => item.id !== reservation.id))
      )

      message.success('Thanh toán thành công')
      const timeoutId = setTimeout(() => {
        navigate('/home/menu/detail')
      }, 2000)

      setTimeoutId(timeoutId)
    } catch (error) {
      console.error('Lỗi khi thanh toán:', error.response ? error.response.data : error.message)
      message.error('Thanh toán thất bại')
    }
  }

  useEffect(() => {
    return () => {
      if (timeoutId) {
        clearTimeout(timeoutId)
      }
    }
  }, [timeoutId])

  const total = dishes.reduce((sum, dish) => sum + dish.price * dish.quantity, 0)

  return (
    <div className="Payment">
      <Card className="payment-cart__container">
        <h3 className="payment-cart__title">Thông tin đặt bàn</h3>
        <div className="payment-cart__details">
          <div className="payment-cart__details-item">
            <span className="payment-cart__details-item-title">Mã đặt bàn</span>
            <span className="payment-cart__details-item-value">{reservation.id}</span>
          </div>
          <Divider style={{ margin: '7px 0' }} />

          <div className="payment-cart__details-item">
            <span className="payment-cart__details-item-title">Thời gian</span>
            <span className="payment-cart__details-item-value">
              {new Date(reservation.time).toLocaleString()}
            </span>
          </div>
          <Divider style={{ margin: '7px 0' }} />

          <div className="payment-cart__details-item">
            <span className="payment-cart__details-item-title">Loại bàn</span>
            <span className="payment-cart__details-item-value">{reservation.tableType} người</span>
          </div>
          <Divider style={{ margin: '7px 0' }} />

          <div className="payment-cart__details-item">
            <span className="payment-cart__details-item-title">Số lượng bàn đặt</span>
            <span className="payment-cart__details-item-value">{reservation.tableCount}</span>
          </div>
        </div>

        {/* <Divider /> */}

        <h3 className="payment-cart__title">Thông tin đơn hàng</h3>
        <List
          className="payment-cart__order-list"
          itemLayout="horizontal"
          dataSource={dishes}
          renderItem={(dish) => (
            <List.Item className="payment-cart__order-list-item">
              <div className="payment-cart__order-list-item-meta">
                <div className="payment-cart__order-list-item-meta-avatar">
                  <img src={dish.image} alt={dish.dishName} width={80} height={80} />
                </div>
                <div className="payment-cart__order-list-item-meta-info">
                  <p className="payment-cart__order-list-item-meta-info-name">{dish.dishName}</p>
                  <span className="payment-cart__order-list-item-meta-info-category">
                    {dish.categoryName}
                  </span>
                </div>
              </div>
              <div className="payment-cart__order-list-item-price">
                <div className="payment-cart__order-list-item-price-amount">
                  {formatCurrency(dish.price)}
                </div>
                <div className="payment-cart__order-list-item-price-quantity">{`Số lượng: ${dish.quantity}`}</div>
              </div>
            </List.Item>
          )}
        />
        <Divider />
        <div className="payment-cart__summary">
          {/* <div className="payment-cart__summary-row">
            <span className="payment-cart__summary-row-title">Subtotal</span>
            <span className="payment-cart__summary-row-value">{formatCurrency(subtotal)}</span>
          </div>
          <div className="payment-cart__summary-row">
            <span className="payment-cart__summary-row-title">
              Service Charge (5%)
              <Tooltip title="5% service charge applied to the subtotal">
                <QuestionCircleOutlined className="payment-cart__tooltip" />
              </Tooltip>
            </span>
            <span className="payment-cart__summary-row-value">
              {formatCurrency(serviceCharge)}
            </span>
          </div>
          <Divider /> */}
          <div className="payment-cart__summary-row">
            <strong className="payment-cart__summary-total-title">Total</strong>
            <strong className="payment-cart__summary-total">{formatCurrency(total)}</strong>
          </div>
          <div className="payment-card__button-container">
            <Button
              onClick={handlePayment}
              type="primary"
              className="payment-card__button"
              size="large"
              block
            >
              Thanh toán
            </Button>
          </div>
        </div>
      </Card>
    </div>
  )
}
