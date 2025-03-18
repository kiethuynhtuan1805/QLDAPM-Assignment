import './ManageMenu.scss'
import { useEffect, useMemo, useState } from 'react'
import { Space, Tag, Button, Popconfirm } from 'antd'
import { Icon } from '@iconify/react'
import dayjs from 'dayjs'
import customParseFormat from 'dayjs/plugin/customParseFormat'
import { CustomTable } from 'components'
import { useNavigate } from 'react-router-dom'
import httpClient from 'lib/httpClient'

dayjs.extend(customParseFormat)

const ManageTable = () => {
  // useNavigate
  const navigate = useNavigate()

  const [data, setData] = useState([])

  const [isLoading, setLoading] = useState(true)
  const [searchText, setSearchText] = useState('')
  const [searchedColumn, setSearchedColumn] = useState('')
  const areaNames = useMemo(() => {
    return [...new Set(data.map((item) => item.area))]
  }, [data])

  const handleAdd = () => {
    navigate('add')
  }

  const handleEdit = (e) => {
    navigate(`edit/${e.tableId}`, {
      state: {
        key: 'edit',
        data: e,
        areaNames,
      },
    })
  }

  const handleDelete = async (e) => {
    const updatedItems = data.filter((item) => JSON.stringify(item) !== JSON.stringify(e))
    setData(updatedItems)
    await httpClient.delete(`/table/${e.tableId}`)
  }

  const columns = [
    {
      title: 'STT',
      key: 'stt',
      width: 50,
      isSearched: false,
      render: (_, __, index) => index + 1,
    },
    {
      title: 'Table ID',
      dataIndex: 'tableId',
      key: 'tableId',
      width: 150,
      isSearched: false,
    },
    {
      title: 'Tên bàn',
      dataIndex: 'name',
      key: 'name',
      width: 100,
      isSearched: true,
    },
    {
      title: 'Sức chứa',
      dataIndex: 'capacity',
      key: 'capacity',
      width: 80,
      sorter: (a, b) => a.capacity - b.capacity,
      isSearched: false,
    },
    {
      title: 'Khu vực',
      dataIndex: 'area',
      key: 'area',
      width: 100,
      filters: [
        { text: 'Lầu 1', value: 'Lầu 1' },
        { text: 'Lầu 2', value: 'Lầu 2' },
      ],
      onFilter: (value, record) => record.area === value,
      isSearched: false,
    },
    {
      title: 'Trạng thái',
      dataIndex: 'status',
      key: 'status',
      width: 120,
      render: (status) =>
        status ? (
          <Tag
            color="#87d068"
            style={{ fontSize: '.9rem', padding: '5px 10px', fontWeight: 'bold' }}
          >
            Đang hoạt động
          </Tag>
        ) : (
          <Tag color="#f50" style={{ fontSize: '.9rem', padding: '5px 10px', fontWeight: 'bold' }}>
            Không hoạt động
          </Tag>
        ),
      filters: areaNames.map((item) => ({ text: item, value: item })),
      onFilter: (value, record) => record.status === value,
      isSearched: false,
    },
    {
      title: 'Action',
      key: 'operation',
      fixed: 'right',
      width: 100,
      render: (_, record) => (
        <div className="btn-wrapper">
          <Button onClick={() => handleEdit(record)}>
            <Icon icon="typcn:edit" width={28} height={28} />
          </Button>
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

  useEffect(() => {
    const fetchTables = async () => {
      try {
        const response = await httpClient.get('/tables') // Use httpClient for the GET request
        setData(
          response.data.map((item, index) => ({
            ...item,
            key: item.tableId, // Use tableId as the key
          }))
        )
        console.log(isLoading) // Log the loading state
        setLoading(false)
      } catch (error) {
        console.error('Error fetching tables:', error.message || error)
        setLoading(false)
      }
    }
    fetchTables()
  }, [isLoading])

  return (
    <div className="ManageMenu">
      <Space className="manage-menu-filter">
        <div>
          <div className="range-picker-container"></div>
        </div>
        <Button onClick={handleAdd}>THÊM BÀN ĂN</Button>
      </Space>
      <CustomTable
        columns={columns}
        data={data}
        isLoading={isLoading}
        searchText={searchText}
        searchedColumn={searchedColumn}
        setSearchText={setSearchText}
        setSearchedColumn={setSearchedColumn}
      />
    </div>
  )
}

export default ManageTable
