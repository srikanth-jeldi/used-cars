CREATE TABLE car_images (
                            id BIGINT PRIMARY KEY AUTO_INCREMENT,
                            car_id BIGINT NOT NULL,
                            url VARCHAR(1000) NOT NULL,
                            category VARCHAR(50) NOT NULL,
                            sort_order INT NOT NULL DEFAULT 0,
                            is_primary BOOLEAN NOT NULL DEFAULT FALSE,
                            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

                            CONSTRAINT fk_car_images_car FOREIGN KEY (car_id) REFERENCES cars(id) ON DELETE CASCADE
);

CREATE INDEX idx_car_images_car_id ON car_images(car_id);
CREATE INDEX idx_car_images_category ON car_images(category);
