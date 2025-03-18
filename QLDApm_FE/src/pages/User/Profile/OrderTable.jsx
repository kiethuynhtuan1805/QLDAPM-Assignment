export default function OrderTable({ items, total, onPay }) {
  return (
    <div className="OrderTable">
      <h2>Đơn hàng của tôi</h2>
      <table>
        <thead>
          <tr>
            <th>Món ăn</th>
            <th>Số lượng</th>
            <th>Giá</th>
          </tr>
        </thead>
        <tbody>
          {items.map((item, index) => (
            <tr key={index}>
              <td>{item.name}</td>
              <td>{item.quantity}</td>
              <td>{item.price}</td>
            </tr>
          ))}
        </tbody>
      </table>
      <div className="total">
        <strong>Tổng cộng: {total}</strong>
      </div>
      <button onClick={onPay}>Thanh toán</button>
    </div>
  )
}
