package vn.restaurant.management.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import vn.restaurant.management.dto.request.TablesRequest;
import vn.restaurant.management.dto.response.TablesResponse;
import vn.restaurant.management.entity.Tables;
import vn.restaurant.management.repository.TableRepository;
import vn.restaurant.management.service.TableService;
import vn.restaurant.management.utils.GeneralUtils;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class TableServiceImpl implements TableService {

    @Autowired
    private TableRepository tableRepository;

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public TablesResponse createTable(TablesRequest request) {
        Tables table = new Tables();
        table.setTableId(GeneralUtils.generateId());
        table.setName(request.getName());
        table.setCapacity(request.getCapacity());
        table.setArea(request.getArea());
        table.setStatus(request.getStatus());

        Tables savedTable = tableRepository.save(table);
        return convertToResponse(savedTable);
    }

    @Override
    public List<TablesResponse> getAllTables() {
        List<Tables> tables = tableRepository.findAll();
        return tables.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public TablesResponse getTableById(String tableId) {
        return tableRepository.findById(tableId)
                .map(this::convertToResponse)
                .orElseThrow(() -> new NoSuchElementException("Table with ID " + tableId + " not found"));
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public TablesResponse updateTable(String tableId, TablesRequest request) throws Exception {
        Optional<Tables> tableOptional = tableRepository.findById(tableId);

        if (tableOptional.isPresent()) {
            Tables table = tableOptional.get();
            table.setName(request.getName());
            table.setCapacity(request.getCapacity());
            table.setArea(request.getArea());
            table.setStatus(request.getStatus());

            Tables updatedTable = tableRepository.save(table);
            return convertToResponse(updatedTable);
        }
        throw new Exception("Table not found with ID: " + tableId);
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF')")
    public void deleteTable(String tableId) throws Exception {
        if (tableRepository.existsById(tableId)) {
            tableRepository.deleteById(tableId);
        } else {
            throw new Exception("Table not found with ID: " + tableId);
        }
    }

    @Override
    public Boolean getStatusForToday(String tableId) throws Exception {
        LocalDate today = LocalDate.now();
        return tableRepository.findById(tableId)
                .map(Tables::getStatus)
                .orElseThrow(() -> new Exception("Table not found with ID: " + tableId));
    }

    private TablesResponse convertToResponse(Tables table) {
        return TablesResponse.builder()
                .tableId(table.getTableId())
                .name(table.getName())
                .capacity(table.getCapacity())
                .area(table.getArea())
                .status(table.getStatus())
                .build();
    }

    @Override
    public List<TablesResponse> getAvailableTablesByDate(Date reservationDate) {
        // Tính thời gian đầu ngày (00:00:00) của reservationDate
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reservationDate);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        Date startOfDay = calendar.getTime();

        // Tính thời gian cuối ngày (23:59:59) của reservationDate
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        Date endOfDay = calendar.getTime();

        List<Tables> availableTables = tableRepository.findAvailableTablesByDate(startOfDay, endOfDay);

        // Chuyển đổi danh sách `Tables` thành `TablesResponse`
        return availableTables.stream()
                .map(table -> TablesResponse.builder()
                        .tableId(table.getTableId())
                        .name(table.getName())
                        .capacity(table.getCapacity())
                        .status(table.getStatus())
                        .area(table.getArea())
                        .build())
                .collect(Collectors.toList());
    }
}
