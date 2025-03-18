package vn.restaurant.management.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import vn.restaurant.management.dto.request.DishRequest;
import vn.restaurant.management.dto.response.DishResponse;
import vn.restaurant.management.entity.Category;
import vn.restaurant.management.entity.Dish;
import vn.restaurant.management.entity.Images;
import vn.restaurant.management.exception.category.CategoryNotFoundException;
import vn.restaurant.management.exception.dish.DishNotFoundException;
import vn.restaurant.management.repository.CategoryRepository;
import vn.restaurant.management.repository.DishRepository;
import vn.restaurant.management.repository.ImagesRepository;
import vn.restaurant.management.service.DishService;
import vn.restaurant.management.utils.DateUtils;
import vn.restaurant.management.utils.GeneralUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DishServiceImpl implements DishService {

    @Autowired
    CategoryRepository categoryRepository;

    @Autowired
    DishRepository dishRepository;

    @Autowired
    ImagesRepository imagesRepository;

    @Autowired
    private S3FileUploadServiceImpl s3FileUploadServiceImpl;

    @Override
    /**
     * Lấy thông tin một món ăn theo ID.
     *
     * @param id ID của món ăn cần lấy.
     * @return Đối tượng DishResponse chứa thông tin của món ăn.
     * @throws DishNotFoundException Nếu không tìm thấy món ăn với ID đã cho.
     */
    public List<DishResponse> getAllDishes() throws DishNotFoundException {
        Map<String, String> categories = getCategoryMap();

        List<Dish> dishes = dishRepository.findAll();
        if(dishes.isEmpty()){
            throw new DishNotFoundException("Không có dữ liệu trong danh sách");
        }

        return dishes.stream()
                .map(dish -> DishResponse.builder()
                        .dishId(dish.getDishId())
                        .categoryId(dish.getCategoryId())
                        .categoryName(categories.get(dish.getCategoryId()))
                        .dishName(dish.getName())
                        .description(dish.getDescription())
                        .price(dish.getPrice())
                        .image(dish.getImage())
                        .available(dish.isAvailable())
                        .rating(dish.getRating())
                        .classify(dish.getClassify())
                        .subImages(getSubImagesByDishId(dish.getDishId())) // Sử dụng hàm mới
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    /**
     * Lấy thông tin một món ăn theo ID.
     *
     * @param id ID của món ăn cần lấy.
     * @return Đối tượng DishResponse chứa thông tin của món ăn.
     * @throws DishNotFoundException Nếu không tìm thấy món ăn với ID đã cho.
     */
    public DishResponse getDishById(String id) throws DishNotFoundException {
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new DishNotFoundException("Không tìm thấy món ăn id: " + id));

        return DishResponse.builder()
                .dishId(dish.getDishId())
                .categoryId(dish.getCategoryId())
                .categoryName(getCategoryMap().get(dish.getCategoryId())) // Lấy tên danh mục
                .dishName(dish.getName())
                .description(dish.getDescription())
                .price(dish.getPrice())
                .image(dish.getImage())
                .available(dish.isAvailable())
                .rating(dish.getRating())
                .classify(dish.getClassify())
                .subImages(getSubImagesByDishId(dish.getDishId()))
                .build();
    }

    @Override
    /**
     * Lưu một món ăn mới vào cơ sở dữ liệu.
     *
     * @param request Đối tượng DishRequest chứa thông tin của món ăn cần lưu.
     * @return Đối tượng DishResponse chứa thông tin của món ăn đã lưu.
     * @throws IOException Nếu có lỗi xảy ra trong quá trình tải lên tệp hình ảnh.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public DishResponse saveDish(DishRequest request) throws IOException, DishNotFoundException {

        if(!categoryRepository.existsById(request.getCategoryId())){
            throw new DishNotFoundException("Category not found");
        }

        // Tạo đối tượng Dish từ DishRequest
        Dish dish = Dish.builder()
                .dishId(GeneralUtils.generateId())
                .categoryId(request.getCategoryId())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .image((request.getImage() != null && !request.getImage().isEmpty())
                        ? s3FileUploadServiceImpl.uploadFile(request.getImage()) // Tải lên tệp hình ảnh nếu có
                        : null) // Giả định lưu tên tệp hình ảnh
                .available(request.isAvailable())
                .rating(request.getRating())
                .classify(request.getClassify())
                .dateCreated(DateUtils.getCurrentDateTime())
                .build();

        // Lưu đối tượng Dish vào kho dữ liệu
        Dish savedDish = dishRepository.save(dish);

        // Trả về đối tượng DishResponse
        return DishResponse.builder()
                .dishId(savedDish.getDishId())
                .categoryId(savedDish.getCategoryId())
                .categoryName(getCategoryMap().get(savedDish.getCategoryId())) // Lấy tên danh mục
                .dishName(savedDish.getName())
                .description(savedDish.getDescription())
                .price(savedDish.getPrice())
                .image(savedDish.getImage())
                .available(savedDish.isAvailable())
                .rating(savedDish.getRating())
                .classify(savedDish.getClassify())
                //.subImages(getSubImagesByDishId(savedDish.getDishId())) // Lấy hình ảnh liên quan
                .build();
    }


    @Override
    /**
     * Cập nhật thông tin của một món ăn.
     *
     * @param id ID của món ăn cần cập nhật.
     * @param request Đối tượng DishRequest chứa thông tin mới cho món ăn.
     * @return Đối tượng DishResponse chứa thông tin của món ăn đã cập nhật.
     * @throws DishNotFoundException Nếu không tìm thấy món ăn với ID đã cho.
     * @throws IOException Nếu có lỗi xảy ra trong quá trình tải lên tệp hình ảnh.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public DishResponse updateDish(String id,DishRequest request) throws DishNotFoundException, IOException {
        // Tìm món ăn theo ID
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new DishNotFoundException("Không tìm thấy món ăn id: " + id));

        // Cập nhật thông tin món ăn
        dish.setCategoryId(request.getCategoryId());
        dish.setName(request.getName());
        dish.setDescription(request.getDescription());
        dish.setPrice(request.getPrice());
        dish.setImage((request.getImage() != null && !request.getImage().isEmpty())
                ? s3FileUploadServiceImpl.uploadFile(request.getImage()) // Tải lên tệp hình ảnh nếu có
                : null); // Giả định lưu tên tệp hình ảnh
        dish.setAvailable(request.isAvailable());
        dish.setRating(request.getRating());
        dish.setClassify(request.getClassify());
        dish.setDateUpdated(DateUtils.getCurrentDateTime());

        // Lưu lại đối tượng Dish đã cập nhật
        Dish updatedDish = dishRepository.save(dish);

        // Trả về đối tượng DishResponse
        return DishResponse.builder()
                .dishId(updatedDish.getDishId())
                .categoryId(updatedDish.getCategoryId())
                .categoryName(getCategoryMap().get(updatedDish.getCategoryId())) // Lấy tên danh mục
                .dishName(updatedDish.getName())
                .description(updatedDish.getDescription())
                .price(updatedDish.getPrice())
                .image(updatedDish.getImage())
                .available(updatedDish.isAvailable())
                .rating(updatedDish.getRating())
                .classify(updatedDish.getClassify())
                .subImages(getSubImagesByDishId(updatedDish.getDishId())) // Lấy hình ảnh liên quan
                .build();
    }



    @Override
    /**
     * Xóa một món ăn theo ID.
     *
     * @param id ID của món ăn cần xóa.
     * @throws DishNotFoundException Nếu không tìm thấy món ăn với ID đã cho.
     */
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteDish(String id) throws DishNotFoundException {
        // Kiểm tra xem món ăn có tồn tại hay không
        Dish dish = dishRepository.findById(id)
                .orElseThrow(() -> new DishNotFoundException("Không tìm thấy món ăn id: " + id));

        // Nếu có, thực hiện xóa món ăn
        dishRepository.delete(dish);
    }

    /**
     * Lấy bản đồ các ID danh mục với tên danh mục tương ứng.
     *
     * Phương thức này truy xuất tất cả các danh mục từ kho danh mục và
     * xây dựng một bản đồ mà trong đó khóa là ID danh mục và giá trị là
     * tên danh mục. Điều này hữu ích cho việc tra cứu nhanh tên danh mục
     * dựa trên ID khi xử lý các món ăn.
     *
     * @return một Map chứa các ID danh mục là khóa và tên danh mục là giá trị.
     */
    private Map<String, String> getCategoryMap() {
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .collect(Collectors.toMap(Category::getCategoryId, Category::getName));
    }

    /**
     * Lấy danh sách các hình ảnh liên quan đến một ID món ăn cụ thể.
     *
     * Phương thức này truy vấn kho hình ảnh để lấy tất cả các hình ảnh
     * liên quan đến ID món ăn đã cho. Sau đó, nó ánh xạ mỗi hình ảnh
     * thành một đối tượng ImagesResponse chứa ID hình ảnh, liên kết,
     * và ID món ăn tương ứng. Điều này hữu ích cho việc cung cấp thông
     * tin chi tiết về hình ảnh khi hiển thị thông tin món ăn.
     *
     * @param dishId ID của món ăn mà cần lấy hình ảnh liên quan.
     * @return một danh sách các đối tượng ImagesResponse đại diện cho
     * các hình ảnh của món ăn được chỉ định.
     */
    private List<Images> getSubImagesByDishId(String dishId) {
        log.info("Fetching images for dishId: {}", dishId);
        List<Images> subImages = imagesRepository.findByDishId(dishId).stream()
                .map(image -> Images.builder()
                        .id(image.getId())
                        .linkImg(image.getLinkImg())
                        .dishId(image.getDishId())
                        .build())
                .collect(Collectors.toList());

        // Nếu danh sách trống, trả về null
        return subImages.isEmpty() ? null : subImages;
    }

}
