import axios from 'axios'

// Create an axios instance
const httpClient = axios.create({
  baseURL: 'https://htk.sflavor-demo-app-backend.io.vn/api/v1', // Base URL for the API
  timeout: 10000, // Request timeout (optional)
})

// Request interceptor to attach token
httpClient.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken') // Retrieve token from localStorage
    if (token) {
      config.headers['Authorization'] = `Bearer ${token}` // Attach token as a Bearer token
    }
    if (config.data instanceof FormData) {
      config.headers['Content-Type'] = 'multipart/form-data'
    } else {
      // Default to JSON content type
      config.headers['Content-Type'] = 'application/json'
    }
    return config
  },
  (error) => {
    return Promise.reject(error)
  }
)

// Response interceptor to handle responses and errors
httpClient.interceptors.response.use(
  (response) => {
    return response.data // Return the response data directly
  },
  (error) => {
    // Handle errors globally
    if (error.response) {
      console.error(`API Error: ${error.response.status} - ${error.response.statusText}`)
      return Promise.reject(error.response.data) // Return the error response data
    } else if (error.request) {
      console.error('API Error: No response received')
      return Promise.reject({ message: 'No response from server' })
    } else {
      console.error(`API Error: ${error.message}`)
      return Promise.reject({ message: error.message })
    }
  }
)

export default httpClient
