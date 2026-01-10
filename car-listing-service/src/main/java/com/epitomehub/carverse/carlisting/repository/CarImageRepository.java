package com.epitomehub.carverse.carlisting.repository;

import com.epitomehub.carverse.carlisting.entity.CarImage;
import com.epitomehub.carverse.carlisting.entity.CarImageCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import com.epitomehub.carverse.carlisting.entity.CarImageType;

public interface CarImageRepository extends JpaRepository<CarImage, Long> {

    Optional<CarImage> findByIdAndCar_Id(Long id, Long carId);

    List<CarImage> findByCar_IdOrderByPrimaryDescSortOrderAscIdAsc(Long carId);
    List<CarImage> findByCarIdAndImageTypeOrderBySortOrderAscIdAsc(Long carId, CarImageType imageType);

    Optional<CarImage> findTopByCar_IdOrderByPrimaryDescSortOrderAscIdAsc(Long carId);

    long countByCar_Id(Long carId);

    long countByCar_IdAndCategory(Long carId, CarImageCategory category);

    boolean existsByCar_IdAndPrimaryTrue(Long carId);
    List<CarImage> findByCarIdAndImageTypeOrderByIdAsc(Long carId, CarImageType imageType);
    @Modifying
    @Query("update CarImage i set i.primary = false where i.car.id = :carId")
    int clearPrimaryForCar(Long carId);

    @Query("select count(i) from CarImage i where i.car.id = :carId and i.primary = true")
    long countPrimaryByCarId(Long carId);

    @Query("""
        select i.url
        from CarImage i
        where i.car.id = :carId
        order by i.primary desc, i.sortOrder asc, i.id asc
    """)
    String findThumbnailUrl(Long carId);
}
