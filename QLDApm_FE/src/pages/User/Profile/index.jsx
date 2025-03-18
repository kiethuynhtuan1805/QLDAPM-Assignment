import { useEffect, useState } from 'react'
import './Profile.scss'
import { useSelector } from 'react-redux'
import { message, Select } from 'antd'
import httpClient from 'lib/httpClient'
import { useNavigate } from 'react-router-dom'

export default function Profile() {
  const navigate = useNavigate()
  const { Option } = Select
  const [selectedMenu, setSelectedMenu] = useState(null)
  const [userState, setUserState] = useState({
    nameRole: null,
    firstname: null,
    lastname: null,
    gender: null,
    dateOfBirth: null,
    avatar: null,
    address: null,
    email: null,
    phone: null,
    username: null,
    userId: null,
  })

  const userData = useSelector((state) => state.userManage.data?.data) // Adjust the path to match your Redux structure
  const [messageApi, contextHolder] = message.useMessage()

  useEffect(() => {
    if (userData) {
      console.log(userData)
      setUserState({
        nameRole: userData.nameRole || 'CUSTOMER',
        avatar: userData.avatar,
        firstname: userData.firstname,
        lastname: userData.lastname,
        gender: userData.gender,
        dateOfBirth: userData.dateOfBirth,
        address: userData.address,
        email: userData.email,
        phone: userData.phone,
        username: userData.username,
        userId: userData.userId,
      })
    }
  }, [userData])

  const handleInputChange = (key, value) => {
    setUserState((prevState) => ({
      ...prevState,
      [key]: value,
    }))
  }

  const handleMenuClick = (menu) => {
    setSelectedMenu(menu)
  }

  const handleSubmit = async (e) => {
    e.preventDefault()
    const response = await httpClient.put(`/user/${userState.userId}`, {
      ...userData,
      ...userState,
    })
    if (response.status) {
      setUserState((prevState) => ({ ...prevState, ...response.data }))
      messageApi.open({
        type: 'success',
        content: 'Sửa dữ liệu thành công!',
        duration: 2,
      })
    }
  }

  return (
    <div className="Profile">
      {contextHolder}
      <div className="Profile-left">
        <div className="user-info">
          <img
            src={
              userState.avatar ||
              'https://cdn.tuoitre.vn/zoom/700_525/2019/5/8/avatar-publicitystill-h2019-1557284559744252594756-crop-15572850428231644565436.jpg'
            }
            alt="User Avatar"
            className="user-avatar"
          />
          <h2 className="user-name">
            {userData.lastname} {userData.firstname}
          </h2>
          <p className="user-job">Nhà phát triển web</p>
        </div>
        <div className="menu">
          <button
            className={`menu-item ${selectedMenu === 'Profile' ? 'active' : ''}`}
            onClick={() => handleMenuClick('Profile')}
          >
            Hồ sơ
          </button>
          <button
            className={`menu-item ${selectedMenu === 'My Order' ? 'active' : ''}`}
            onClick={() => navigate('/user/orders')}
          >
            Đơn hàng của tôi
          </button>
          <button
            className={`menu-item ${selectedMenu === 'Change Password' ? 'active' : ''}`}
            onClick={() => navigate('/user/change-password')}
          >
            Đổi mật khẩu
          </button>
        </div>
      </div>
      <div className="Profile-right">
        <form onSubmit={handleSubmit}>
          <div className="form-row">
            <div className="form-group">
              <label htmlFor="lastname">Họ:</label>
              <input
                type="text"
                id="lastname"
                value={userState.lastname}
                onChange={(e) => handleInputChange('lastname', e.target.value)}
                required
              />
            </div>
            <div className="form-group">
              <label htmlFor="firstname">Tên:</label>
              <input
                type="text"
                id="firstname"
                value={userState.firstname}
                onChange={(e) => handleInputChange('firstname', e.target.value)}
                required
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="gender">Giới tính:</label>
            <Select
              id="gender"
              value={userState.gender || 'Không xác định'}
              onChange={(value) => handleInputChange('gender', value)}
              style={{ width: '100%' }}
            >
              <Option value="Nam">Nam</Option>
              <Option value="Nữ">Nữ</Option>
              <Option value="Không xác định">Không xác định</Option>
            </Select>
          </div>
          <div className="form-group">
            <label htmlFor="dateOfBirth">Ngày sinh:</label>
            <input
              type="date"
              id="dateOfBirth"
              value={userState.dateOfBirth?.slice(0, 10) ?? ''} // Provide fallback
              onChange={(e) => handleInputChange('dateOfBirth', e.target.value)}
            />
          </div>
          <div className="form-group">
            <label htmlFor="address">Địa chỉ:</label>
            <textarea
              id="address"
              value={userState.address}
              onChange={(e) => handleInputChange('address', e.target.value)}
              rows="2"
            />
          </div>
          <div className="form-group">
            <label htmlFor="email">Email:</label>
            <input
              type="email"
              id="email"
              value={userState.email}
              onChange={(e) => handleInputChange('email', e.target.value)}
              required
            />
          </div>
          <div className="form-group">
            <label htmlFor="phone">Số điện thoại:</label>
            <input
              type="text"
              id="phone"
              value={userState.phone || 'Không có số điện thoại'}
              onChange={(e) => handleInputChange('phone', e.target.value)}
            />
          </div>
          <div className="form-group">
            <label htmlFor="username">Tên đăng nhập:</label>
            <input
              type="text"
              id="username"
              value={userState.username}
              onChange={(e) => handleInputChange('username', e.target.value)}
              disabled
            />
          </div>
          <div>
            <button type="submit">Lưu thay đổi</button>
          </div>
        </form>
      </div>
    </div>
  )
}
