import { takeEvery, call, put, all } from 'redux-saga/effects'
import axios from 'axios'
import actUser from 'redux/Action/actUser'
import { message } from 'antd'

function* getUser() {
  try {
    const accessToken = localStorage.getItem('accessToken')
    const response = yield call(
      axios.get,
      `https://htk.sflavor-demo-app-backend.io.vn/api/v1/user/info`,
      {
        headers: {
          Authorization: `bearer ${accessToken}`,
        },
      }
    )
    if (response.data.status) {
      yield put({ type: actUser.GET_USER_SUCCESS, payload: response.data })
    } else {
      message.error('Không tìm thấy thông tin người dùng!')
    }
  } catch (error) {
    yield put({ type: actUser.GET_USER_FAILURE, payload: error.message })
  }
}

export function* watchUser() {
  yield takeEvery(actUser.GET_USER, getUser)
}

export default function* userSaga() {
  yield all([watchUser()])
}
