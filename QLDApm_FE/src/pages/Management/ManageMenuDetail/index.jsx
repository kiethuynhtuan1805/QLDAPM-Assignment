import './ManageMenuDetail.scss'
import axios from 'axios'
import { useEffect, useState } from 'react'
import { useLocation } from 'react-router-dom'
import { DishDetail } from 'components'
import { PlusOutlined, LoadingOutlined } from '@ant-design/icons'
import { Button, Form, Input, InputNumber, Select, Upload, Radio, Skeleton, message } from 'antd'
import { EditableTable } from 'components'
const { TextArea } = Input

const ManageMenuDetail = () => {
  // useLocation
  const location = useLocation()
  const [data, setData] = useState({
    categoryName: '',
    dishName: '',
    description: '',
    price: 0,
    image: '',
    available: true,
    rating: 0,
    classify: 'starter',
  })
  const [isVisible, setIsVisible] = useState(false)
  const [form] = Form.useForm()
  const [messageApi, contextHolder] = message.useMessage()
  const [dataForm, setDataForm] = useState({})
  const [fileList, setFileList] = useState([])
  const [fileImg, setFileImg] = useState(null)
  const [isLoadingImg, setLoadingImg] = useState(false)
  const [categories, setCategories] = useState([])
  const [isLoading, setLoading] = useState(true)
  const [dataSource, setDataSource] = useState(() => {
    const savedData = localStorage.getItem('menuDataSource')
    return savedData ? JSON.parse(savedData) : []
  })

  const getBase64 = (img, callback) => {
    const reader = new FileReader()
    reader.addEventListener('load', () => callback(reader.result))
    reader.readAsDataURL(img)
  }

  const beforeUpload = (file) => {
    const isJpgOrPng = file.type === 'image/jpeg' || file.type === 'image/png'
    if (!isJpgOrPng) {
      message.error('Chỉ có thể upload JPG/PNG file!')
    }
    const isLt2M = file.size / 1024 / 1024 < 2
    if (!isLt2M) {
      message.error('Hình ảnh phải có dung lượng <= 2MB!')
    }
    return isJpgOrPng && isLt2M
  }

  const togglePanel = async () => {
    try {
      if (!data.dishId) {
        const formData = await form.validateFields()
        const selectedCategory = categories.find((item) => item.categoryId === formData.categoryId)
        setData({
          ...formData,
          image: formData.image ? formData.image.url : '',
          categoryName: selectedCategory.name,
          rating: 0,
        })
      }
      setIsVisible(!isVisible)
    } catch (error) {
      console.log('Validation Failed:', error)
    }
  }

  const handleAdd = async () => {
    try {
      let formData = await form.validateFields()

      formData = {
        ...formData,
        file: fileImg ? fileImg : '',
        image: fileList.length > 0 ? fileList[0].url : '',
      }

      if (dataForm.key === undefined) {
        const newData = {
          key: Math.random().toString(36).substr(2, 9),
          ...formData,
        }
        setDataSource([...dataSource, newData])
        localStorage.setItem('menuDataSource', JSON.stringify([...dataSource, newData]))
        form.resetFields()
        setFileList([])
        setFileImg(null)
        setLoadingImg(false)
      } else {
        const updatedData = {
          key: dataForm.key,
          ...formData,
        }

        const updatedDataSource = dataSource.map((item) =>
          item.key === updatedData.key ? updatedData : item
        )

        setDataSource(updatedDataSource)
        setDataForm({})
        localStorage.setItem('menuDataSource', JSON.stringify([...updatedDataSource]))
        form.resetFields()
        setFileList([])
        setFileImg(null)
        setLoadingImg(false)
      }
    } catch (error) {
      console.log('Validation Failed:', error)
    }
  }

  const handleSubmit = async () => {
    const accessToken = localStorage.getItem('accessToken')

    if (data.dishId) {
      const loadingMessage = messageApi.open({
        type: 'loading',
        content: 'Đang sửa dữ liệu...',
        duration: 0,
      })

      try {
        const formData = await form.validateFields()
        setData({
          ...data,
          dishName: formData.dishName,
          available: formData.available,
          classify: formData.classify,
          categoryId: formData.categoryId,
          price: formData.price,
          description: formData.description,
        })
        const newFormData = new FormData()
        newFormData.append('categoryId', formData.categoryId)
        newFormData.append('name', formData.dishName)
        newFormData.append('description', formData.description)
        newFormData.append('price', formData.price)
        newFormData.append('image', fileImg ? fileImg : data.image)
        newFormData.append('available', formData.available)
        newFormData.append('rating', 0)
        newFormData.append('classify', formData.classify)
        await axios.put(
          `https://htk.sflavor-demo-app-backend.io.vn/api/v1/dish/${data.dishId}`,
          newFormData,
          {
            headers: {
              Authorization: `bearer ${accessToken}`,
              'Content-Type': 'multipart/form-data',
            },
          }
        )

        loadingMessage()
        messageApi.open({
          type: 'success',
          content: 'Sửa dữ liệu thành công!',
          duration: 2,
        })
      } catch (error) {
        console.error('Error:', error)

        loadingMessage()
        messageApi.open({
          type: 'error',
          content: 'Có lỗi xảy ra khi sửa dữ liệu!',
          duration: 2,
        })
      } finally {
        setTimeout(messageApi.destroy, 2000)
      }
    } else {
      const loadingMessage = messageApi.open({
        type: 'loading',
        content: 'Đang thêm dữ liệu...',
        duration: 0,
      })

      try {
        for (let i = 0; i < dataSource.length; i++) {
          const formData = new FormData()
          formData.append('categoryId', dataSource[i].categoryId)
          formData.append('name', dataSource[i].dishName)
          formData.append('description', dataSource[i].description)
          formData.append('price', dataSource[i].price)
          formData.append('image', dataSource[i].file ? dataSource[i].file : '')
          formData.append('available', dataSource[i].available)
          formData.append('rating', 0)
          formData.append('classify', dataSource[i].classify)
          await axios.post('https://htk.sflavor-demo-app-backend.io.vn/api/v1/dish', formData, {
            headers: {
              Authorization: `bearer ${accessToken}`,
              'Content-Type': 'multipart/form-data',
            },
          })
        }

        setDataSource([])
        setData({
          categoryName: '',
          dishName: '',
          description: '',
          price: 0,
          image: '',
          available: true,
          rating: 0,
          classify: 'starter',
        })
        localStorage.removeItem('menuDataSource')

        loadingMessage()
        messageApi.open({
          type: 'success',
          content: 'Thêm dữ liệu thành công!',
          duration: 2,
        })
      } catch (error) {
        console.error('Error:', error)

        loadingMessage()
        messageApi.open({
          type: 'error',
          content: 'Có lỗi xảy ra khi thêm dữ liệu!',
          duration: 2,
        })
      } finally {
        setTimeout(messageApi.destroy, 2000)
      }
    }
  }

  const handleEdit = (e) => {
    const dataFormApi = e
    setDataForm(dataFormApi)
    setFileList([
      {
        uid: dataFormApi.file.uid,
        name: dataFormApi.file.name,
        status: 'done',
        url: dataFormApi.image,
      },
    ])
    form.setFieldsValue(dataFormApi)
  }

  const handleDelete = (e) => {
    const newData = [...dataSource].filter((item) => item.key !== e.key)
    setDataSource(newData)
    localStorage.setItem('menuDataSource', JSON.stringify([...newData]))
  }

  const uploadButton = (
    <button
      style={{
        border: 0,
        background: 'none',
      }}
      type="button"
    >
      {isLoadingImg ? <LoadingOutlined /> : <PlusOutlined />}
      <div
        style={{
          marginTop: 8,
        }}
      >
        Upload
      </div>
    </button>
  )

  useEffect(() => {
    const pathname = location.pathname.split('/')
    const fetchData = async () => {
      try {
        const [categoriesResponse, dishResponse] = await Promise.all([
          axios.get('https://htk.sflavor-demo-app-backend.io.vn/v1/categories'),
          axios.get(`https://htk.sflavor-demo-app-backend.io.vn/v1/dish/${pathname[4]}`),
        ])

        setCategories(categoriesResponse.data.data)
        setData(dishResponse.data.data)
      } catch (error) {
        console.error(error.message)
      } finally {
        setLoading(false)
      }
    }

    if (location.state) {
      axios
        .get('https://htk.sflavor-demo-app-backend.io.vn/api/v1/categories')
        .then((response) => {
          setCategories(response.data.data)
          setLoading(false)
        })
        .catch((error) => {
          console.error(error.message)
        })

      const { stt, key, ...rest } = location.state.data
      setData(rest)
    } else {
      if (pathname[3] === 'add') {
        axios
          .get('https://htk.sflavor-demo-app-backend.io.vn/api/v1/categories')
          .then((response) => {
            setCategories(response.data.data)
            setLoading(false)
          })
          .catch((error) => {
            console.error(error.message)
          })
      } else {
        fetchData()
      }
    }
    return () => {}
  }, [location])

  return isLoading ? (
    <Skeleton
      active
      style={{
        marginTop: '40px',
        marginBottom: '40px',
      }}
      paragraph={{ rows: 20 }}
    />
  ) : (
    <div className="ManageMenuDetail">
      {contextHolder}
      <div className="left-panel">
        <Form
          form={form}
          labelCol={{
            span: 4,
          }}
          wrapperCol={{
            span: 14,
          }}
          layout="horizontal"
          style={{
            maxWidth: 1000,
          }}
          size="large"
          initialValues={{
            dishName: data.dishName,
            available: data.available,
            classify: data.classify,
            categoryId: data.categoryId ? data.categoryId : categories[0].categoryId,
            price: data.price,
            description: data.description,
          }}
        >
          <Form.Item
            label="Tên món ăn"
            name={'dishName'}
            rules={[
              {
                required: true,
                message: 'Vui lòng nhập tên món ăn!',
              },
            ]}
          >
            <Input spellCheck={false} />
          </Form.Item>
          <Form.Item label="Trạng thái" name={'available'}>
            <Radio.Group>
              <Radio value={true}> Sẵn sàng phục vụ </Radio>
              <Radio value={false}> Tạm ngưng phục vụ </Radio>
            </Radio.Group>
          </Form.Item>
          <Form.Item label="Loại bữa ăn" name={'classify'}>
            <Select>
              <Select.Option value="starter">Starter</Select.Option>
              <Select.Option value="main">Main Course</Select.Option>
              <Select.Option value="dessert">Dessert</Select.Option>
              <Select.Option value="drinks">Drinks</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item label="Phân loại" name={'categoryId'}>
            <Select>
              {categories.map((item, index) => {
                return (
                  <Select.Option value={item.categoryId} key={`option-category-${index}`}>
                    {item.name}
                  </Select.Option>
                )
              })}
            </Select>
          </Form.Item>
          <Form.Item label="Giá" name={'price'}>
            <InputNumber
              min={0}
              step={1000}
              onChange={(value) => form.setFieldsValue({ price: value })}
              style={{ width: '40%' }}
            />
          </Form.Item>
          <Form.Item
            label="Mô tả món ăn"
            name={'description'}
            rules={[
              {
                required: true,
                message: 'Vui lòng nhập mô tả món ăn!',
              },
            ]}
          >
            <TextArea rows={5} spellCheck={false} style={{ textAlign: 'justify' }} />
          </Form.Item>
          <Form.Item
            label="Upload"
            name={'image'}
            rules={
              !data.image
                ? [
                    {
                      required: true,
                      message: 'Vui lòng upload ảnh!',
                    },
                  ]
                : []
            }
          >
            <Upload
              listType="picture-card"
              className="avatar-uploader"
              fileList={fileList}
              showUploadList={false}
              beforeUpload={beforeUpload}
              customRequest={(options) => {
                const { onSuccess, file } = options
                setFileImg(file)
                getBase64(file, (url) => {
                  setFileList([
                    {
                      uid: file.uid,
                      name: file.name,
                      status: 'done',
                      url: url,
                    },
                  ])
                  onSuccess(null, file)
                })
              }}
            >
              {fileList.length > 0 ? (
                <img
                  src={fileList[0].url}
                  alt="avatar"
                  style={{
                    width: '100%',
                  }}
                />
              ) : data.image ? (
                <img
                  src={data.image}
                  alt="avatar"
                  style={{
                    width: '100%',
                  }}
                />
              ) : (
                uploadButton
              )}
            </Upload>
          </Form.Item>
        </Form>
        <EditableTable
          dataSource={dataSource}
          setDataSource={setDataSource}
          handleEdit={handleEdit}
          handleDelete={handleDelete}
        />
      </div>
      <div className={`right-panel ${isVisible ? 'visible' : 'hidden'}`}>
        <div>
          <DishDetail data={data} type={'management'} />
        </div>
      </div>
      <div className="btn-wrapper">
        <Button className="toggle-button" onClick={togglePanel} type="primary">
          {isVisible ? 'Hide' : 'Review'}
        </Button>
        {!isVisible && (
          <>
            {!data.dishId && (
              <Button onClick={handleAdd} className="btn-add-row">
                Thêm tạm thời
              </Button>
            )}
            <Button
              onClick={handleSubmit}
              className="btn-submit"
              disabled={!data.dishId && dataSource.length === 0}
            >
              {!data.dishId ? 'Thêm toàn bộ' : 'Sửa'}
            </Button>
          </>
        )}
      </div>
    </div>
  )
}

export default ManageMenuDetail
