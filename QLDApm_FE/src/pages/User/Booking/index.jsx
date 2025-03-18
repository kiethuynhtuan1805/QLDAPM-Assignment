import './Booking.scss'
import dayjs from 'dayjs'
import { Icon } from '@iconify/react'
import { useEffect, useRef, useState } from 'react'
import { useLocation, useNavigate } from 'react-router-dom'
import { ReservationCard } from 'components'
import {
  Card,
  Col,
  Row,
  Checkbox,
  Tag,
  Popconfirm,
  Button,
  Table,
  TimePicker,
  Input,
  Modal,
  Skeleton,
  message,
} from 'antd'
import { formatCurrency } from 'components/CommonFunction/formatCurrency'
import { StarRating } from 'components'
import { connect } from 'react-redux'
import { mapDispatchToProps, mapStateToProps } from './rdBooking'
import httpClient from 'lib/httpClient'

const Booking = ({ GetReservations, orderState }) => {
  const location = useLocation()
  const navigate = useNavigate()
  const [params, setParams] = useState({})
  const headerStickyRef = useRef(null)
  const [isSticky, setIsSticky] = useState(false)
  const [capacity, setCapacity] = useState(null)
  const [morningData, setMorningData] = useState(0)
  const [afternoonData, setAfternoonData] = useState(0)
  const [eveningData, setEveningData] = useState(0)
  const [reservation, setReservation] = useState({})
  const [checkedValue, setCheckedValue] = useState('morning')
  const [open, setOpen] = useState(false)
  const [showDiv, setShowDiv] = useState(false)
  const [isLoading, setLoading] = useState(true)
  const [dataSource, setDataSource] = useState([])
  const [time, setTime] = useState()
  const [disabledTime, setDisabledTime] = useState()
  const [messageApi, contextHolder] = message.useMessage()

  const columns = [
    {
      title: 'Món ăn',
      dataIndex: 'dishName',
      key: 'dishNameBT',
      width: '35%',
      render: (_, record) => (
        <div className="d-wrapper">
          <img src={record.image} alt="dish" />
          <div>
            <p
              style={{ fontWeight: 'bold' }}
              onClick={() => {
                navigate(`/home/menu/dish/${record.id}`)
              }}
            >
              {record.dishName}
            </p>
            <StarRating rating={record.rating} />
          </div>
        </div>
      ),
    },
    {
      title: 'Giá',
      dataIndex: 'price',
      key: 'priceBT',
      width: '20%',
      render: (e) => formatCurrency(e),
    },
    {
      title: 'Số lượng',
      dataIndex: 'quantity',
      key: 'quantityBT',
      width: '15%',
    },
    {
      title: 'Tổng',
      dataIndex: 'summaryPrice',
      key: 'summaryPriceBT',
      width: '20%',
      render: (_, record) => formatCurrency(record.price * record.quantity),
    },
    {
      title: 'Xóa',
      key: 'deleteBT',
      width: '10%',
      render: (_, record) => (
        <div className="btn-wrapper">
          <Popconfirm title="Sure to delete?" onConfirm={() => handleDelete(record)}>
            <Button>
              <Icon icon="material-symbols:delete" width={28} height={28} />
            </Button>
          </Popconfirm>
        </div>
      ),
      isSearched: false,
    },
  ]

  const updateReservation = (columns, values) => {
    let updatedReservation = JSON.parse(localStorage.getItem('prevReservation')).map((obj) =>
      obj.id === reservation.id
        ? {
            ...reservation,
            // Tạo các cặp key-value từ mảng columns và values
            ...columns.reduce((acc, column, index) => {
              acc[column] = values[index]
              return acc
            }, {}),
          }
        : obj
    )

    localStorage.setItem('prevReservation', JSON.stringify(updatedReservation))

    setReservation((prevData) => ({
      ...prevData,
      ...columns.reduce((acc, column, index) => {
        acc[column] = values[index]
        return acc
      }, {}),
    }))
  }

  const filterTable = (type) => {
    let result = orderState.table
    if (type === 'morning') {
      result = orderState.table.filter(
        (item1) =>
          !orderState.reservation.some((item2) => {
            const isSameTable = item2.tableId === item1.tableId
            const [hours, minutes] = item2.reservationTime.split(' ')[1].split(':').map(Number)
            const timeInMinutes = hours * 60 + minutes

            const startTime = 8 * 60
            const endTime = 11 * 60 + 59
            return isSameTable && timeInMinutes >= startTime && timeInMinutes <= endTime
          })
      )
    } else if (type === 'afternoon') {
      result = orderState.table.filter(
        (item1) =>
          !orderState.reservation.some((item2) => {
            const isSameTable = item2.tableId === item1.tableId
            const [hours, minutes] = item2.reservationTime.split(' ')[1].split(':').map(Number)
            const timeInMinutes = hours * 60 + minutes

            const startTime = 12 * 60
            const endTime = 16 * 60 + 59

            return isSameTable && timeInMinutes >= startTime && timeInMinutes <= endTime
          })
      )
    } else if (type === 'evening') {
      result = orderState.table.filter(
        (item1) =>
          !orderState.reservation.some((item2) => {
            const isSameTable = item2.tableId === item1.tableId
            const [hours, minutes] = item2.reservationTime.split(' ')[1].split(':').map(Number)
            const timeInMinutes = hours * 60 + minutes

            const startTime = 17 * 60
            const endTime = 20 * 60 + 0

            return isSameTable && timeInMinutes >= startTime && timeInMinutes <= endTime
          })
      )
    }
    return result
  }

  const onChangeTime = (time) => {
    setTime(time)
    updateReservation(['time'], [time])
  }

  const showModal = () => {
    setOpen(true)
  }

  const hideModal = () => {
    setOpen(false)
  }

  const handleDelete = (e) => {
    const updatedData = dataSource.filter((item) => item.dishId !== e.dishId)
    setDataSource(updatedData)
    updateReservation(['dishes'], [updatedData])
  }

  const handleCheckboxChange = (e) => {
    const { name } = e.target
    setCheckedValue(name)

    const hoursArray = []
    let newTime = dayjs(new Date())
    if (name === 'morning') {
      for (let i = 0; i < 24; i++) {
        if (i < 8 || i > 11) {
          hoursArray.push(i)
        }
      }

      newTime = dayjs(new Date(`${params.year}-${params.month}-${params.day}T01:00:00.000Z`))
    } else if (name === 'afternoon') {
      for (let i = 0; i < 24; i++) {
        if (i < 12 || i > 16) {
          hoursArray.push(i)
        }
      }

      newTime = dayjs(new Date(`${params.year}-${params.month}-${params.day}T05:00:00.000Z`))
    } else if (name === 'evening') {
      for (let i = 0; i < 24; i++) {
        if (i < 17 || i > 19) {
          hoursArray.push(i)
        }
      }

      newTime = dayjs(new Date(`${params.year}-${params.month}-${params.day}T10:00:00.000Z`))
    }
    setDisabledTime({
      disabledHours: () => hoursArray,
    })

    setTime(newTime)
    updateReservation(['time', 'checkedValue'], [newTime, name])
    console.log(JSON.parse(localStorage.getItem('prevReservation')))
  }

  const handleTable = (e) => {
    const selectedTags = document.querySelectorAll('.pt.selected')
    selectedTags.forEach((tag) => tag.classList.remove('selected'))
    e.target.classList.add('selected')
    const text = e.target.textContent
    const firstPart = parseInt(text.split(' ')[0])
    setCapacity(firstPart)
    const newArea = [
      ...new Map(
        filterTable(checkedValue)
          .filter((item) => item.capacity === firstPart)
          .map((item) => [item.area, item])
      ).values(),
    ]

    if (newArea.length !== 0) {
      updateReservation(['tableType', 'tableId'], [firstPart, newArea[0].tableId])
      let selectedTags = document.querySelectorAll('.ar.selected')
      selectedTags.forEach((tag) => tag.classList.remove('selected'))

      setTimeout(() => {
        selectedTags = document.getElementById(newArea[0].tableId)
        selectedTags.classList.add('selected')
      }, 50)
    } else {
      updateReservation(['tableType', 'tableId'], [firstPart, ''])
    }
  }

  const handleArea = (e, tableId) => {
    const selectedTags = document.querySelectorAll('.ar.selected')
    selectedTags.forEach((tag) => tag.classList.remove('selected'))
    e.target.classList.add('selected')
    updateReservation(['tableId'], [tableId])
  }

  const handleAdd = () => {
    localStorage.setItem('currReservation', JSON.stringify(reservation))
    navigate('/home/menu/detail')
  }

  const processDishes = async (dishes, orderId) => {
    try {
      const results = await Promise.all(
        dishes.map((item) =>
          httpClient.post(`https://htk.sflavor-demo-app-backend.io.vn/api/v1/order-item`, {
            orderId: orderId,
            dishId: item.dishId,
            quantity: item.quantity,
            specialRequests: 'None',
          })
        )
      )

      console.log('All dishes processed:', results)
    } catch (error) {
      console.error('Error processing dishes:', error)
    }
  }

  const handleSubmit = async () => {
    try {
      messageApi.open({
        type: 'loading',
        content: 'Đang tiến hành đặt bàn...',
        duration: 0,
      })

      const responseRes = await httpClient.post(
        `https://htk.sflavor-demo-app-backend.io.vn/api/v1/reservation`,
        {
          tableId: reservation.tableId,
          reservationTime: new Date(reservation.time)
            .toLocaleString('en-CA', { timeZone: 'Asia/Bangkok', hour12: false })
            .replace(',', ''),
          numberOfGuests: reservation.tableCount,
          specialRequests: 'None',
          status: '1',
        }
      )
      // const currRes = JSON.parse(localStorage.getItem('currReservation'))
      // if (currRes && currRes.length !== 0 && currRes.id === reservation.id) {
      //   localStorage.removeItem('currReservation')
      // }
      // const prevRes = JSON.parse(localStorage.getItem('prevReservation'))
      // localStorage.setItem(
      //   'prevReservation',
      //   JSON.stringify(prevRes.filter((item) => item.id !== reservation.id))
      // )

      await processDishes(reservation.dishes, responseRes.data.orders.orderId)
      messageApi.destroy()
      message.success('Đặt bàn thành công, đang chuyển sang trang thanh toán...', 1.5)

      setTimeout(() => {
        navigate(`/home/reservation/payment`, {
          state: { orderId: responseRes.data.orders.orderId },
        })
      }, 2000)
    } catch (error) {
      console.error('Error:', error.message)
      messageApi.destroy()
      message.error('Đặt bàn thất bại, vui lòng thử lại!', 2.5)
    }
  }

  useEffect(() => {
    if (capacity) {
      setShowDiv(true)
    }
  }, [capacity])

  useEffect(() => {
    const handleScroll = () => {
      if (headerStickyRef.current) {
        const offsetTop = headerStickyRef.current.getBoundingClientRect().top
        setIsSticky(offsetTop <= 0)
      }
    }

    window.addEventListener('scroll', handleScroll)

    return () => {
      window.removeEventListener('scroll', handleScroll)
    }
  }, [])

  useEffect(() => {
    const paramsArray = location.search.split('?').slice(1)

    const newParams = {}

    paramsArray.forEach((param) => {
      const [key, value] = param.split('=')
      newParams[key] = value
    })
    setParams(newParams)

    if (!orderState.r_status) {
      GetReservations(
        `${newParams.year}-${newParams.month}-${newParams.day} 00:00:00`,
        `${newParams.year}-${newParams.month}-${newParams.day} 24:00:00`
      )
    } else {
      let result = filterTable('morning')
      setMorningData(result.length)

      result = filterTable('afternoon')
      setAfternoonData(result.length)

      result = filterTable('evening')
      setEveningData(result.length)
    }

    const data = JSON.parse(localStorage.getItem('prevReservation')).filter(
      (item) =>
        item.date.day === newParams.day &&
        item.date.month === newParams.month &&
        item.date.year === newParams.year &&
        item.id === newParams.id
    )

    if (data && data.length !== 0) {
      setReservation(data[0])

      if (data[0].checkedValue) {
        setCheckedValue(data[0].checkedValue)
        const hoursArray = []
        if (data[0].checkedValue === 'morning') {
          for (let i = 0; i < 24; i++) {
            if (i < 8 || i > 11) {
              hoursArray.push(i)
            }
          }
        } else if (data[0].checkedValue === 'afternoon') {
          for (let i = 0; i < 24; i++) {
            if (i < 12 || i > 16) {
              hoursArray.push(i)
            }
          }
        } else if (data[0].checkedValue === 'evening') {
          for (let i = 0; i < 24; i++) {
            if (i < 17 || i > 19) {
              hoursArray.push(i)
            }
          }
        }
        setDisabledTime({
          disabledHours: () => hoursArray,
        })
      }

      if (data[0].tableType) {
        setCapacity(data[0].tableType)

        setTimeout(() => {
          let selectedTags = document.querySelectorAll('.pt.selected')
          selectedTags.forEach((tag) => tag.classList.remove('selected'))
          const spans = document.querySelectorAll('.pt')
          const spanWithValue = Array.from(spans).filter(
            (span) => span.textContent.trim() === `${data[0].tableType} người`
          )
          spanWithValue.forEach((span) => span.classList.add('selected'))

          selectedTags = document.querySelectorAll('.ar.selected')
          selectedTags.forEach((tag) => tag.classList.remove('selected'))

          const targetElement = document.getElementById(data[0].tableId)
          if (targetElement) {
            targetElement.classList.add('selected')
          }
        }, 50)
      }

      const currData = JSON.parse(localStorage.getItem('currReservation'))
      if (currData && currData.length !== 0 && currData.id === data[0].id) {
        updateReservation(['dishes'], [currData.dishes])
        setDataSource(currData.dishes)
      } else {
        if (data[0].dishes) {
          setDataSource(data[0].dishes)
        }
      }

      if (data[0].time) {
        setTime(dayjs(new Date(data[0].time)))
      }

      setLoading(false)
    } else {
      navigate('/home/reservation')
    }

    return () => {}
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [GetReservations, location.search, orderState.loading])

  return orderState.loading || isLoading ? (
    <Skeleton
      active
      style={{
        marginTop: '40px',
        marginBottom: '40px',
      }}
      paragraph={{ rows: 10 }}
    />
  ) : (
    <div className="Booking">
      {contextHolder}
      <ReservationCard />
      <Row>
        <Col sm={{ span: 0 }} md={{ span: 10 }} style={{ paddingRight: 60 }}>
          <Card className={`header-sticky ${isSticky ? 'sticky' : ''}`} ref={headerStickyRef}>
            <p>Bộ lọc tìm kiếm</p>
            <div style={{ marginBottom: 20 }}>
              <p>Khung giờ</p>
              <div>
                <Checkbox
                  name="morning"
                  checked={checkedValue === 'morning'}
                  onChange={handleCheckboxChange}
                  disabled={morningData === 0}
                >
                  Buổi sáng 08:00 - 12:00 ({morningData})
                </Checkbox>
                <Checkbox
                  name="afternoon"
                  checked={checkedValue === 'afternoon'}
                  onChange={handleCheckboxChange}
                  disabled={afternoonData === 0}
                >
                  Buổi chiều 12:00 - 17:00 ({afternoonData})
                </Checkbox>
                <Checkbox
                  name="evening"
                  checked={checkedValue === 'evening'}
                  onChange={handleCheckboxChange}
                  disabled={eveningData === 0}
                >
                  Buổi tối 17:00 - 20:00 ({eveningData})
                </Checkbox>
              </div>
            </div>
            <div className="t-b-type">
              <p>Loại bàn</p>
              <div>
                {[...new Map(orderState.table.map((item) => [item.capacity, item])).values()]
                  .sort((a, b) => a.capacity - b.capacity)
                  .map((item, index) => {
                    return (
                      <Tag className="pt" key={`type-table-${item.tableId}`} onClick={handleTable}>
                        {item.capacity} người
                      </Tag>
                    )
                  })}
              </div>
            </div>
            <div className={`t-b-type area ${showDiv ? 'show' : ''} `}>
              <p>Khu vực</p>
              <div>
                {[
                  ...new Map(
                    filterTable(checkedValue)
                      .filter((item) => item.capacity === capacity)
                      .map((item) => [item.area, item])
                  ).values(),
                ].length !== 0 ? (
                  [
                    ...new Map(
                      filterTable(checkedValue)
                        .filter((item) => item.capacity === capacity)
                        .map((item) => [item.area, item])
                    ).values(),
                  ]
                    .sort((a, b) => a.area - b.area)
                    .map((item, index) => {
                      return (
                        <Tag
                          className={`ar`}
                          id={item.tableId}
                          key={`area-table-${item.tableId}`}
                          onClick={(e) => {
                            handleArea(e, item.tableId)
                          }}
                        >
                          {item.area}
                        </Tag>
                      )
                    })
                ) : (
                  <p style={{ marginTop: 10, color: 'red' }}> Không có bàn phù hợp</p>
                )}
              </div>
            </div>
            <div className="payment">
              <p style={{ marginBottom: 25 }}>Coupon</p>
              <Input
                size="large"
                placeholder="large size"
                prefix={
                  <Icon icon="mingcute:coupon-line" width={28} height={28} style={{ height: 32 }} />
                }
              />
            </div>
          </Card>
        </Col>
        <Col sm={{ span: 24 }} md={{ span: 14 }} className="m-content">
          <div className="t-wrapper">
            <div>
              <p>Giờ nhận bàn:</p>
              <TimePicker
                value={time}
                onChange={onChangeTime}
                size="large"
                format={'HH:mm'}
                allowClear={false}
                disabledTime={() => {
                  return disabledTime
                }}
              />
            </div>
            {reservation.dishes.length !== 0 && reservation.tableId !== '' ? (
              <div className="btn-order" onClick={showModal}>
                Đặt bàn
              </div>
            ) : (
              <div className="btn-order" style={{ cursor: 'not-allowed' }}>
                Đặt bàn
              </div>
            )}
            <Modal
              title="Kiểm tra lại thông tin đặt bàn!"
              open={open}
              onOk={handleSubmit}
              onCancel={hideModal}
              okText="Xác nhận"
              cancelText="Hủy"
              okButtonProps={{
                style: {
                  height: '42px',
                },
              }}
              cancelButtonProps={{
                style: {
                  height: '42px',
                },
              }}
            >
              <p style={{ paddingTop: 20, paddingBottom: 5 }}>
                Ngày đặt bàn: <b>{`${params.day}/${params.month}/${params.year}`}</b>
              </p>
              <p style={{ paddingBottom: 5 }}>
                Giờ đặt bàn:{' '}
                <b>
                  {new Date(reservation.time).getHours()}:
                  {String(new Date(reservation.time).getMinutes()).padStart(2, '0')}
                </b>
              </p>
              <p style={{ paddingBottom: 5 }}>
                Loại bàn: <b>{reservation.tableType}</b> người
              </p>
              <p style={{ paddingBottom: 5 }}>
                Tổng số bàn: <b>{reservation.tableCount}</b> bàn
              </p>
              <div>
                <p>Món ăn:</p>
                <ul style={{ marginLeft: 50 }}>
                  {dataSource.map((item, index) => {
                    return (
                      <li key={`list-item-booking-${index}`}>
                        {item.dishName} x{item.quantity}
                      </li>
                    )
                  })}
                </ul>
              </div>
            </Modal>
          </div>
          {reservation.tableId ? (
            <Table
              dataSource={dataSource}
              columns={columns}
              className="d-table"
              pagination={false}
              footer={() => (
                <div className="d-t-footer" onClick={handleAdd}>
                  Thêm món
                </div>
              )}
            />
          ) : (
            <div className="notification-content" style={{ maxHeight: 500 }}>
              <img src={require('assets/images/notiMenu.png')} alt="" />
              <p>Vui lòng chọn bàn!</p>
            </div>
          )}
        </Col>
      </Row>
    </div>
  )
}

export default connect(mapStateToProps, mapDispatchToProps)(Booking)
