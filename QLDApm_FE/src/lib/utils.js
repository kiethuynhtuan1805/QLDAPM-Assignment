export const formatTime = (timeString) => {
  const date = new Date(timeString)
  const hours = date.getHours()
  const minutes = date.getMinutes().toString().padStart(2, '0')
  const period = hours >= 12 ? 'PM' : 'AM'
  const formattedTime = `${hours % 12 || 12}:${minutes}${period}`

  const options = { day: '2-digit', month: 'short', year: 'numeric' }
  const formattedDate = date.toLocaleDateString('en-GB', options)

  return { formattedTime, formattedDate }
}

export const getRoleId = (role) => {
  switch (role) {
    case 'ADMIN':
      return '2024091800sc77P4Lk'
    case 'STAFF':
      return '2024091839I+7zoU6I'
    case 'CUSTOMER':
      return '20240925440xjFbMbx'
    default:
      return ''
  }
}
