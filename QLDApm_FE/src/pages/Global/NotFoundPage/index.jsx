import './NotFoundPage.scss'
import { UserFooter, NavBar } from 'components'

export default function NotFoundPage() {
  return (
    <div className="NotFoundPage">
      <div className="container-wrapper">
        <div style={{ position: 'relative', height: '80px' }}>
          <NavBar page="Home" />
        </div>
        <img src={require('assets/images/404.png')} alt="" />
        <UserFooter />
      </div>
    </div>
  )
}
