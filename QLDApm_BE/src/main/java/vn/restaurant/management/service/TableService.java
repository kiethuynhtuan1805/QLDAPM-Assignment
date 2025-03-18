package vn.restaurant.management.service;

import vn.restaurant.management.dto.request.TablesRequest;
import vn.restaurant.management.dto.response.TablesResponse;
import vn.restaurant.management.entity.Tables;

import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface TableService {

    TablesResponse createTable(TablesRequest request);

    List<TablesResponse> getAllTables();

    TablesResponse getTableById(String tableId);

    TablesResponse updateTable(String tableId, TablesRequest request) throws Exception;

    void deleteTable(String tableId) throws Exception;

    Boolean getStatusForToday(String tableId) throws Exception;

    List<TablesResponse> getAvailableTablesByDate(Date reservationDate);
}
