package vn.restaurant.management.controller.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import vn.restaurant.management.constant.Common;
import vn.restaurant.management.dto.request.ImagesRequest;
import vn.restaurant.management.dto.response.DataResponse;
import vn.restaurant.management.entity.Images;
import vn.restaurant.management.exception.images.ImagesNotFoundException;
import vn.restaurant.management.service.ImageService;
import vn.restaurant.management.utils.PrintUtils;

import java.util.List;

@CrossOrigin("*")
@RestController
@Slf4j
@RequestMapping(Common.API_PATH + Common.API_VERSION)
public class SubImageRestController {
    @Autowired
    ImageService imageService;

    @GetMapping("/subimages")
    private DataResponse<List<Images>> getSubImages() {
        DataResponse<List<Images>> response = new DataResponse<>();
        try {
            List<Images> imgs = imageService.getImages();
            if (imgs == null && imgs.size() <= 0) {
                throw new ImagesNotFoundException("Không có dữ liệu");
            }
            response = DataResponse.successResponse(imgs,"");
        }catch (Exception ex) {
            log.error("Get all images failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @GetMapping("/subimage/{id}")
    private DataResponse<Images> getSubImage(@PathVariable String id) {
        DataResponse<Images> response = new DataResponse<>();
        try {
            Images img = imageService.getImageById(id);
            if (img == null) {
                throw new ImagesNotFoundException("Không có dữ liệu");
            }
            response = DataResponse.successResponse(img,"");
        }catch (Exception ex) {
            log.error("Get image failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @GetMapping("/subimages/dish/{dishId}")
    private DataResponse<List<Images>> getSubImagesDish(@PathVariable String dishId) {
        DataResponse<List<Images>> response = new DataResponse<>();
        try {
            List<Images> imgs = imageService.getImagesByDishId(dishId);
            if (imgs == null && imgs.size() == 0) {
                response = DataResponse.successResponse(imgs,"Không có dữ liệu hình ảnh");
            }
            response = DataResponse.successResponse(imgs,"Hình ảnh của sản phẩm: " +dishId);
        }catch (Exception ex) {
            log.error("Get all images failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @PostMapping("/subimage")
    private DataResponse<Images> addSubImage(@ModelAttribute ImagesRequest request) {
        DataResponse<Images> response = new DataResponse<>();
        try {
            PrintUtils.print(request);
            Images images = imageService.saveImage(request);
            response = DataResponse.successResponse(images,"Thêm ảnh thành công!");
        }catch (Exception ex) {
            log.error("Add image failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @PutMapping("/subimage/{id}")
    private DataResponse<Images> updateSubImage(@PathVariable String id, @ModelAttribute ImagesRequest request) {
        DataResponse<Images> response = new DataResponse<>();
        try{
            PrintUtils.print(request);
            Images images = imageService.updateImage(id, request);
            response = DataResponse.successResponse(images,"Update image successfully!");
        }catch (Exception ex) {
            log.error("Update image failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }

    @DeleteMapping("/subimage/{id}")
    private DataResponse<Images> deleteSubImage(@PathVariable String id) {
        DataResponse<Images> response = new DataResponse<>();
        try {
            PrintUtils.print(id);
            imageService.deleteImage(id);
            response = DataResponse.successResponse("Delete image successfully!");
        }catch (Exception ex) {
            log.error("Delete image failed, cause={}", ex.getMessage());
            response = DataResponse.failResponse(ex.getMessage());
        }
        return response;
    }
}
