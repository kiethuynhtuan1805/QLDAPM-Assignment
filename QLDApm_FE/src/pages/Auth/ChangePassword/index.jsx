import { Button, Form, Input, message } from 'antd'
import './ChangePassword.scss'
import httpClient from 'lib/httpClient'
import { useSelector } from 'react-redux'

const ChangePassword = () => {
  const [form] = Form.useForm()
  const userData = useSelector((state) => state.userManage.data?.data) // Adjust the path to match your Redux structure

  const onFinish = async (values) => {
    try {
      const response = await httpClient.put(`/user/account/change-password/${userData.userId}`, {
        currentPassword: values.oldPassword,
        newPassword: values.newPassword,
        confirmPassword: values.confirmPassword,
      })
      if (response.data.status) message.success('Mật khẩu đã được thay đổi thành công!')
      form.resetFields()
    } catch (error) {
      message.error(error.response?.data?.message || 'Có lỗi xảy ra, vui lòng thử lại!')
    }
  }
  const onFinishFailed = (errorInfo) => {
    console.log('Failed:', errorInfo)
  }

  return (
    <div className="container">
      <Form
        name="basic"
        labelCol={{
          span: 8,
        }}
        wrapperCol={{
          span: 16,
        }}
        style={{
          maxWidth: 600,
        }}
        initialValues={{
          remember: true,
        }}
        onFinish={onFinish}
        onFinishFailed={onFinishFailed}
        autoComplete="off"
      >
        <Form.Item
          label="Mật khẩu cũ"
          name="oldPassword"
          rules={[
            {
              required: true,
              message: 'Nhập mật khẩu cũ!',
            },
          ]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          label="Mật khẩu mới"
          name="newPassword"
          rules={[
            {
              required: true,
              message: 'Nhập mật khẩu mới!',
            },
          ]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          label="Xác nhận mật khẩu"
          name="confirmPassword"
          rules={[
            {
              required: true,
              message: 'Mật khẩu mới không đúng!',
            },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('newPassword') === value) {
                  return Promise.resolve()
                }
                return Promise.reject(new Error('Mật khẩu không khớp!'))
              },
            }),
          ]}
        >
          <Input.Password />
        </Form.Item>
        <Form.Item
          wrapperCol={{
            offset: 8,
            span: 16,
          }}
        >
          <Button type="primary" htmlType="submit">
            Xác nhận
          </Button>
        </Form.Item>
      </Form>
    </div>
  )
}

export default ChangePassword
