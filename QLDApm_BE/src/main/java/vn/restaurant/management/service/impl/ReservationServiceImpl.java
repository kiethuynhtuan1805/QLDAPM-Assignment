package vn.restaurant.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import vn.restaurant.management.config.UserContext;
import vn.restaurant.management.dto.request.ReservationRequest;
import vn.restaurant.management.dto.request.ReservationsByDateRangeRequest;
import vn.restaurant.management.dto.response.ReservationResponse;
import vn.restaurant.management.entity.*;
import vn.restaurant.management.repository.*;
import vn.restaurant.management.service.ReservationService;
import vn.restaurant.management.utils.DateUtils;
import vn.restaurant.management.utils.GeneralUtils;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationServiceImpl implements ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private TableRepository tableRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private InvoiceRepository invoiceRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserContext userContext;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    @PreAuthorize("isAuthenticated() or hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ReservationResponse createReservation(ReservationRequest request) {
        // Bước 1: Xác minh sự tồn tại của bàn
        validateTableExistence(request.getTableId());

        validateReservationTimeConflict(request.getTableId(), request.getReservationTime());

        // Bước 2: Tạo Reservation từ thông tin trong request
        Reservation reservation = buildReservationFromRequest(request);
        Reservation savedReservation = reservationRepository.save(reservation);

        // Bước 3: Cập nhật trạng thái bàn (đánh dấu là đã đặt)
        // updateTableStatus(request.getTableId(), false);

        // Bước 4: Tạo Order liên kết với Reservation
        Orders order = Orders.builder()
                .orderId(GeneralUtils.generateId())
                .userId(userContext.getUserId())
                .reservationId(savedReservation.getReservationId())
                .orderDate(DateUtils.getCurrentDateTime())
                .totalPrice(BigDecimal.valueOf(0))
                .status("pending")
                .build();
        orderRepository.save(order);

        // Tạo Payment
        Payment payment = Payment.builder()
                .paymentId(GeneralUtils.generateId())
                .orderId(order.getOrderId())
                .userId(userContext.getUserId())
                .paymentMethod(null)
                .paymentStatus("pending")
                .amountPaid(BigDecimal.valueOf(0))
                .paymentDate(DateUtils.getCurrentDateTime())
                .build();
        paymentRepository.save(payment);

        // Tạo hóa đơn
        Invoice invoice = Invoice.builder()
                .invoiceId(GeneralUtils.generateId())
                .orderId(order.getOrderId())
                .paymentId(payment.getPaymentId())
                .totalAmount(BigDecimal.valueOf(0))
                .invoiceDate(DateUtils.getCurrentDateTime())
                .build();
        invoiceRepository.save(invoice);

        // Trả về thông tin Reservation cùng với orderId để tiếp tục các bước tiếp theo
        return buildReservationResponse(savedReservation);
    }


    @Override
    public ReservationResponse getReservationById(String reservationId) {
        Reservation reservation = reservationRepository.getReferenceById(reservationId);
        return buildReservationResponse(reservation);
    }

    @Override
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll().stream()
                .map(this::buildReservationResponse)
                .collect(Collectors.toList());
    }

    @Override
    @PreAuthorize("isAuthenticated() or hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public ReservationResponse updateReservation(String reservationId, ReservationRequest request) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new IllegalArgumentException("Reservation ID không tồn tại."));

        validateTableExistence(request.getTableId());

        updateReservationFields(reservation, request);

        Reservation updatedReservation = reservationRepository.save(reservation);

        return buildReservationResponse(updatedReservation);
    }

    @Override
    @PreAuthorize("isAuthenticated() or hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')")
    public boolean deleteReservation(String reservationId) {
        if (reservationRepository.existsById(reservationId)) {
            // Lấy thông tin Reservation trước khi xóa
            Reservation reservation = reservationRepository.getReferenceById(reservationId);

            // Xóa Reservation
            reservationRepository.deleteById(reservationId);

            // Cập nhật trạng thái của Table thành true
            Tables table = tableRepository.getReferenceById(reservation.getTableId());
            table.setStatus(true);
            tableRepository.save(table);

            return true;
        }
        return false;
    }

    @Override
    public List<ReservationResponse> getReservationsByDate(ReservationsByDateRangeRequest request) {
        SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // Chuyển đổi startDate và endDate từ chuỗi thành đối tượng Date với giờ, phút, giây
            Date startDate = DATE_FORMAT.parse(request.getStartDate());
            Date endDate = DATE_FORMAT.parse(request.getEndDate());

            // Kiểm tra nếu startDate lớn hơn endDate
            validateDateRange(startDate, endDate);

            // Lấy danh sách đặt chỗ trong khoảng thời gian
            List<Reservation> reservations = reservationRepository.findByReservationTimeBetween(startDate, endDate);

            // Chuyển đổi danh sách Reservation thành ReservationResponse
            return reservations.stream()
                    .map(this::buildReservationResponse)
                    .collect(Collectors.toList());

        } catch (ParseException e) {
            throw new IllegalArgumentException("Định dạng ngày không hợp lệ. Vui lòng sử dụng định dạng yyyy-MM-dd HH:mm:ss.");
        }
    }


    private void validateTableExistence(String tableId) {
        // Kiểm tra xem bàn có tồn tại không
        Tables table = tableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Table ID không tồn tại."));
    }

    private void validateReservationTimeConflict(String tableId, Date requestedTime) {
        // Lấy danh sách các đặt chỗ hiện tại của bàn
        List<Reservation> existingReservations = reservationRepository.findByTableId(tableId);

        // Kiểm tra xem thời gian đặt bàn có trùng với bất kỳ đặt chỗ nào hiện có
        for (Reservation reservation : existingReservations) {
            if (reservation.getReservationTime().equals(requestedTime)) {
                throw new IllegalArgumentException("Thời gian đặt bàn đã bị trùng với một đặt chỗ khác.");
            }
        }
    }

    private void updateTableStatus(String tableId, boolean status) {
        Tables table = tableRepository.getReferenceById(tableId);
        table.setStatus(status);
        tableRepository.save(table);
    }

    private Reservation buildReservationFromRequest(ReservationRequest request) {
        return Reservation.builder()
                .reservationId(GeneralUtils.generateId())
                .userId(userContext.getUserId())
                .tableId(request.getTableId())
                .reservationTime(request.getReservationTime())
                .numberOfGuests(request.getNumberOfGuests())
                .specialRequests(request.getSpecialRequests())
                .status(request.getStatus())
                .build();
    }

    private ReservationResponse buildReservationResponse(Reservation reservation) {
        Tables table = tableRepository.getReferenceById(reservation.getTableId());
        User user = userRepository.getReferenceById(reservation.getUserId());
        Orders orders = orderRepository.findByReservationId(reservation.getReservationId());
        return ReservationResponse.builder()
                .reservationId(reservation.getReservationId())
                .userId(reservation.getUserId())
                .username(user.getUsername())
                .tableId(reservation.getTableId())
                .tableName(table.getName())
                .area(table.getArea())
                .reservationTime(reservation.getReservationTime())
                .numberOfGuests(reservation.getNumberOfGuests())
                .specialRequests(reservation.getSpecialRequests())
                .status(reservation.getStatus())
                .orders(orders)
                .build();
    }

    private void updateReservationFields(Reservation reservation, ReservationRequest request) {
        reservation.setTableId(request.getTableId());
        reservation.setNumberOfGuests(request.getNumberOfGuests());
        reservation.setSpecialRequests(request.getSpecialRequests());
        reservation.setReservationTime(request.getReservationTime());
        reservation.setStatus(request.getStatus());
    }

    private void validateDateRange(Date start, Date end) {
        if (start.after(end)) {
            throw new IllegalArgumentException("Ngày bắt đầu phải nhỏ hơn hoặc bằng ngày kết thúc.");
        }
    }
}
