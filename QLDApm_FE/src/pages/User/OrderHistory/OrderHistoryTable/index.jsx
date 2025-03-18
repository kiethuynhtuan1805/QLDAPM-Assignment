import { List, Typography, Row, Col } from 'antd'
import { formatCurrency } from 'components/CommonFunction/formatCurrency'
import { useState, useEffect } from 'react'
import httpClient from 'lib/httpClient'

const OrderHistoryList = ({ data }) => {
  const [dishesDetails, setDishesDetails] = useState({})

  // Fetch dish details for multiple dishIds at once
  const fetchDishDetails = async (dishId) => {
    try {
      const response = await httpClient.get(`/dish/${dishId}`)
      if (response.status) {
        return { dishId, details: response.data }
      } else {
        console.error(response.message)
        return { dishId, details: null }
      }
    } catch (error) {
      console.error('Error fetching dish details:', error)
      return { dishId, details: null }
    }
  }

  const getDishDetails = (dishId) => {
    if (dishesDetails[dishId]) {
      return {
        name: dishesDetails[dishId].dishName,
        price: dishesDetails[dishId].price,
      }
    }
    return { name: 'Loading...', price: 0 }
  }

  useEffect(() => {
    // Extract unique dishIds from the order data
    const uniqueDishIds = [...new Set(data.map((item) => item.dishId))]

    // Use Promise.all to fetch details for all unique dishIds concurrently
    Promise.all(uniqueDishIds.map((id) => fetchDishDetails(id)))
      .then((results) => {
        const newDishesDetails = {}
        results.forEach(({ dishId, details }) => {
          if (details) {
            newDishesDetails[dishId] = details
          }
        })
        setDishesDetails(newDishesDetails)
      })
      .catch((error) => {
        console.error('Error fetching dish details:', error)
      })
  }, [data])

  return (
    <div>
      <Row style={{ width: '100%', marginBottom: '16px' }}>
        <Col span={2} style={{ textAlign: 'center' }}>
          <Typography.Text strong>STT</Typography.Text>
        </Col>
        <Col span={6} style={{ textAlign: 'center' }}>
          <Typography.Text strong>Tên món</Typography.Text>
        </Col>
        <Col span={4} style={{ textAlign: 'center' }}>
          <Typography.Text strong>Số lượng</Typography.Text>
        </Col>
        <Col span={6} style={{ textAlign: 'center' }}>
          <Typography.Text strong>Giá</Typography.Text>
        </Col>
        <Col span={6} style={{ textAlign: 'center' }}>
          <Typography.Text strong>Yêu cầu đặc biệt</Typography.Text>
        </Col>
      </Row>
      <List
        itemLayout="horizontal"
        dataSource={data}
        renderItem={(item, index) => {
          const { name, price } = getDishDetails(item.dishId)

          return (
            <List.Item>
              <Row style={{ width: '100%' }}>
                <Col span={2} style={{ textAlign: 'center' }}>
                  <Typography.Text>{index + 1}</Typography.Text> {/* STT */}
                </Col>
                <Col span={6} style={{ textAlign: 'center' }}>
                  <Typography.Text strong>{name}</Typography.Text> {/* Dish Name */}
                </Col>
                <Col span={4} style={{ textAlign: 'center' }}>
                  <Typography.Text>{item.quantity}</Typography.Text> {/* Quantity */}
                </Col>
                <Col span={6} style={{ textAlign: 'center' }}>
                  <Typography.Text>{price ? formatCurrency(price) : 'N/A'}</Typography.Text>{' '}
                  {/* Price */}
                </Col>
                <Col span={6} style={{ textAlign: 'center' }}>
                  <Typography.Text>{item.specialRequests || 'N/A'}</Typography.Text>{' '}
                  {/* Special Request */}
                </Col>
              </Row>
            </List.Item>
          )
        }}
      />
    </div>
  )
}

export default OrderHistoryList
