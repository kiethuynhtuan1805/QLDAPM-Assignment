package vn.restaurant.management.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.restaurant.management.constant.Common;
import vn.restaurant.management.dto.request.TablesRequest;
import vn.restaurant.management.dto.response.DataResponse;
import vn.restaurant.management.dto.response.TablesResponse;
import vn.restaurant.management.service.TableService;
import vn.restaurant.management.utils.PrintUtils;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

@CrossOrigin("*")
@RestController
@Slf4j
@RequestMapping(Common.API_PATH + Common.API_VERSION)
public class TableRestController {

    @Autowired
    private TableService tablesService;

    /**
     * Hiển thị danh sách bàn
     * @return danh sách các bàn
     */
    @GetMapping("/tables")
    private DataResponse<List<TablesResponse>> getAllTables() {
        DataResponse<List<TablesResponse>> response;
        try {
            List<TablesResponse> tables = tablesService.getAllTables();
            if (tables == null || tables.isEmpty()) {
                response = DataResponse.failResponse("Tables have no data");
            } else {
                response = DataResponse.successResponse(tables, "");
            }
        } catch (Exception ex) {
            log.error("Get all tables failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    /**
     * Hiển thị thông tin bàn theo ID
     * @param id ID của bàn
     * @return thông tin bàn
     */
    @GetMapping("/table/{id}")
    private DataResponse<TablesResponse> getTableById(@PathVariable String id) {
        DataResponse<TablesResponse> response;
        try {
            TablesResponse table = tablesService.getTableById(id);
            if (table == null) {
                response = DataResponse.failResponse("Table not found");
            } else {
                response = DataResponse.successResponse(table, "");
            }
        } catch (Exception ex) {
            log.error("Get table by id failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    /**
     * Tạo bàn mới
     * @param tablesRequest yêu cầu tạo bàn mới
     * @return thông tin bàn được tạo
     */
    @PostMapping("/table")
    private DataResponse<TablesResponse> createTable(@RequestBody TablesRequest tablesRequest) {
        DataResponse<TablesResponse> response;
        try {
            PrintUtils.print(tablesRequest);
            TablesResponse table = tablesService.createTable(tablesRequest);
            response = DataResponse.successResponse(table, "Table created successfully");
        } catch (Exception ex) {
            log.error("Create table failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    /**
     * Cập nhật thông tin bàn theo ID
     * @param id ID của bàn
     * @param tablesRequest yêu cầu cập nhật bàn
     * @return thông tin bàn đã được cập nhật
     */
    @PutMapping("/table/{id}")
    private DataResponse<TablesResponse> updateTable(@PathVariable String id, @RequestBody TablesRequest tablesRequest) {
        DataResponse<TablesResponse> response;
        try {
            if (id == null || id.isEmpty()) {
                throw new NoSuchElementException("Not found table with id");
            }
            PrintUtils.print(tablesRequest);
            TablesResponse updatedTable = tablesService.updateTable(id, tablesRequest);
            response = DataResponse.successResponse(updatedTable, "Table updated successfully");
        } catch (Exception ex) {
            log.error("Update table failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    /**
     * Xóa bàn theo ID
     * @param id ID của bàn cần xóa
     * @return kết quả xóa bàn
     */
    @DeleteMapping("/table/{id}")
    private DataResponse<Void> deleteTable(@PathVariable("id") String id) {
        DataResponse<Void> response;
        try {
            if (id == null || id.isEmpty()) {
                throw new NoSuchElementException("Not found table with id");
            }
            tablesService.deleteTable(id);
            response = DataResponse.successResponse("Table deleted successfully");
        } catch (Exception ex) {
            log.error("Delete table failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @GetMapping("/table/available/today")
    public DataResponse<List<TablesResponse>> getAvailableTablesForToday() {
        // Lấy ngày hiện tại với thời gian 00:00:00
        LocalDate localDate = LocalDate.now();
        Date today = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());

        DataResponse<List<TablesResponse>> response;
        try {
            List<TablesResponse> availableTables = tablesService.getAvailableTablesByDate(today);
            response = DataResponse.successResponse(availableTables, "Danh sách bàn trống cho ngày hôm nay");
        } catch (Exception ex) {
            log.error("Get available tables for today failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }
}
