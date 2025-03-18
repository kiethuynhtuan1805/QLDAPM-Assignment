import { message, Tag } from 'antd'
import { CustomTable } from 'components'
import dayjs from 'dayjs'
import customParseFormat from 'dayjs/plugin/customParseFormat'
import { useEffect, useState } from 'react'
import './OrderHistory.scss'
import OrderHistoryTable from './OrderHistoryTable'
import httpClient from 'lib/httpClient'
import { formatCurrency } from 'components/CommonFunction/formatCurrency'

dayjs.extend(customParseFormat)

const ManageTable = () => {
  const [data, setData] = useState([])

  const [isLoading, setLoading] = useState(false)
  const [searchText, setSearchText] = useState('')
  const [searchedColumn, setSearchedColumn] = useState('')

  const renderOrderedDishes = (record) => {
    console.log(record)
    const renderData = record.orderItems.map((item) => ({
      ...item,
      key: item.dishId,
    }))

    return <OrderHistoryTable data={renderData} />
  }
  // Mimicking the given data

  useEffect(() => {
    // Simulate fetching data
    const fetchData = async () => {
      try {
        setLoading(true)
        const orderRes = await httpClient.get('/order/user')
        const dataWithKeys = orderRes.data.map((item) => ({
          ...item,
          key: item.orderId, // Add a unique key for each order
        }))
        setData(dataWithKeys)
        setLoading(false)
      } catch (error) {
        message.error('Lỗi khi lấy dữ liệu đơn hàng')
        setLoading(false)
      }
    }
    fetchData()
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [])

  const columns = [
    {
      title: 'STT',
      key: 'stt',
      width: 50,
      isSearched: false,
      render: (_, __, index) => index + 1,
    },
    {
      title: 'Mã đơn hàng',
      dataIndex: 'orderId',
      key: 'orderId',
      width: 150,
      isSearched: false,
    },
    {
      title: 'Mã đặt chỗ',
      dataIndex: 'reservationId',
      key: 'reservationId',
      width: 100,
      isSearched: true,
    },
    {
      title: 'Ngày đặt',
      dataIndex: 'orderDate',
      key: 'orderDate',
      width: 180,
      render: (orderDate) => dayjs(orderDate).format('YYYY-MM-DD HH:mm:ss'),
      isSearched: false,
    },
    {
      title: 'Số lượng món',
      key: 'numItems',
      width: 150,
      render: (record) => (record.orderItems ? record.orderItems.length : 0),
      isSearched: false,
    },
    {
      title: 'Tổng tiền',
      dataIndex: 'totalPrice',
      key: 'totalPrice',
      width: 100,
      sorter: (a, b) => a.totalPrice - b.totalPrice,
      render: (totalPrice) => formatCurrency(totalPrice),
      isSearched: false,
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status) => (
        <Tag
          color={status === 'pending' ? '#f50' : '#87d068'}
          style={{ fontSize: '.9rem', padding: '5px 10px', fontWeight: 'bold' }}
        >
          {status === 'pending' ? 'Đang chờ' : 'Hoàn thành'}
        </Tag>
      ),
      filters: [
        { text: 'Đang chờ', value: 'pending' },
        { text: 'Hoàn thành', value: 'paid' },
      ],
      onFilter: (value, record) => record.status === value,
      isSearched: false,
    },
  ]

  return (
    <div className="OrderHistory">
      <CustomTable
        columns={columns}
        data={data}
        isLoading={isLoading}
        searchText={searchText}
        searchedColumn={searchedColumn}
        setSearchText={setSearchText}
        setSearchedColumn={setSearchedColumn}
        expandedRowRender={renderOrderedDishes}
      />
    </div>
  )
}

export default ManageTable
