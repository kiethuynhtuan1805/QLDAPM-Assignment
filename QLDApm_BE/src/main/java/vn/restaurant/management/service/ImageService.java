package vn.restaurant.management.service;

import vn.restaurant.management.dto.request.ImagesRequest;
import vn.restaurant.management.entity.Images;
import vn.restaurant.management.exception.dish.DishNotFoundException;
import vn.restaurant.management.exception.images.ImagesNotFoundException;

import java.io.IOException;
import java.util.List;

public interface ImageService {

    List<Images> getImages() throws ImagesNotFoundException;

    Images getImageById(String id) throws ImagesNotFoundException;

    List<Images> getImagesByDishId(String restaurantId) throws ImagesNotFoundException, DishNotFoundException;

    Images saveImage(ImagesRequest request) throws IOException, ImagesNotFoundException;

    Images updateImage(String imgId, ImagesRequest request) throws ImagesNotFoundException, IOException;

    void deleteImage(String id) throws ImagesNotFoundException;

}
